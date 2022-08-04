package dev.practice.order.interfaces.order;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.application.order.OrderFacade;
import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.item.*;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderInfo;
import dev.practice.order.domain.order.OrderInfoMapper;
import dev.practice.order.domain.order.OrderService;
import dev.practice.order.domain.order.fragment.DeliveryFragment;
import dev.practice.order.domain.order.gift.GiftApiCaller;
import dev.practice.order.domain.order.item.OrderItem;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.tupleDto.AggregateDto;
import dev.practice.order.domain.user.User;
import dev.practice.order.infrastructure.order.OrderRepository;
import dev.practice.order.infrastructure.order.gift.RetrofitGiftApiResponse;
import dev.practice.order.infrastructure.partner.PartnerRepository;
import dev.practice.order.infrastructure.user.UserRepository;
import dev.practice.order.interfaces.item.PageDto;
import dev.practice.order.interfaces.order.gift.GiftOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.dom4j.rule.Mode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;
    private final OrderDtoMapper orderDtoMapper;

    private final ItemReader itemReader;

    private final ItemFacade itemFacade;

    private final OrderRepository orderRepository;

    private final OrderInfoMapper orderInfoMapper;

    private final GiftApiCaller giftApiCaller;

    private final UserRepository userRepository;

    private final PartnerRepository partnerRepository;

    @ModelAttribute("deliveryFragment")
    public DeliveryFragment deliveryFragment(@LoginUser SessionUser user) {
        return DeliveryFragment.builder()
                .receiverName(user.getName())
                .receiverPhone("receiverPhone")
                .receiverZipcode("receiverZipcode")
                .receiverAddress1("receiverAddress1")
                .receiverAddress2("receiverAddress2")
                .etcMessage("etcMessage")
                .build();
    }

    @ModelAttribute("optionList")
    public List<ItemInfo.ItemOptionInfo> optionList() {
        List<ItemInfo.ItemOptionInfo> optionList = new ArrayList<>();
        return optionList;
    }

    @ModelAttribute("orderCount")
    public Long orderCount() {
        return 1L;
    }

    @ModelAttribute("pushTypes")
    public PushType[] pushTypes() {
            return PushType.values();
        }

    /**
     * 0. 주문 목록
     */
