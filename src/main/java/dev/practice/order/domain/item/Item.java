package dev.practice.order.domain.item;

import com.google.common.collect.Lists;
import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.common.util.TokenGenerator;
import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.partner.PartnerCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "items")
public class Item extends AbstractEntity {
    private static final String ITEM_PREFIX = "itm_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemToken;

    private String partnerToken;
    private Long partnerId;
    private String itemName;
    private Long itemPrice;

    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
    private List<ItemOptionGroup> itemOptionGroupList = Lists.newArrayList();

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long stockQuantity;

    private String representImagePath;
    private long representImageSize;
    private String representImageName;

    @Builder
    public Item(String partnerToken, Long partnerId, String itemName, Long itemPrice, Long stockQuantity,String representImagePath,
    long representImageSize,
    String representImageName) {
        if (partnerId == null) throw new InvalidParamException("Item.partnerId is null");
        if (partnerToken == null) throw new InvalidParamException("Item.partnerToken is null");
        if (StringUtils.isBlank(itemName)) throw new InvalidParamException("Item.itemName is null");
        if (itemPrice == null) throw new InvalidParamException("Item.itemPrice is null");

        this.partnerId = partnerId;
        this.partnerToken = partnerToken;
        this.itemToken = TokenGenerator.randomCharacterWithPrefix(ITEM_PREFIX);
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.stockQuantity = stockQuantity;
        this.status = Status.PREPARE;
        this.representImagePath = representImagePath;
        this.representImageSize = representImageSize;
        this.representImageName = representImageName;
    }

    public void changeOnSale() {
        this.status = Status.ON_SALE;
    }

    public void changeEndOfSale() {
        this.status = Status.END_OF_SALE;
    }

    public boolean availableSales() {
        return this.status == Status.ON_SALE;
    }

    public void updateItem(ItemCommand.RegisterItemRequest itemCommand) {
        this.itemName = itemCommand.getItemName();
        this.itemPrice = itemCommand.getItemPrice();
        this.stockQuantity = itemCommand.getStockQuantity();
    }

    //    private List<ItemOptionGroup> itemOptionGroupList = Lists.newArrayList();
    public void updateOptionGroupList(List<ItemOptionGroup> itemOptionGroupList) {
        this.itemOptionGroupList = itemOptionGroupList;
    }

    /**
     * stock ??????
     */
    public void addStock(Long quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock ??????
     */
    public void removeStock(Long quantity) {
        Long resetStock = this.stockQuantity - quantity;
        if (resetStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = resetStock;
    }

    //addItem thymeleaf select loop ?????????????????? ?????? ???????????? ??????
        /*@Getter
        @RequiredArgsConstructor
        public enum Status {
            PREPARE("???????????????"),
            ON_SALE("?????????"),
            END_OF_SALE("????????????");

            private final String description;
    }*/
}
