package dev.practice.order.domain.item.optiongroup;

import com.google.common.collect.Lists;
import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.option.ItemOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
@Table(name = "item_option_groups")
public class ItemOptionGroup extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // 실제 db 상의 컬럼을 정의하면 중간맴핑테이블 없이 연결설정이 된다.
    private Item item;
    private Integer ordering;
    private String itemOptionGroupName;

    //mappedBy는 ItemOption쪽에서 ItemOptionGroup을 표현하는 변수를 지정해야 한다.
    @OneToMany(mappedBy = "itemOptionGroup", cascade = CascadeType.PERSIST)
    private List<ItemOption> itemOptionList = Lists.newArrayList();

    @Builder
    public ItemOptionGroup(Item item, Integer ordering, String itemOptionGroupName) {
        if (item == null) throw new InvalidParamException("ItemOptionGroup.item");
        if (ordering == null) throw new InvalidParamException("ItemOptionGroup.ordering");
        if (StringUtils.isBlank(itemOptionGroupName))
            throw new InvalidParamException("ItemOptionGroup.itemOptionGroupName");

        this.item = item;
        this.ordering = ordering;
        this.itemOptionGroupName = itemOptionGroupName;
    }

    public ItemOptionGroup addItemOption(ItemOption itemOption) {
        this.itemOptionList.add(itemOption);
        return this;
    }
}
