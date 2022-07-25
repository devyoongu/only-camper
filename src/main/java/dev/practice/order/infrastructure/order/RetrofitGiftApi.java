package dev.practice.order.infrastructure.order;

import dev.practice.order.common.response.CommonResponse;
import dev.practice.order.infrastructure.order.gift.RetrofitGiftApiResponse;
import dev.practice.order.interfaces.order.OrderDto;
import dev.practice.order.interfaces.order.gift.GiftOrderDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface RetrofitGiftApi {

    @POST("api/v1/gifts/")
    Call<CommonResponse<RetrofitGiftApiResponse.Register>> registerGift(@Body GiftOrderDto.RegisterOrderRequest request);

    @GET("api/v1/gifts")
    Call<CommonResponse<List<RetrofitGiftApiResponse.Gift>>> giftList(@Query("giftReceiverUserId") Long giftReceiverUserId, @Query("status") String status);

    @POST("/api/v1/gifts/{giftToken}/accept-gift")
    Call<CommonResponse> acceptGift(@Body OrderDto.DeliveryInfo deliveryInfo, @Path("giftToken") String giftToken);
}
