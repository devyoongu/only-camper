package dev.practice.order.domain.order;

import java.util.List;

public interface OrderReader {
    Order getOrder(String orderToken);

    List<Order> getOrderList();
}
