package dev.practice.order.infrastructure.order;

import dev.practice.order.common.response.CommonResponse;
import dev.practice.order.infrastructure.order.gift.RetrofitOrderApiResponse;
import dev.practice.order.interfaces.order.gift.GiftOrderDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitGiftApi {

    @POST("api/v1/gifts/")
    Call<CommonResponse<RetrofitOrderApiResponse.Register>> registerGift(@Body GiftOrderDto.RegisterOrderRequest request);
}
