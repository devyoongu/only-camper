package dev.practice.order.interfaces.item;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.item.ItemService;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.partner.PartnerService;
import dev.practice.order.domain.user.User;
import dev.practice.order.infrastructure.aws.S3Uploader;
import dev.practice.order.infrastructure.user.UserRepository;
import dev.practice.order.infrastructure.item.ItemRepository;
import dev.practice.order.infrastructure.partner.PartnerRepository;
import dev.practice.order.interfaces.order.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("item")
@RequiredArgsConstructor
public class ItemController {

    private final PartnerService partnerService;

    private final PartnerRepository partnerRepository;
    private final ItemFacade itemFacade;

    private final ItemDtoMapper itemDtoMapper;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final S3Uploader s3Uploader;

    private final ItemReader itemReader;

    public String findPartnerToken = new String(); //heap 영역참조로 다른 쓰레드에서 접근하기 위함

    @GetMapping("/list-seller")
    public String itemListForSeller(@ModelAttribute("searchCondition") ItemSearchCondition searchCondition
            , @PageableDefault(size = 10, sort = "createdDate",direction = Sort.Direction.DESC) Pageable pageable, Model model, @LoginUser SessionUser user) {

        String byPartnerToken = findByPartnerToken(user);

        searchCondition.setPartnerToken(byPartnerToken);

        //item 객체만 쿼리 todo : 파트너 정보 추가
        Page<Item> itemPage = itemRepository.findItemAllWithDsl(searchCondition, pageable);

        //item DTO 변환 및 Lazy 객체에 대한 초기화를 통해 조회 쿼리  todo : repository>jpa에서 DTO로 받는 로직으로 변경 필요 (controller 레이어서 맞지 않는 코드)
        List<ItemDto.RegisterItemRequest> itemDtoList = itemPage.getContent().stream()
                .map(item -> new ItemDto.RegisterItemRequest(item))
                .collect(Collectors.toList());

        model.addAttribute("items", itemDtoList);
        model.addAttribute("isSeller", true);
        model.addAttribute("page", new PageDto(itemPage.getTotalElements(), pageable));
        model.addAttribute("activeNum", pageable.getPageNumber());
        return "item/itemList";
    }

    @GetMapping("/list")
    public String itemList(@ModelAttribute("searchCondition") ItemSearchCondition searchCondition
            , @PageableDefault(size = 10, sort = "createdDate",direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        //item 객체만 쿼리
        Page<Item> itemPage = itemRepository.findItemAllWithDsl(searchCondition, pageable);

        //item DTO 변환 및 Lazy 객체에 대한 초기화를 통해 조회 쿼리  todo : repository>jpa에서 DTO로 받는 로직으로 변경 필요 (controller 레이어서 맞지 않는 코드)
        List<ItemDto.RegisterItemRequest> itemDtoList = itemPage.getContent().stream()
                .map(item -> new ItemDto.RegisterItemRequest(item))
                .collect(Collectors.toList());

        model.addAttribute("items", itemDtoList);
        model.addAttribute("isSeller", false);
        model.addAttribute("page", new PageDto(itemPage.getTotalElements(), pageable));
        model.addAttribute("activeNum", pageable.getPageNumber());
        return "item/itemList";
    }

    /**아이템 등록 form*/
    @GetMapping("/add")
    public String addItemForm(Model model) {

        ItemDto.RegisterItemRequest item = new ItemDto.RegisterItemRequest();
        ItemDto.RegisterItemOptionGroupRequest optionGroup= new ItemDto.RegisterItemOptionGroupRequest();

        for (int i = 0; i < 2; i++) {
            List<ItemDto.RegisterItemOptionRequest> registerItemOptionRequests = optionGroup.addOptionList(new ItemDto.RegisterItemOptionRequest(i));
            item.addItemGroup(new ItemDto.RegisterItemOptionGroupRequest(i, null, registerItemOptionRequests));
        }

        model.addAttribute("item", item);
        return "item/addItem";
    }

    @PostMapping("/add")
    public String addItem(@Valid @ModelAttribute("item") ItemDto.RegisterItemRequest itemDto, BindingResult bindingResult, @LoginUser SessionUser user
            , RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "item/addItem";
        }

        String byPartnerToken = findByPartnerToken(user);

//        FileInfo fileInfo = itemService.fileSaveAndParsing(itemDto.getImage());

        String aStatic = s3Uploader.upload(itemDto.getImage(), "static");
        itemDto.setRepresentImagePath(aStatic);

        var itemToken = itemService.registerItem(itemDto, byPartnerToken);

        //상세 화면으로 이동
        redirectAttributes.addAttribute("itemToken", itemToken);
        return "redirect:/order/{itemToken}";
    }

    @GetMapping("/view/{itemToken}")
    public String itemView(@PathVariable String itemToken, Model model) {

        ItemInfo.Main itemInfo = itemFacade.retrieveItemInfo(itemToken);
        model.addAttribute("item", itemInfo);

        return "item/viewItem";
    }

    @GetMapping("/edit/{itemToken}")
    public String itemEditForm(@PathVariable String itemToken, Model model) {
        Item findItem = itemReader.getItemBy(itemToken);

        List<ItemInfo.ItemOptionGroupInfo> itemOptionSeries = itemReader.getItemOptionSeries(findItem);
        ItemInfo.Main item = new ItemInfo.Main(findItem, itemOptionSeries);

        model.addAttribute("item", item);

        return "item/editItem";
    }

    @PostMapping("/edit/{itemToken}")
    public String itemEdit(@PathVariable String itemToken, @Valid @ModelAttribute ItemDto.UpdateItemRequest updateDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            return "item/editItem";
        }

        if (!StringUtils.isEmpty(updateDto.getImage().getOriginalFilename())) {
            String aStatic = s3Uploader.upload(updateDto.getImage(), "static");
            updateDto.setRepresentImagePath(aStatic);
        } else {
            Item findItem = itemReader.getItemBy(itemToken);
            updateDto.setRepresentImagePath(findItem.getRepresentImagePath());
        }

        var itemCommand = itemDtoMapper.of(updateDto);
        itemService.updateItem(itemToken, updateDto);

        //상세 화면으로 이동
        redirectAttributes.addAttribute("itemToken", itemToken);
        return "redirect:/order/{itemToken}";
    }

    @GetMapping("/ispartner/{itemToken}")
    @ResponseBody
    public Boolean isPartner(@PathVariable String itemToken, @LoginUser SessionUser user) {
        ItemInfo.Main itemInfo = itemFacade.retrieveItemInfo(itemToken);

        Boolean isPartner = true;

        if (itemInfo.getPartner().getId() != user.getPartner().getId()) {
            isPartner = false;
        }

        return isPartner;
    }

    private String findByPartnerToken(SessionUser user) {
        User findUser = userRepository.getById(user.getId());
        Partner partner = partnerRepository.getById(findUser.getPartner().getId());
        String partnerToken = partner.getPartnerToken();
        return partnerToken;

       /*Optional<User> userOptional = userRepository.findById(user.getId());

       userOptional.ifPresent(findUser -> {
           Long partnerId = findUser.getPartner().getId();
           Optional<Partner> partnerOptional = partnerRepository.findById(partnerId);

           partnerOptional.ifPresent(findPartner -> {
               findPartnerToken = findPartner.getPartnerToken();
           });
       });*/
   }
}

