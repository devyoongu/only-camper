package dev.practice.order.infrastructure.order;

import dev.practice.order.common.exception.EntityNotFoundException;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReaderImpl implements OrderReader {

    private final OrderRepository orderRepository;

    @Override
    public Order getOrder(String orderToken) {
        return orderRepository.findByOrderToken(orderToken)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    //todo : 페이징 추가
    public List<Order> getOrderList() {
        List<Order> all = orderRepository.findAll();
        return all;
    }
}
