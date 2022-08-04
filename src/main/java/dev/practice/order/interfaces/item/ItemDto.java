package dev.practice.order.interfaces.item;

import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.Status;
import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.partner.Partner;
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

        private Status status;

        private String representImagePath;
        private long representImageSize;
        private String representImageName;

        //todo : form용 dto를 추가하고 item 객체에 없는 저장용 필드는 제거해야 함.
        @NotNull(message = "대표 이미지 첨부는 필수값입니다")
        private MultipartFile image;
        private List<RegisterItemOptionGroupRequest> itemOptionGroupList = new ArrayList<>();

        public RegisterItemRequest(Item item) {
            this.partnerToken = item.getPartnerToken();
            this.itemToken = item.getItemToken();
            this.itemName = item.getItemName();
            this.itemPrice = item.getItemPrice();
            this.stockQuantity = item.getStockQuantity();
            this.status = item.getStatus();
            this.representImagePath = item.getRepresentImagePath();
            this.representImageSize = item.getRepresentImageSize();
            this.representImageName = item.getRepresentImageName();
            this.itemOptionGroupList = item.getItemOptionGroupList().stream()
                    .map(og -> new RegisterItemOptionGroupRequest(og))
                    .collect(Collectors.toList());
        }

        //임시 -테스트용도
        public RegisterItemRequest(Item item, List<RegisterItemOptionGroupRequest> itemOptionGroupList) {
            this.partnerToken = item.getPartnerToken();
            this.itemToken = item.getItemToken();
            this.itemName = item.getItemName();
            this.itemPrice = item.getItemPrice();
            this.stockQuantity = item.getStockQuantity();
            this.status = item.getStatus();
            this.representImagePath = item.getRepresentImagePath();
            this.representImageSize = item.getRepresentImageSize();
            this.representImageName = item.getRepresentImageName();
            this.itemOptionGroupList = itemOptionGroupList;
        }

        //DTO에 값이 저장되어 있기 대문에 바로 주입이 가능하여 편리함.
        public Item toEntity(Partner partner) {
            return Item.builder()
                    .partnerToken(partner.getPartnerToken())
                    .partner(partner)
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
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterItemOptionGroupRequest {

        private Long optionGroupId;
        private Integer ordering;
        private String itemOptionGroupName;
        private List<RegisterItemOptionRequest> itemOptionList = new ArrayList<>();

        public RegisterItemOptionGroupRequest(ItemOptionGroup itemOptionGroup) {
            this.optionGroupId = itemOptionGroup.getId();
            this.ordering = itemOptionGroup.getOrdering();
            this.itemOptionGroupName = itemOptionGroup.getItemOptionGroupName();
            this.itemOptionList = itemOptionGroup.getItemOptionList().stream()
                    .map(o -> new RegisterItemOptionRequest(o))
                    .collect(Collectors.toList());
        }

        public RegisterItemOptionGroupRequest(Integer ordering, String itemOptionGroupName, List<RegisterItemOptionRequest> itemOptionList) {
            this.itemOptionGroupName = itemOptionGroupName;
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
        private Long itemOptionId;
        private Integer ordering = 1;
        private String itemOptionName;
        private Long itemOptionPrice = 0L;
        private Long optionStockQuantity;

        public RegisterItemOptionRequest(ItemOption itemOption) {
            this.itemOptionId = itemOption.getId();
            this.ordering = itemOption.getOrdering();
            this.itemOptionName = itemOption.getItemOptionName();
            this.itemOptionPrice = itemOption.getItemOptionPrice();
            this.optionStockQuantity = itemOption.getOptionStockQuantity();
        }

        //임시 -테스트용도
        public RegisterItemOptionRequest(Integer ordering) {
            this.ordering = ordering;
        }

        //임시 -테스트용도
        public RegisterItemOptionRequest(Integer ordering, String itemOptionName, Long itemOptionPrice, Long optionStockQuantity) {
            this.ordering = ordering;
            this.itemOptionName = itemOptionName;
            this.itemOptionPrice = itemOptionPrice;
            this.optionStockQuantity = optionStockQuantity;
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
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UpdateItemRequest {
        private String partnerToken;
        private String itemToken;

        @NotBlank(message = "상품명은 필수입니다")
        private String itemName;
        @NotNull(message = "상품가격은 필수입니다")
        private Long itemPrice;
        @NotNull(message = "제고수량은 필수입니다")
        private Long stockQuantity;

        private String statusName;

        private String representImagePath;
        private long representImageSize;
        private String representImageName;

        private MultipartFile image;
        private List<RegisterItemOptionGroupRequest> itemOptionGroupList = new ArrayList<>();

        public UpdateItemRequest(Item item) {
            this.partnerToken = item.getPartnerToken();
            this.itemToken = item.getItemToken();
            this.itemName = item.getItemName();
            this.itemPrice = item.getItemPrice();
            this.stockQuantity = item.getStockQuantity();
            this.statusName = item.getStatus().name();
            this.representImagePath = item.getRepresentImagePath();
            this.representImageSize = item.getRepresentImageSize();
            this.representImageName = item.getRepresentImageName();
            this.itemOptionGroupList = item.getItemOptionGroupList().stream()
                    .map(og -> new RegisterItemOptionGroupRequest(og))
                    .collect(Collectors.toList());
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
        private final String partnerToken;
        private final Partner partner;
        private final String itemName;
        private final Long itemPrice;
        private final Status status;
        private final List<ItemOptionGroupInfo> itemOptionGroupList;
        private final String representImagePath;
        private final long representImageSize;
        private final String representImageName;
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
