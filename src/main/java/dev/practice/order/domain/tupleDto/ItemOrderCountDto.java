package dev.practice.order.domain.tupleDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ItemOrderCountDto {
    private String itemName;
    private Long itemId;
    private Long orderCount;

    @QueryProjection
    public ItemOrderCountDto(String itemName, Long itemId, Long orderCount) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.orderCount = orderCount;
    }
}
