package dev.practice.order.infrastructure.item;

import dev.practice.order.domain.tupleDto.AggregateDto;
import dev.practice.order.domain.item.Item;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> getItemList();
    List<Item> findItemAll(ItemSearchCondition searchCondition, Pageable pageable);

    Page<Item> findItemAllWithDsl(ItemSearchCondition searchCondition, Pageable pageable);

    List<AggregateDto.PartnerItemCountDto> findPartnerWithItemCount();

    List<AggregateDto.ItemOrderCountDto> findItemOrderCountList(int limit);
}
