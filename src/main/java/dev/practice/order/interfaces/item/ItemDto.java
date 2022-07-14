package dev.practice.order.interfaces.item;

import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.Status;
import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemDto {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterItemRequest {
        private String partnerToken;
        private String itemToken;

        @NotBlank(message = "상품명은 필수입니다")
        private String itemName;
        @NotNull(message = "상품가격은 필수입니다")
        private Long itemPrice;
        @NotNull(message = "제고수량은 필수입니다")
        private Long stockQuantity;

        private String status;

        private String representImagePath;
        private long representImageSize;
        private String representImageName;

        //todo : form용 dto를 추가하고 item 객체에 없는 저장용 필드는 제거해야 함.
        @NotNull(message = "대표 이미지 첨부는 필수값입니다")
        private MultipartFile image;
        private List<RegisterItemOptionGroupRequest> itemOptionGroupList = new ArrayList<>();

        public RegisterItemRequest(String partnerToken, String itemToken, String itemName, Long itemPrice
                , Long stockQuantity, String status, String representImagePath, long representImageSize
                , String representImageName, List<RegisterItemOptionGroupRequest> itemOptionGroupList) {
            this.partnerToken = partnerToken;
            this.itemToken = itemToken;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.stockQuantity = stockQuantity;
            this.status = status;
            this.representImagePath = representImagePath;
            this.representImageSize = representImageSize;
            this.representImageName = representImageName;
            this.itemOptionGroupList = itemOptionGroupList;
        }

        public RegisterItemRequest(Item item) {
            this.partnerToken = item.getPartnerToken();
            this.itemToken = item.getItemToken();
            this.itemName = item.getItemName();
            this.itemPrice = item.getItemPrice();
            this.stockQuantity = item.getStockQuantity();
            this.status = item.getStatus().toString();
            this.representImagePath = item.getRepresentImagePath();
            this.representImageSize = item.getRepresentImageSize();
            this.representImageName = item.getRepresentImageName();
            this.itemOptionGroupList = item.getItemOptionGroupList().stream()
                    .map(og -> new RegisterItemOptionGroupRequest(og))
                    .collect(Collectors.toList());
        }

        //DTO에 값이 저장되어 있기 대문에 바로 주입이 가능하여 편리함.
        public Item toEntity(Long partnerId, String partnerToken) {
            return Item.builder()
                    .partnerToken(partnerToken)
                    .partnerId(partnerId)
                    .itemName(itemName)
                    .itemPrice(itemPrice)
                    .stockQuantity(stockQuantity)
                    .representImageName(representImageName)
                    .representImagePath(representImagePath)
                    .representImageSize(representImageSize)
                    .build();
        }

        public void addItemGroup(RegisterItemOptionGroupRequest itemOptionGroup) {
            this.itemOptionGroupList.add(itemOptionGroup);
        }

        URI convertStrToUri(String url) {
            return URI.create(url);
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterItemOptionGroupRequest {

        private Integer ordering;
        private String itemOptionGroupName;
        private List<RegisterItemOptionRequest> itemOptionList = new ArrayList<>();

        public RegisterItemOptionGroupRequest(ItemOptionGroup itemOptionGroup) {
            this.ordering = itemOptionGroup.getOrdering();
            this.itemOptionGroupName = itemOptionGroup.getItemOptionGroupName();
            this.itemOptionList = itemOptionGroup.getItemOptionList().stream()
                    .map(o -> new RegisterItemOptionRequest(o))
                    .collect(Collectors.toList());
        }

        public RegisterItemOptionGroupRequest(List<RegisterItemOptionRequest> itemOptionList, Integer ordering) {
            this.ordering = ordering;
            this.itemOptionList = itemOptionList;
        }

        public List<RegisterItemOptionRequest> addOptionList(RegisterItemOptionRequest itemOption) {
            this.itemOptionList.add(itemOption);
            return itemOptionList;
        }

        public ItemOptionGroup toEntity(Item item) {
            return ItemOptionGroup.builder()
                    .item(item)
                    .ordering(ordering)
                    .itemOptionGroupName(itemOptionGroupName)
                    .build();
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterItemOptionRequest {
        private Integer ordering = 1;
        private String itemOptionName;
        private Long itemOptionPrice;
        private Long optionStockQuantity;

        public RegisterItemOptionRequest(ItemOption itemOption) {
            this.ordering = itemOption.getOrdering();
            this.itemOptionName = itemOption.getItemOptionName();
            this.itemOptionPrice = itemOption.getItemOptionPrice();
            this.optionStockQuantity = itemOption.getOptionStockQuantity();
        }

        public RegisterItemOptionRequest(Integer ordering) {
            this.ordering = ordering;
        }

        public ItemOption toEntity(ItemOptionGroup itemOptionGroup) {
            return ItemOption.builder()
                    .itemOptionGroup(itemOptionGroup)
                    .ordering(ordering)
                    .itemOptionName(itemOptionName)
                    .itemOptionPrice(itemOptionPrice)
                    .optionStockQuantity(optionStockQuantity)
                    .build();
        }

    }

    @Getter
    @Builder
    @ToString
    public static class RegisterResponse {
        private final String itemToken;
    }

    @Getter
    @Setter
    @ToString
    public static class ChangeStatusItemRequest {
        private String itemToken;
    }

    @Getter
    @Builder
    @ToString
    public static class Main {
        private final String itemToken;
        private final Long partnerId;
        private final String itemName;
        private final Long itemPrice;
        private final Status status;
        private final List<ItemOptionGroupInfo> itemOptionGroupList;
    }

    @Getter
    @Builder
    @ToString
    public static class ItemOptionGroupInfo {
        private final Integer ordering;
        private final String itemOptionGroupName;
        private final List<ItemOptionInfo> itemOptionList;
    }

    @Getter
    @Builder
    @ToString
    public static class ItemOptionInfo {
        private final Integer ordering;
        private final String itemOptionName;
        private final Long itemOptionPrice;
        private final Long optionStockQuantity;
    }
}
