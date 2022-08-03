package dev.practice.order.domain.item;

import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.interfaces.item.ItemDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ItemCommand {

    @Getter
    @Builder
    @ToString
    public static class RegisterItemRequest {
        private final String partnerToken;
        private final String itemToken;
        private final String itemName;
        private final Long itemPrice;
        private final Long stockQuantity;
        private final Status status;
        private final String representImagePath;
        private final long representImageSize;
        private final String representImageName;
        private final List<RegisterItemOptionGroupRequest> itemOptionGroupRequestList; // ex) 색상, 사이즈

    }

    @Getter
    @Builder
    @ToString
    public static class RegisterItemOptionGroupRequest {  // ex) 색상
        private final Integer ordering;
        private final String itemOptionGroupName;
        private final List<RegisterItemOptionRequest> itemOptionRequestList;  // ex) R, B, W

        public ItemOptionGroup toEntity(Item item) {
            return ItemOptionGroup.builder()
                    .item(item)
                    .ordering(ordering)
                    .itemOptionGroupName(itemOptionGroupName)
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class RegisterItemOptionRequest {
        private final Integer ordering;
        private final String itemOptionName;
        private final Long itemOptionPrice;
        private final Long optionStockQuantity;

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
}
