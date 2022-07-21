package dev.practice.order.domain.order.gift;

import dev.practice.order.interfaces.order.gift.GiftOrderDto;

public interface GiftApiCaller {
    String registerGift(GiftOrderDto.RegisterOrderRequest request);
}
