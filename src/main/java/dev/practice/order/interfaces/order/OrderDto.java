package dev.practice.order.interfaces.order;
import dev.practice.order.domain.order.payment.PayMethod;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class OrderDto {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterOrderRequest {
        @NotNull(message = "userId 는 필수값입니다")
        private Long userId;

        @NotBlank(message = "payMethod 는 필수값입니다")
        private String payMethod;

        @NotBlank(message = "receiverName 는 필수값입니다")
        private String receiverName;

        @NotBlank(message = "receiverPhone 는 필수값입니다")
        private String receiverPhone;

        @NotBlank(message = "receiverZipcode 는 필수값입니다")
        private String receiverZipcode;

        @NotBlank(message = "receiverAddress1 는 필수값입니다")
        private String receiverAddress1;

        @NotBlank(message = "receiverAddress2 는 필수값입니다")
        private String receiverAddress2;

        @NotBlank(message = "etcMessage 는 필수값입니다")
        private String etcMessage;

        private List<RegisterOrderItem> orderItemList = new ArrayList<>();


        public void addOrderItem(RegisterOrderItem registerOrderItem) {
            this.orderItemList.add(registerOrderItem);
        }

        /**
         * 주문 가격 = 주문 상품의 총 가격
         * 주문 하나의 상품의 가격 = (상품 가격 + 상품 옵션 가격) * 주문 갯수
         */
        public Long calculateTotalAmount(int optionGroupOrdering, int optionOrdering) {
            return orderItemList.stream()
                    .mapToLong(registerOrderItem -> registerOrderItem.calculateTotalAmount(optionGroupOrdering, optionOrdering))
                    .sum();
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RegisterOrderItem {
        @NotNull(message = "orderCount 는 필수값입니다")
        private Long orderCount;

        @NotBlank(message = "itemToken 는 필수값입니다")
        private String itemToken;

        @NotBlank(message = "itemName 는 필수값입니다")
        private String itemName;

        @NotNull(message = "itemPrice 는 필수값입니다")
        private Long itemPrice;

        private List<RegisterOrderItemOptionGroupRequest> orderItemOptionGroupList = new ArrayList<>();

        private String representImagePath;
        private long representImageSize;
        private String representImageName;

        @NotNull(message = "옵션그룹 선택은 필수값입니다")
        private Integer optionGroupOrdering;
        private Integer optionOrdering;

        public RegisterOrderItem(Long orderCount, String itemToken, String itemName, Long itemPrice, List<RegisterOrderItemOptionGroupRequest> orderItemOptionGroupList, String representImagePath, long representImageSize, String representImageName) {
            this.orderCount = orderCount;
            this.itemToken = itemToken;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.orderItemOptionGroupList = orderItemOptionGroupList;
            this.representImagePath = representImagePath;
            this.representImageSize = representImageSize;
            this.representImageName = representImageName;
        }

        public Long calculateTotalAmount(int optionGroupOrdering, int optionOrdering) {
            var itemOptionTotalAmount = orderItemOptionGroupList.stream()
                    .mapToLong(registerOrderItemOptionGroupRequest -> {
                        if (registerOrderItemOptionGroupRequest.getOrdering() == optionGroupOrdering) {
                            return registerOrderItemOptionGroupRequest.calculateTotalAmount(optionOrdering);
                        }
                        return 0;
                    })
                    .sum();
            return (itemPrice + itemOptionTotalAmount) * orderCount;
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Slf4j
    public static class RegisterOrderItemOptionGroupRequest {
        @NotNull(message = "ordering 는 필수값입니다")
        private Integer ordering;

        @NotBlank(message = "itemOptionGroupName 는 필수값입니다")
        private String itemOptionGroupName;

        private List<RegisterOrderItemOptionRequest> orderItemOptionList = new ArrayList<>();

        public void addOrderItemOption(RegisterOrderItemOptionRequest registerOrderItemOptionRequest) {
            this.orderItemOptionList.add(registerOrderItemOptionRequest);
        }

        public Long calculateTotalAmount(int optionOrdering) {
            long sum = 0L;
            for (RegisterOrderItemOptionRequest registerOrderItemOptionRequest : orderItemOptionList) {
                if (registerOrderItemOptionRequest.getOrdering() == optionOrdering) {
                    log.info("getOrdering ={}, optionName={}", registerOrderItemOptionRequest.getOrdering(), registerOrderItemOptionRequest.getItemOptionName());
                    long itemOptionPrice = registerOrderItemOptionRequest.getItemOptionPrice();
                    sum += itemOptionPrice;
                    continue;
                }
            }
            return sum;

        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterOrderItemOptionRequest {
        @NotNull(message = "ordering 는 필수값입니다")
        private Integer ordering;

        @NotBlank(message = "itemOptionName 는 필수값입니다")
        private String itemOptionName;

        @NotNull(message = "itemOptionPrice 는 필수값입니다")
        private Long itemOptionPrice;

        @NotNull(message = "optionStockQuantity 는 필수값입니다")
        private Long optionStockQuantity;
    }

    @Getter
    @Builder
    @ToString
    public static class RegisterResponse {
        private final String orderToken;
    }

    @Getter
    @Setter
    @ToString
    public static class PaymentRequest {
        @NotBlank(message = "orderToken 는 필수값입니다")
        private String orderToken;

        @NotNull(message = "payMethod 는 필수값입니다")
        private PayMethod payMethod;

        @NotNull(message = "amount 는 필수값입니다")
        private Long amount;

        private String orderDescription;
    }

    // 조회
    @Getter
    @Builder
    @ToString
    public static class Main {
        private final String orderToken;
        private final Long userId;
        private final String payMethod;
        private final Long totalAmount;
        private final DeliveryInfo deliveryInfo;
        private final String orderedAt;
        private final String status;
        private final String statusDescription;
        private final List<OrderItem> orderItemList;
    }


    @Getter
    @Builder
    @ToString
    public static class OrderItem {
        private final Integer orderCount;
        private final Long partnerId;
        private final Long itemId;
        private final String itemName;
        private final Long totalAmount;
        private final Long itemPrice;
        private final String deliveryStatus;
        private final String deliveryStatusDescription;
        private final List<OrderItemOptionGroup> orderItemOptionGroupList;
    }

    @Getter
    @Builder
    @ToString
    public static class OrderItemOptionGroup {
        private final Integer ordering;
        private final String itemOptionGroupName;
        private final List<OrderItemOption> orderItemOptionList;
    }

    @Getter
    @Builder
    @ToString
    public static class OrderItemOption {
        private final Integer ordering;
        private final String itemOptionName;
        private final Long itemOptionPrice;
        private final Long optionStockQuantity;
    }

    @Getter
    @Builder
    @ToString
    public static class DeliveryInfo {
        private final String receiverName;
        private final String receiverPhone;
        private final String receiverZipcode;
        private final String receiverAddress1;
        private final String receiverAddress2;
        private final String etcMessage;
    }
}
