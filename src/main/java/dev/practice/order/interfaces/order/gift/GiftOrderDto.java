package dev.practice.order.interfaces.order.gift;

import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.order.OrderCommand;
import dev.practice.order.domain.order.payment.PayMethod;
import dev.practice.order.interfaces.order.OrderDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiftOrderDto {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterOrderRequest {
        @NotNull(message = "buyerUserId 는 필수값입니다")
        private Long buyerUserId;

        @NotBlank(message = "결제수단 필수값입니다")
        private String payMethod;

        @NotBlank(message = "전송수단은 필수값입니다")
        private String pushType;
        private String giftReceiverName;
        @NotBlank(message = "받는사람의 전화번호는 필수값입니다")
        private String giftReceiverPhone;
        private String giftMessage;
        @NotNull(message = "받는사람은 필수값입니다")
        private Long giftReceiverUserId;

        private List<RegisterOrderItem> orderItemList;

        private String receiverName = "TEMP_VALUE";
        private String receiverPhone = "TEMP_VALUE";
        private String receiverZipcode = "TEMP_VALUE";
        private String receiverAddress1 = "TEMP_VALUE";
        private String receiverAddress2 = "TEMP_VALUE";
        private String etcMessage = "TEMP_VALUE";

        /*public RegisterOrderRequest(OrderDto.RegisterOrderRequest orderDto) {
            this.buyerUserId = orderDto.getUserId();
            this.payMethod = orderDto.getPayMethod();
            this.orderItemList = orderDto.getOrderItemList().stream().map(oi ->
                    new GiftOrderDto.RegisterOrderItem(oi))
                    .collect(Collectors.toList());
        }*/
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterOrderItem {
        @NotNull(message = "orderCount 는 필수값입니다")
        private Long orderCount;

        @NotBlank(message = "itemToken 는 필수값입니다")
        private String itemToken;

        @NotBlank(message = "itemName 는 필수값입니다")
        private String itemName;

        @NotNull(message = "itemPrice 는 필수값입니다")
        private Long itemPrice;

        private List<RegisterOrderItemOptionGroupRequest> orderItemOptionGroupList;

        public RegisterOrderItem(ItemInfo.ItemOptionGroupInfo og, ItemInfo.Main itemInfo, Long orderCount) {
            RegisterOrderItemOptionGroupRequest registerOrderItemOptionGroupRequest = new RegisterOrderItemOptionGroupRequest(og);

            ArrayList<RegisterOrderItemOptionGroupRequest> registerOrderItemOptionGroupRequests = new ArrayList<>();
            registerOrderItemOptionGroupRequests.add(registerOrderItemOptionGroupRequest);

            this.orderCount = orderCount;
            this.itemToken = itemInfo.getItemToken();
            this.itemName = itemInfo.getItemName();
            this.itemPrice = itemInfo.getItemPrice();
            this.orderItemOptionGroupList = registerOrderItemOptionGroupRequests;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterOrderItemOptionGroupRequest {
        @NotNull(message = "ordering 는 필수값입니다")
        private Integer ordering;

        @NotBlank(message = "itemOptionGroupName 는 필수값입니다")
        private String itemOptionGroupName;

        private List<RegisterOrderItemOptionRequest> orderItemOptionList;

        public RegisterOrderItemOptionGroupRequest(ItemInfo.ItemOptionGroupInfo og) {
            this.ordering = og.getOrdering();
            this.itemOptionGroupName = og.getItemOptionGroupName();
            this.orderItemOptionList = og.getItemOptionList().stream()
                    .map(o -> new RegisterOrderItemOptionRequest(o))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterOrderItemOptionRequest {
        @NotNull(message = "ordering 는 필수값입니다")
        private Integer ordering;

        @NotBlank(message = "itemOptionName 는 필수값입니다")
        private String itemOptionName;

        @NotNull(message = "itemOptionPrice 는 필수값입니다")
        private Long itemOptionPrice;

//        @NotNull(message = "optionStockQuantity 는 필수값입니다")
//        private Long optionStockQuantity;

        public RegisterOrderItemOptionRequest(ItemInfo.ItemOptionInfo option) {
            this.ordering = option.getOrdering();
            this.itemOptionName = option.getItemOptionName();
            this.itemOptionPrice = option.getItemOptionPrice();
        }
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

        private Boolean isGift;

//        @NotBlank(message = "orderDescription 는 필수값입니다")
        private String orderDescription;
    }

    @Getter
    @Setter
    @ToString
    public static class UpdateReceiverInfoReq {
        private String receiverName;
        private String receiverPhone;
        private String receiverZipcode;
        private String receiverAddress1;
        private String receiverAddress2;
        private String etcMessage;

        public OrderCommand.UpdateReceiverInfoRequest toCommand() {
            return OrderCommand.UpdateReceiverInfoRequest.builder()
                    .receiverName(receiverName)
                    .receiverPhone(receiverPhone)
                    .receiverZipcode(receiverZipcode)
                    .receiverAddress1(receiverAddress1)
                    .receiverAddress2(receiverAddress2)
                    .etcMessage(etcMessage)
                    .build();
        }
    }
}
