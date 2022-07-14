package dev.practice.order.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자"),
    SELLER("SELLER_USER", "판매자"),
    MANAGER("MANAGER_USER", "관리자");

    private final String key;
    private final String title;

}
