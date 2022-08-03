package dev.practice.order.infrastructure.item;

import dev.practice.order.common.exception.EntityNotFoundException;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.tupleDto.AggregateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemReaderImpl implements ItemReader {
    private final ItemRepository itemRepository;

    @Override
    public Item getItemBy(String itemToken) {
        return getItem(itemToken);
    }

    @Override
    public List<ItemInfo.ItemOptionGroupInfo> getItemOptionSeries(Item item) {
        //aggreate 중 item(root)를 통해서 ItemOptionGroup, ItemOption 조회
        return getItemOptionGroupInfos(item);
    }

    private List<ItemInfo.ItemOptionGroupInfo> getItemOptionGroupInfos(Item item) {
        var itemOptionGroupList = item.getItemOptionGroupList();

        return itemOptionGroupList.stream()
                .map(itemOptionGroup -> {
                    var itemOptionList = itemOptionGroup.getItemOptionList();
                    var itemOptionInfoList = itemOptionList.stream()
                            .map(ItemInfo.ItemOptionInfo::new)
                            .collect(Collectors.toList());

                    return new ItemInfo.ItemOptionGroupInfo(itemOptionGroup, itemOptionInfoList);
                }).collect(Collectors.toList());
    }

    @Override
    //todo : 페이징 추가
    public List<Item> getItemList() {
//        List<Item> all = itemRepository.findAll();
        List<Item> all = itemRepository.getItemList();
        return all;
    }

    @Override
    public Long getItemCount(String itemToken, Integer optionGroupOrdering, Integer optionOrdering) {
        Item item = getItem(itemToken);
        List<ItemInfo.ItemOptionGroupInfo> optionGroupInfoList = getItemOptionGroupInfos(item);

        Long optionStockQuantity = optionGroupInfoList.get(optionGroupOrdering).getItemOptionList().get(optionOrdering).getOptionStockQuantity();

        return optionStockQuantity;
    }

    @Override
    public List<AggregateDto.ItemOrderCountDto> findItemOrderCountList(int limit) {
        List<AggregateDto.ItemOrderCountDto> itemMostOrderList = itemRepository.findItemOrderCountList(limit);

        List<AggregateDto.ItemOrderCountDto> orderCountFilterList = itemMostOrderList.stream()
                .filter(io -> io.getOrderCount() > 0)
                .collect(Collectors.toList());

        return orderCountFilterList;
    }


    private Item getItem(String itemToken) {
        return itemRepository.findByItemToken(itemToken)
                .orElseThrow(EntityNotFoundException::new);
    }
}
