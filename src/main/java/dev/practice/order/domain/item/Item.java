package dev.practice.order.domain.item;

import com.google.common.collect.Lists;
import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.common.util.TokenGenerator;
import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.partner.PartnerCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY) //
    @JoinColumn(name = "partner_id") //partner join 필요로 인한 추가
    private Partner partner;

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
    public Item(String partnerToken, Partner partner, String itemName, Long itemPrice, Long stockQuantity,String representImagePath,
    long representImageSize,
    String representImageName) {
        if (partner == null) throw new InvalidParamException("Item.partner is null");
        if (partnerToken == null) throw new InvalidParamException("Item.partnerToken is null");
        if (StringUtils.isBlank(itemName)) throw new InvalidParamException("Item.itemName is null");
        if (itemPrice == null) throw new InvalidParamException("Item.itemPrice is null");

        this.partner = partner;
        this.partnerToken = partnerToken;
        this.itemToken = TokenGenerator.randomCharacterWithPrefix(ITEM_PREFIX);
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.stockQuantity = stockQuantity;
        this.status = Status.ON_SALE;
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

    public void updateOptionGroupList(List<ItemOptionGroup> itemOptionGroupList) {
        this.itemOptionGroupList = itemOptionGroupList;
    }

    /**
     * stock 증가
     */
    public void addStock(Long quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(Long quantity) {
        Long resetStock = this.stockQuantity - quantity;
        if (resetStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = resetStock;
    }

    //addItem thymeleaf select loop 표현식오류로 별도 클래스로 분리
        /*@Getter
        @RequiredArgsConstructor
        public enum Status {
            PREPARE("판매준비중"),
            ON_SALE("판매중"),
            END_OF_SALE("판매종료");

            private final String description;
    }*/
}
