package dev.practice.order.domain.item.option;


import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.item.NotEnoughStockException;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
@Table(name = "item_options")
public class ItemOption extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_option_group_id")
    private ItemOptionGroup itemOptionGroup;
    private Integer ordering;
    private String itemOptionName;
    private Long itemOptionPrice;

    private Long optionStockQuantity;

    @Builder
    public ItemOption(
            ItemOptionGroup itemOptionGroup,
            Integer ordering,
            String itemOptionName,
            Long itemOptionPrice,
            Long optionStockQuantity
    ) {
        if (itemOptionGroup == null) throw new InvalidParamException("ItemOption.itemOptionGroup null");
        if (ordering == null) throw new InvalidParamException("ItemOption.ordering null");
        if (StringUtils.isBlank(itemOptionName)) throw new InvalidParamException("ItemOption.itemOptionName null");
        if (itemOptionPrice == null) throw new InvalidParamException("ItemOption.itemOptionPrice null");
        if (optionStockQuantity == null) throw new InvalidParamException("ItemOption.optionStockQuantity null");

        this.itemOptionGroup = itemOptionGroup;
        this.ordering = ordering;
        this.itemOptionName = itemOptionName;
        this.itemOptionPrice = itemOptionPrice;
        this.optionStockQuantity = optionStockQuantity;

    }

    /**
     * stock 증가
     */
    public void addStock(Long quantity) {
        this.optionStockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(Long quantity) {
        if (this.optionStockQuantity != null) {
            Long resetStock = this.optionStockQuantity - quantity;
            if (resetStock < 0) {
                throw new NotEnoughStockException("need more stock");
            }
            this.optionStockQuantity = resetStock;
        }
    }
}
