package dev.practice.order.infrastructure.retrofit;

import dev.practice.order.infrastructure.order.RetrofitGiftApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetrofitServiceRegistry {

    @Value("${example.gift.base-url}")
    private String baseUrl;

    @Bean
    public RetrofitGiftApi retrofitOrderApi() {
        var retrofit = RetrofitUtils.initRetrofit(baseUrl);
        return retrofit.create(RetrofitGiftApi.class);
    }
}
