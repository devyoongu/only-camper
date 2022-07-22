package dev.practice.order.infrastructure.order.gift;

import dev.practice.order.domain.order.gift.GiftApiCaller;
import dev.practice.order.infrastructure.order.RetrofitGiftApi;
import dev.practice.order.infrastructure.retrofit.RetrofitUtils;
import dev.practice.order.interfaces.order.gift.GiftOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftApiCallerImpl implements GiftApiCaller {
    private final RetrofitUtils retrofitUtils;
    private final RetrofitGiftApi retrofitGiftApi;

    @Override
    public String registerGift(GiftOrderDto.RegisterOrderRequest request) {
        var call = retrofitGiftApi.registerGift(request);//call을 execute 해야만 그때 api call이 된다.
        String giftToken = retrofitUtils.responseSync(call)
                .map(commonResponse -> commonResponse.getData())
                .map(register -> register.getGiftToken())
                .orElseThrow(RuntimeException::new);

        return giftToken;
    }

    @Override
    public List<RetrofitGiftApiResponse.Gift> giftList(Long giftReceiverUserId){
        try {
            var call= retrofitGiftApi.giftList(giftReceiverUserId);
            List<RetrofitGiftApiResponse.Gift> gifts = retrofitUtils.responseSync(call)
                    .map(commonResponse -> commonResponse.getData())
                    .orElseThrow(RuntimeException::new);
            return gifts;
        } catch (Exception e) {
            RetrofitGiftApiResponse.Gift gift = RetrofitGiftApiResponse.Gift.builder().build();
            return new ArrayList<RetrofitGiftApiResponse.Gift>();
        }
    }
}
