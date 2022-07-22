package dev.practice.order.infrastructure.order.gift;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class RetrofitGiftApiResponse {

    @Getter
    @Builder
    @ToString
    public static class Register {
        private final String giftToken;
    }

    @Getter
    @Builder
    @ToString
    public static class Gift {
        private final String orderToken;
        private final String giftToken;
        private final String pushType;
        private final String giftReceiverName;
        private final String giftReceiverPhone;
        private final String giftMessage;
        private final String status;
    }
}
