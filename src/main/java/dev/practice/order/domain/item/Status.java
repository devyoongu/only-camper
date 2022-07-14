package dev.practice.order.domain.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    PREPARE("판매준비중"),
    ON_SALE("판매중"),
    END_OF_SALE("판매종료");

    private final String description;
}
