package dev.practice.order.domain.tupleDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

public class AggregateDto {

    @Data
    public static class PartnerItemCountDto {

        private Long partnerId;
        private String partnerName;
        private Long itemCount;

        @QueryProjection
        public PartnerItemCountDto(Long partnerId, String partnerName, Long itemCount) {
            this.partnerId = partnerId;
            this.partnerName = partnerName;
            this.itemCount = itemCount;
        }
    }

    @Data
    public static class ItemOrderCountDto {
        private String itemName;
        private Long itemId;
        private Long orderCount;
        private String itemToken;
        private Long itemPrice;
        private String representImagePath;

        @QueryProjection
        public ItemOrderCountDto(String itemName, Long itemId, Long orderCount, String itemToken, Long itemPrice, String representImagePath) {
            this.itemName = itemName;
            this.itemId = itemId;
            this.orderCount = orderCount;
            this.itemToken = itemToken;
            this.itemPrice = itemPrice;
            this.representImagePath = representImagePath;
        }
    }

    @Data
    public static class PartnerOrderCountDto {
        private String partnerName;
        private Long partnerId;
        private Long orderCount;

        @QueryProjection
        public PartnerOrderCountDto(String partnerName, Long partnerId, Long orderCount) {
            this.partnerName = partnerName;
            this.partnerId = partnerId;
            this.orderCount = orderCount;
        }
    }

    @Data
    public static class OrderDateCountDto {
        private String orderDate;
        private Long orderCount;

        @QueryProjection
        public OrderDateCountDto(String orderDate, Long orderCount) {
            this.orderDate = orderDate;
            this.orderCount = orderCount;
        }
    }
}
