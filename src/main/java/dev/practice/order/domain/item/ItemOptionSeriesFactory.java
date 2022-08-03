package dev.practice.order.domain.item;


import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.interfaces.item.ItemDto;

import java.util.List;

public interface ItemOptionSeriesFactory {
    List<ItemOptionGroup> store(ItemDto.RegisterItemRequest request, Item item);
    void update(ItemDto.UpdateItemRequest request, Item item);
}
