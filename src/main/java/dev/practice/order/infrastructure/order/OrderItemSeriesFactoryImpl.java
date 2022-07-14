package dev.practice.order.infrastructure.order;

import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderCommand;
import dev.practice.order.domain.order.OrderItemSeriesFactory;
import dev.practice.order.domain.order.OrderStore;
import dev.practice.order.domain.order.item.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderItemSeriesFactoryImpl implements OrderItemSeriesFactory {
    private final ItemReader itemReader;
    private final OrderStore orderStore;

    @Override
    public List<OrderItem> store(Order order, OrderCommand.RegisterOrder requestOrder) {
        //Order -> OrderItem -> OrderItemOpitonGroup -> OrderItemOption

        //requestOrder를 1. orderItem 저장 ->2. orderItemOptionGroup 저장 -> 3. orderItemOption 저장
        return requestOrder.getOrderItemList().stream()
                .map(orderItemRequest -> {
                    //item 정보 get
                    var item = itemReader.getItemBy(orderItemRequest.getItemToken());

                    //1. orderItem으로 변환 후 저장
                    var initOrderItem = orderItemRequest.toEntity(order, item);
                    var orderItem = orderStore.store(initOrderItem);

                    //todo : 0번째가 아닌 값을 받아서 처리
                    ItemOption itemOption = item.getItemOptionGroupList().get(0).getItemOptionList().get(0);

                    if (orderItemRequest.getOrderItemOptionGroupList() != null) {
                        orderItemRequest.getOrderItemOptionGroupList().forEach(orderItemOptionGroupRequest -> {
                            //2. OrderItemOptionGroup 엔티티로 변환 후 저장
                            var initOrderItemOptionGroup = orderItemOptionGroupRequest.toEntity(orderItem);
                            var orderItemOptionGroup = orderStore.store(initOrderItemOptionGroup);

                            orderItemOptionGroupRequest.getOrderItemOptionList().forEach(orderItemOptionRequest -> {
                                //3. orderItemOption 엔티티로 변환 및 재고수정
                                var initOrderItemOption = orderItemOptionRequest.toEntity(itemOption, orderItemOptionGroup);
                                orderStore.store(initOrderItemOption);
                            });
                        });

                    }

                    return orderItem;
                }).collect(Collectors.toList());
    }
}
