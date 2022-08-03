package dev.practice.order.infrastructure.item;

import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemCommand;
import dev.practice.order.domain.item.ItemOptionSeriesFactory;
import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.item.option.ItemOptionStore;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroupStore;
import dev.practice.order.infrastructure.item.option.ItemOptionRepository;
import dev.practice.order.infrastructure.item.optiongroup.ItemOptionGroupRepository;
import dev.practice.order.interfaces.item.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemOptionSeriesFactoryImpl implements ItemOptionSeriesFactory {
    private final ItemOptionGroupStore itemOptionGroupStore;
    private final ItemOptionStore itemOptionStore;
    private final ItemOptionGroupRepository itemOptionGroupRepository;
    private final ItemOptionRepository itemOptionRepository;

    @Override
    public List<ItemOptionGroup> store(ItemDto.RegisterItemRequest command, Item item) {
        var itemOptionGroupRequestList = command.getItemOptionGroupList();

        if (CollectionUtils.isEmpty(itemOptionGroupRequestList)) return Collections.emptyList();

        return itemOptionGroupRequestList.stream()
                .map(requestItemOptionGroup -> {
                    if (StringUtils.isBlank(requestItemOptionGroup.getItemOptionGroupName())) {
                        return new ItemOptionGroup();
                    }

                    //itemOptionGroup stroe - ex) 색상, 사이즈
                    var initItemOptionGroup = requestItemOptionGroup.toEntity(item);
                    var itemOptionGroup = itemOptionGroupStore.store(initItemOptionGroup);

                    // itemOption store - ex) 빨, 노, L, XL
                    if (requestItemOptionGroup.getItemOptionList() != null) {
                        requestItemOptionGroup.getItemOptionList().forEach(requestItemOption -> {
                            var initItemOption = requestItemOption.toEntity(itemOptionGroup);
                            itemOptionStore.store(initItemOption);
                        });
                    }

                    return itemOptionGroup;
                }).collect(Collectors.toList());
    }

    @Override
    public void update(ItemDto.UpdateItemRequest request, Item item) {
        var itemOptionGroupRequestList = request.getItemOptionGroupList();

        itemOptionGroupRequestList.stream()
            .map(og -> {
                ItemOptionGroup optionGroup = itemOptionGroupRepository.getById(og.getOptionGroupId());
                optionGroup.updateOptionGroup(og);

                if (og.getItemOptionList() != null) {
                    og.getItemOptionList().forEach(o -> {
                        ItemOption itemOption = itemOptionRepository.getById(o.getItemOptionId());
                        itemOption.updateItemOption(o);
                    });
                }
                return null;
            }).collect(Collectors.toList());
    }
}
