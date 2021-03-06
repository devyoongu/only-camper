package dev.practice.order.interfaces;

import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.item.ItemService;
import dev.practice.order.infrastructure.item.ItemRepository;
import dev.practice.order.interfaces.item.ItemDto;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import dev.practice.order.interfaces.item.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final ItemReader itemReader;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @GetMapping("/")
    public String index(@ModelAttribute("searchCondition") ItemSearchCondition searchCondition
                , @PageableDefault(size = 16, sort = "createdDate",direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        Page<Item> itemPage = itemRepository.findItemAllWithDsl(searchCondition, pageable);

        //item DTO 변환 및 Lazy 객체에 대한 초기화를 통해 조회 쿼리  todo : repository로 로직 이동 필요
        List<ItemDto.RegisterItemRequest> itemDtoList = itemPage.getContent().stream()
                .map(item -> new ItemDto.RegisterItemRequest(item))
                .collect(Collectors.toList());

        model.addAttribute("items", itemDtoList);
        model.addAttribute("page", new PageDto(itemPage.getTotalElements(), pageable));
        model.addAttribute("activeNum", pageable.getPageNumber());

        return "index";
    }
}
