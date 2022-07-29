package dev.practice.order.infrastructure.order.gift;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
        private final String paidAt;
    }
}
