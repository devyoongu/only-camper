package dev.practice.order.domain.order.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayMethod {
    CARD("카드"),
    NAVER_PAY("네이버페이"),
    TOSS_PAY("토스페이"),
    KAKAO_PAY("카카오페이");

    private final String description;
}
