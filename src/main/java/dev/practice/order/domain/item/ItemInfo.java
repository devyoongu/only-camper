package dev.practice.order.domain.item;

import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.partner.Partner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ItemInfo {

    @Getter
    @ToString
    public static class Main {
        private final String itemToken;
        private final String partnerToken;
        private final Partner partner;
        private final String itemName;
        private final Long itemPrice;
        private final Status status;
        private final Long stockQuantity;
        private final List<ItemOptionGroupInfo> itemOptionGroupList;
        private final String representImagePath;
        private final long representImageSize;
        private final String representImageName;

        public Main(Item item, List<ItemOptionGroupInfo> itemOptionGroupInfoList) {
            this.itemToken = item.getItemToken();
            this.partnerToken = item.getPartner().getPartnerToken();
            this.partner = item.getPartner();
            this.itemName = item.getItemName();
            this.itemPrice = item.getItemPrice();
            this.status = item.getStatus();
            this.stockQuantity = item.getStockQuantity();
            this.itemOptionGroupList = itemOptionGroupInfoList;
            this.representImagePath = item.getRepresentImagePath();
            this.representImageSize = item.getRepresentImageSize();
            this.representImageName = item.getRepresentImageName();
        }
    }

    @Getter
    @ToString
    public static class ItemOptionGroupInfo {
        private final Long optionGroupId;
        private final Integer ordering;
        private final String itemOptionGroupName;
        private final List<ItemOptionInfo> itemOptionList;

        public ItemOptionGroupInfo(ItemOptionGroup itemOptionGroup, List<ItemOptionInfo> itemOptionList) {
            this.optionGroupId = itemOptionGroup.getId();
            this.ordering = itemOptionGroup.getOrdering();
            this.itemOptionGroupName = itemOptionGroup.getItemOptionGroupName();
            this.itemOptionList = itemOptionList;
        }
    }

    @Getter
    @ToString
    public static class ItemOptionInfo {
        private final Long itemOptionId;
        private final Integer ordering;
        private final String itemOptionName;
        private final Long itemOptionPrice;

        private final Long optionStockQuantity;

        public ItemOptionInfo(ItemOption itemOption) {
            this.itemOptionId = itemOption.getId();
            this.ordering = itemOption.getOrdering();
            this.itemOptionName = itemOption.getItemOptionName();
            this.itemOptionPrice = itemOption.getItemOptionPrice();
            this.optionStockQuantity = itemOption.getOptionStockQuantity();
        }
    }
}