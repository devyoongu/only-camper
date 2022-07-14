package dev.practice.order.interfaces.order;

import dev.practice.order.domain.item.Status;
import lombok.Data;

@Data
public class OrderSearchCondition {
    private Long userId;
    private String itemName;
    private String itemToken;
}
