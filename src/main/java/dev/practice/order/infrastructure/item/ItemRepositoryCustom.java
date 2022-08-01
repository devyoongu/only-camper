package dev.practice.order.infrastructure.item;

import com.querydsl.core.Tuple;
import dev.practice.order.domain.tupleDto.ItemOrderCountDto;
import dev.practice.order.domain.tupleDto.PartnerItemCountDto;
import dev.practice.order.domain.item.Item;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> getItemList();
    List<Item> findItemAll(ItemSearchCondition searchCondition, Pageable pageable);

    Page<Item> findItemAllWithDsl(ItemSearchCondition searchCondition, Pageable pageable);

    List<PartnerItemCountDto> findPartnerWithItemCount();

    List<ItemOrderCountDto> findItemOrderStatusList();
}
