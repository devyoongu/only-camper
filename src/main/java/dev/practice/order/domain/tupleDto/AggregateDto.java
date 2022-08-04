package dev.practice.order.domain.tupleDto;

import com.querydsl.core.annotations.QueryProjection;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.item.OrderItem;
import lombok.Data;

import java.time.ZonedDateTime;

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

    @Data
    public static class OrderItemListDto {
        private String orderToken;
        private ZonedDateTime orderedAt;
        private Order.Status status;
        private String receiverName;
        private String receiverZipcode;
        private Long totalAmount;

        private String itemToken;
        private String itemName;
        private Long itemPrice;
        private Integer orderCount;


        private String representImagePath;

        @QueryProjection
        public OrderItemListDto(Order order, OrderItem orderItem
                ,String representImagePath) {
            this.orderToken = order.getOrderToken();
            this.orderedAt = order.getOrderedAt();
            this.status = order.getStatus();
            this.receiverZipcode = order.getDeliveryFragment().getReceiverZipcode();
            this.totalAmount = order.calculateTotalAmount();
            this.receiverName = order.getDeliveryFragment().getReceiverName();

            this.itemToken = orderItem.getItemToken();
            this.itemName = orderItem.getItemName();
            this.itemPrice = orderItem.getItemPrice();
            this.orderCount = orderItem.getOrderCount();
            this.representImagePath = representImagePath;
        }
    }
}
