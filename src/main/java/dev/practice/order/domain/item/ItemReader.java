package dev.practice.order.domain.item;

import dev.practice.order.domain.tupleDto.AggregateDto;

import java.util.List;

public interface ItemReader {
    Item getItemBy(String itemToken);
    List<ItemInfo.ItemOptionGroupInfo> getItemOptionSeries(Item item);

    //추가
    List<Item> getItemList();

    Long getItemCount(String itemToken, Integer optionGroupOrdering, Integer optionOrdering);

    List<AggregateDto.ItemOrderCountDto> findItemOrderCountList(int limit);
}
