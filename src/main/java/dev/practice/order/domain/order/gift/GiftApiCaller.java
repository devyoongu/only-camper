package dev.practice.order.domain.order.gift;

import dev.practice.order.infrastructure.order.gift.RetrofitGiftApiResponse;
import dev.practice.order.interfaces.order.OrderDto;
import dev.practice.order.interfaces.order.gift.GiftOrderDto;

import java.util.List;

public interface GiftApiCaller {
    String registerGift(GiftOrderDto.RegisterOrderRequest request);
    List<RetrofitGiftApiResponse.Gift> giftList(Long giftReceiverUserId, String Status);
    void acceptGift(OrderDto.DeliveryInfo deliveryInfo, String giftToken);
}
