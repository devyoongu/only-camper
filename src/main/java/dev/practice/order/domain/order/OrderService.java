package dev.practice.order.domain.order;

import java.util.List;

public interface OrderService {
    String registerOrder(OrderCommand.RegisterOrder registerOrder);

    void paymentOrder(OrderCommand.PaymentRequest paymentRequest);

    OrderInfo.Main retrieveOrder(String orderToken);

    void updateReceiverInfo(String orderToken, OrderCommand.UpdateReceiverInfoRequest request);

    List<Order> getOrderList();
}