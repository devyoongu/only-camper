package dev.practice.order.interfaces.aggreate;

import com.querydsl.core.Tuple;
import dev.practice.order.domain.tupleDto.AggregateDto;
import dev.practice.order.infrastructure.item.ItemRepository;
import dev.practice.order.infrastructure.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("aggregate")
@RequiredArgsConstructor
public class AggregateController {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/partner-itemcount")
    @ResponseBody
    public List<AggregateDto.PartnerItemCountDto> findPartnerWithItemCount() {
        List<AggregateDto.PartnerItemCountDto> partnerWithItemList = itemRepository.findPartnerWithItemCount();

        log.error("partnerWithItemList ={}", partnerWithItemList);

        return partnerWithItemList;
    }

    @GetMapping("/item-ordercoount")
    @ResponseBody
    public List<AggregateDto.ItemOrderCountDto> findItemOrderStatusList() {
        List<AggregateDto.ItemOrderCountDto> itemOrderCountDtoList = itemRepository.findItemOrderStatusList();

        log.error("partnerWithItemList ={}", itemOrderCountDtoList);

        return itemOrderCountDtoList;
    }

    @GetMapping("/partner-ordercoount")
    @ResponseBody
    public List<AggregateDto.PartnerOrderCountDto> getOrderCountByPartner() {
        List<AggregateDto.PartnerOrderCountDto> orderCountByPartner = orderRepository.getOrderCountByPartner();

        log.error("partnerWithItemList ={}", orderCountByPartner);

        return orderCountByPartner;
    }
}

