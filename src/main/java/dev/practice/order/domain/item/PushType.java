package dev.practice.order.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushType {
    KAKAO("카카오톡"),
    LMS("문자");

    private final String description;
}
