package dev.practice.order.interfaces.aggreate;

import com.querydsl.core.Tuple;
import dev.practice.order.common.response.CommonResponse;
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

    /**
     * 파트너별 - 아이템 등록 현황
     * */
    @GetMapping("/partner-itemcount")
    @ResponseBody
    public CommonResponse findPartnerWithItemCount() {
        List<AggregateDto.PartnerItemCountDto> partnerWithItemList = itemRepository.findPartnerWithItemCount();

        return CommonResponse.success(partnerWithItemList);
    }

    /**
     * 파트너별 - 판매현황
     * */
    @GetMapping("/partner-ordercoount")
    @ResponseBody
    public CommonResponse getOrderCountByPartner() {
        List<AggregateDto.PartnerOrderCountDto> orderCountByPartner = orderRepository.getOrderCountByPartner();

        return CommonResponse.success(orderCountByPartner);
    }

    /**
     * 상품별 - 주문현황
     * */
    @GetMapping("/item-ordercoount")
    @ResponseBody
    public CommonResponse findItemOrderStatusList() {
        List<AggregateDto.ItemOrderCountDto> itemOrderCountDtoList = itemRepository.findItemOrderStatusList();

        return CommonResponse.success(itemOrderCountDtoList);
    }

    /**
     * 주문날짜별 - 판매현황
     * */
    @GetMapping("/order-datecount")
    @ResponseBody
    public CommonResponse getOrderCountByDate() {
        List<AggregateDto.OrderDateCountDto> orderDateCountDtoList = orderRepository.getOrderCountByDate();

        return CommonResponse.success(orderDateCountDtoList);
    }


    @GetMapping
    public String viewChart () {
        return "aggregate/aggregate";
    }

}

