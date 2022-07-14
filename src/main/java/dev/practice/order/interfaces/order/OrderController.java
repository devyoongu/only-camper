package dev.practice.order.interfaces.order;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.application.order.OrderFacade;
import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.item.ItemService;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderInfo;
import dev.practice.order.domain.order.OrderInfoMapper;
import dev.practice.order.domain.order.OrderService;
import dev.practice.order.domain.order.fragment.DeliveryFragment;
import dev.practice.order.domain.order.item.OrderItem;
import dev.practice.order.infrastructure.order.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {

    public static final Long ORDER_COUNT = 1L;
    private final OrderFacade orderFacade;
    private final OrderDtoMapper orderDtoMapper;
    private final ItemService itemService;

    private final OrderService orderService;

    private final ItemReader itemReader;

    private final ItemFacade itemFacade;

    private final OrderRepository orderRepository;

    private final OrderInfoMapper orderInfoMapper;

    @ModelAttribute("deliveryFragment")
    public DeliveryFragment deliveryFragment() {
        return DeliveryFragment.builder()
                .receiverName("receiverName")
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

    /**
     * 0. 주문 목록
     */
    @GetMapping("/list")
    public String items(@ModelAttribute OrderSearchCondition condition
            , @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
            , @LoginUser SessionUser user, Model model) {

        condition.setUserId(user.getId());
        Page<OrderInfo.Main> orderListMine = orderRepository.getOrderListMine(condition, pageable);

        model.addAttribute("orderList", orderListMine.getContent());
        return "order/orderList";
    }

    /**
     * 1. 상품주문- Form
     */
    @GetMapping("/{itemToken}")
    public String orderItemForm(@PathVariable String itemToken, Model model) {

        Item item = itemReader.getItemBy(itemToken);
        List<ItemInfo.ItemOptionGroupInfo> itemOptionSeries = itemReader.getItemOptionSeries(item);

        List<OrderDto.RegisterOrderItemOptionGroupRequest> optionGroupList = itemOptionSeries.stream()
                .map(og -> new OrderDto.RegisterOrderItemOptionGroupRequest(og.getOrdering(), og.getItemOptionGroupName(), null))
                .collect(Collectors.toList());

        OrderDto.RegisterOrderItem orderItem = new OrderDto.RegisterOrderItem(ORDER_COUNT, item.getItemToken(), item.getItemName(), item.getItemPrice()
                , optionGroupList,item.getRepresentImagePath(),item.getRepresentImageSize(),item.getRepresentImageName());

        model.addAttribute("item", orderItem);

        return "order/addOrder";
    }

    /**
     * 주문등록
     */
    @PostMapping("/{itemToken}")
    public String orderItemGroupOption(@Valid @ModelAttribute("item") OrderDto.RegisterOrderItem orderItem, BindingResult bindingResult
            ,@ModelAttribute("deliveryFragment") DeliveryFragment deliveryFragment
            , @PathVariable String itemToken
                                       ,@LoginUser SessionUser user, @RequestParam("payMethod") String payMethod
    ) {
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "order/addOrder";
        }

        Long itemCount = itemReader.getItemCount(itemToken, orderItem.getOptionGroupOrdering(), orderItem.getOptionOrdering());

        if (itemCount < orderItem.getOptionOrdering()) {
            bindingResult.reject("stockError", new Object[]{itemCount, orderItem.getOrderCount()}, null);
        }

        OrderDto.RegisterOrderRequest orderDto = getOrderDto(itemToken, orderItem.getOrderCount(), orderItem.getOptionGroupOrdering(), orderItem.getOptionOrdering());

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
                                new OrderDto.RegisterOrderItemOptionRequest(optionOrdering, itemOptionList.get(optionOrdering).getItemOptionName(), itemOptionList.get(optionOrdering).getItemOptionPrice(), orderCount)
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

}
