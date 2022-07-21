package dev.practice.order.infrastructure.order.gift;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class RetrofitOrderApiResponse {

    @Getter
    @Builder
    @ToString
    public static class Register {
        private final String giftToken;
    }
}
