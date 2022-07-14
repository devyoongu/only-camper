package dev.practice.order.interfaces.item;

import dev.practice.order.domain.item.Status;
import lombok.Data;

import java.security.PrivateKey;

@Data
public class ItemSearchCondition{
    private String partnerName;
    private String partnerToken;
    private String itemName;
    private Integer itemPriceGeo;
    private Integer itemPriceLoe;
    private Status status;
}