//    @GetMapping("/list")
    public String orderItemList(@ModelAttribute OrderSearchCondition condition
            , @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
            , @LoginUser SessionUser user, Model model) {

        condition.setUserId(user.getId());
        Page<Order> orderPage = orderRepository.getOrderListMine(condition, pageable);

        List<OrderInfo.Main> orderList = orderPage.getContent().stream()
                .map(o -> orderInfoMapper.of(o, o.getOrderItemList()))
                .collect(Collectors.toList());

        model.addAttribute("orderList", orderList);
        model.addAttribute("page", new PageDto(orderPage.getTotalElements(), pageable));
        model.addAttribute("activeNum", pageable.getPageNumber());
        return "order/orderList";
    }

    /**
     * 0. 주문 목록
     */
    @GetMapping("/list")
    public String orderItemList2(@ModelAttribute OrderSearchCondition condition
            , @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
            , @LoginUser SessionUser user, Model model) {

        condition.setUserId(user.getId());
        Page<AggregateDto.OrderItemListDto> orderPage = orderRepository.getOrderListMine2(condition, pageable);
        List<AggregateDto.OrderItemListDto> content = orderPage.getContent();

        model.addAttribute("orderList", content);
        model.addAttribute("page", new PageDto(orderPage.getTotalElements(), pageable));
        model.addAttribute("activeNum", pageable.getPageNumber());
        return "order/orderList";
    }

    /**
     * 1. 상품주문- Form
     */
    @GetMapping("/{itemToken}")
    public String orderItemForm(@PathVariable String itemToken, Model model, @LoginUser SessionUser user) {

        Item item = itemReader.getItemBy(itemToken);
        List<ItemInfo.ItemOptionGroupInfo> itemOptionSeries = itemReader.getItemOptionSeries(item);

        List<OrderDto.RegisterOrderItemOptionGroupRequest> optionGroupList = itemOptionSeries.stream()
                .map(og -> new OrderDto.RegisterOrderItemOptionGroupRequest(og.getOrdering(), og.getItemOptionGroupName(), null))
                .collect(Collectors.toList());

        OrderDto.RegisterOrderItem itemDto = new OrderDto.RegisterOrderItem(item);
        itemDto.setOrderItemOptionGroupList(optionGroupList);

        GiftOrderDto.RegisterOrderRequest giftDto = new GiftOrderDto.RegisterOrderRequest();
        Partner partner = partnerRepository.getById(item.getPartner().getId());


        ItemInfo.Main itemInfo = itemFacade.retrieveItemInfo(itemToken);

        Boolean isPartner = true;
        if (itemInfo.getPartner().getId() != user.getPartner().getId()) {
            isPartner = false;
        }

        //todo:dto로 변환
        List<User> users = userRepository.findAll();

        model.addAttribute("item", itemDto);
        model.addAttribute("gift", giftDto);
        model.addAttribute("users", users);
        model.addAttribute("partner", partner);
        model.addAttribute("isPartner", isPartner);

        return "order/addOrder";
    }

    /**
     * 주문등록
     */
    @PostMapping("/{itemToken}")
    public String orderItem(@Valid @ModelAttribute("item") OrderDto.RegisterOrderItem orderItem, BindingResult bindingResult
            ,@ModelAttribute("deliveryFragment") DeliveryFragment deliveryFragment
            ,@PathVariable String itemToken,@LoginUser SessionUser user, @RequestParam("payMethod") String payMethod
                            ,Model model
    ) {
        //검증 로직
        if (orderItem.getOptionGroupOrdering() != null && orderItem.getOptionGroupOrdering() != -1) { //그룹이 없는 아이템의 경우
            if (orderItem.getOptionOrdering() == null) {
                bindingResult.addError(new FieldError("item", "optionOrdering", "옵션 그룹 및 옵션을 선택해 주세요."));
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);

            Item item = itemReader.getItemBy(itemToken);
            Partner partner = partnerRepository.getById(item.getPartner().getId());
            Boolean isPartner = true;
            if (item.getPartner().getId() != user.getPartner().getId()) {
                isPartner = false;
            }

            List<User> users = userRepository.findAll();
            model.addAttribute("gift", new GiftOrderDto.RegisterOrderRequest());
            model.addAttribute("users", users);
            model.addAttribute("partner", partner);
            model.addAttribute("isPartner", isPartner);

            return "order/addOrder";
        }

        OrderDto.RegisterOrderRequest orderDto = getOrderDto(itemToken, orderItem.getOrderCount(), orderItem.getOptionGroupOrdering(), orderItem.getOptionOrdering());

        //todo : 최초 form에서 orderDto에 전달
        orderDto.setUserId(user.getId());
        orderDto.setPayMethod(payMethod);
        orderDto.setReceiverName(deliveryFragment.getReceiverName());
        orderDto.setReceiverPhone(deliveryFragment.getReceiverPhone());
        orderDto.setReceiverZipcode(deliveryFragment.getReceiverZipcode());
        orderDto.setReceiverAddress1(deliveryFragment.getReceiverAddress1());
        orderDto.setReceiverAddress2(deliveryFragment.getReceiverAddress2());
        orderDto.setEtcMessage(deliveryFragment.getEtcMessage());

        var orderCommand = orderDtoMapper.of(orderDto);
        var orderToken = orderFacade.registerOrder(orderCommand);

        return "redirect:/order/list";
    }

    @GetMapping("/gift-list")
    public String giftList(@LoginUser SessionUser user, Model model,@ModelAttribute OrderSearchCondition condition) {

        List<RetrofitGiftApiResponse.Gift> gifts = giftApiCaller.giftList(user.getId(), null);

        //todo : getItemName() exception 처리
        List<giftResponse> giftList = gifts.stream()
                .map(gift ->
                        {try {
                            return new giftResponse(
                                    gift.getGiftToken()
                                    , gift.getBuyerUserId() != null ? userRepository.getById(gift.getBuyerUserId()).getName() : user.getName()
                                    , user.getName()
                                    , orderRepository.findByOrderToken(gift.getOrderToken()).orElse(new Order()).getOrderItemList().stream().findFirst().get().getItemName()
                                    , gift.getStatusDesc()
                                    , gift.getStatusName()
                                    , LocalDateTime.parse(gift.getPaidAt())
                            );
                        } catch (Exception e) {
                            log.error("giftResponse error ={}",e);
                            return new giftResponse();
                        }}


        ).collect(Collectors.toList());

        model.addAttribute("gifts", giftList);

        return "order/giftList";
    }

    @PostMapping("/gift-list")
    public String acceptGift(@ModelAttribute OrderDto.DeliveryInfo deliveryInfo, @RequestParam String giftToken) {

        giftApiCaller.acceptGift(deliveryInfo, giftToken);
        return "redirect:/order/gift-list";
//        return "order/giftList";
    }


    /**
     * 옵션그룹선택 (옵션 리턴)
     */
    @PostMapping("/option")
    public String optionSelect(Model model, @ModelAttribute OrderParam orderParam) {

        Item itemBy = itemReader.getItemBy(orderParam.getItemToken());
        List<ItemInfo.ItemOptionGroupInfo> optionGroupList = itemReader.getItemOptionSeries(itemBy);

        for (ItemInfo.ItemOptionGroupInfo itemOptionGroup : optionGroupList) {
            if (itemOptionGroup.getOrdering() == orderParam.getOptionGroupOrdering()) {
                model.addAttribute("optionList", itemOptionGroup.getItemOptionList());
            }
        }

        return "order/addOrder :: #optionSelect";
    }

    /**
     * 주문금액 계산 todo : 공통 리턴 처리
     */
    @PostMapping("/calcu")
    @ResponseBody
    public Map<String, Object> orderItemGroupOption(@ModelAttribute OrderParam orderParam) {
        OrderDto.RegisterOrderRequest orderDto = getOrderDto(orderParam.getItemToken(), orderParam.getOrderCount(), orderParam.getOptionGroupOrdering(), orderParam.getOptionOrdering());

        //상품 가격계산
        Long totalAmount = orderDto.calculateTotalAmount(orderParam.getOptionGroupOrdering(), orderParam.getOptionOrdering());

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("totalAmount", totalAmount);

        return responseMap;
    }

    /**
     * order dto creage
     */
    private OrderDto.RegisterOrderRequest getOrderDto(String itemToken, Long orderCount, Integer optionGroupOrdering, Integer optionOrdering) {
        //1. itemToken 통한 item 정보 얻기
        ItemInfo.Main itemInfo = itemFacade.retrieveItemInfo(itemToken);

        //2. itemToken 통한 item의 옵션리스트 얻기
        Item itemBy = itemReader.getItemBy(itemToken);
        List<ItemInfo.ItemOptionGroupInfo> itemOptionSeries = itemReader.getItemOptionSeries(itemBy);
        List<OrderDto.RegisterOrderItemOptionGroupRequest> selectedOptionSeries = new ArrayList<>();

        //3. 옵션 ordering 통해 선택한 옵션만 리스트 만들기
        //optionGroup
        for (ItemInfo.ItemOptionGroupInfo optionGroup : itemOptionSeries) {
            if (optionGroup.getOrdering() == optionGroupOrdering) {
                OrderDto.RegisterOrderItemOptionGroupRequest optionGroupRequest =
                        new OrderDto.RegisterOrderItemOptionGroupRequest(optionGroupOrdering, itemOptionSeries.get(optionGroupOrdering).getItemOptionGroupName(), new ArrayList<>());

                List<ItemInfo.ItemOptionInfo> itemOptionList = optionGroup.getItemOptionList();
                //option
                for (ItemInfo.ItemOptionInfo itemOption : itemOptionList) {
                    if (optionOrdering != -1 && itemOption.getOrdering() == optionOrdering) {
                        optionGroupRequest.addOrderItemOption(
                                new OrderDto.RegisterOrderItemOptionRequest(optionOrdering, itemOptionList.get(optionOrdering).getItemOptionName(), itemOptionList.get(optionOrdering).getItemOptionPrice())
                        );
                    }
                }
                selectedOptionSeries.add(optionGroupRequest);
            }
        }

        //Order DTO에 옵션리스트 set
        OrderDto.RegisterOrderItem registerOrderItem = new OrderDto.RegisterOrderItem();
        registerOrderItem.setOrderItemOptionGroupList(selectedOptionSeries);
        registerOrderItem.setItemName(itemInfo.getItemName());
        registerOrderItem.setItemPrice(itemInfo.getItemPrice());
        registerOrderItem.setOrderCount(orderCount);
        registerOrderItem.setItemToken(itemToken);

        OrderDto.RegisterOrderRequest orderDto = new OrderDto.RegisterOrderRequest();
        orderDto.addOrderItem(registerOrderItem);
        return orderDto;
    }

    @Data
    static class OrderParam {
        private Long orderCount;
        private String itemToken;
        private Integer optionGroupOrdering;
        private Integer optionOrdering;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class giftResponse {
        private String giftToken;
        private String buyerUserName;
        private String receiveUserName;
        private String itemName;
        private String statusDesc;
        private String statusName;
        private LocalDateTime paidAt;
    }

}
