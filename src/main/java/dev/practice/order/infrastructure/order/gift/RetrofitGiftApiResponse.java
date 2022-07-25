package dev.practice.order.infrastructure.order.gift;

import lombok.*;

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
        private final Long buyerUserId;
        private final String pushType;
        private final String giftReceiverName;
        private final String giftReceiverPhone;
        private final String giftMessage;
        private final String statusDesc;
        private final String statusName;
    }
}
