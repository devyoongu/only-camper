package dev.practice.order.infrastructure.item;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.Status;
import dev.practice.order.domain.partner.QPartner;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static dev.practice.order.domain.item.QItem.*;
import static dev.practice.order.domain.partner.QPartner.*;
import static org.springframework.util.StringUtils.isEmpty;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Item> getItemList() {
        return em.createQuery(
                "select i from Item i" +
                        " join fetch i.itemOptionGroupList og", Item.class) // xToOne 은 fetch join이 유리하다.
                .getResultList();
    }

    @Override
    public List<Item> findItemAll(ItemSearchCondition searchCondition, Pageable pageable) {
        return em.createQuery(
                "select i from Item i", Item.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public Page<Item> findItemAllWithDsl(ItemSearchCondition condition, Pageable pageable) {
        QueryResults<Item> results = queryFactory.select(item)
                .from(item)
                .where(
                        itemNameContains(condition.getItemName())
                        , statusEq(condition.getStatus())
                        , priceGoe(condition.getItemPriceGeo())
                        , priceLoe(condition.getItemPriceLoe())
                        , partnerTokenEq(condition.getPartnerToken())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();// result + count

        List<Item> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Item> findItemAllWithDslJoinPartner(ItemSearchCondition searchCondition, Pageable pageable) {
//        queryFactory.select(item)
//                .from(item)
//                .leftJoin(item.partnerId, partner.id)
//                .fetch();




        return null;
    }

    private BooleanExpression itemNameContains(String itemName) {
        if (!StringUtils.hasText(itemName)) {
            return null;
        }
        return item.itemName.contains(itemName);
    }

    private BooleanExpression partnerTokenEq(String partnerToken) {
        if (!StringUtils.hasText(partnerToken)) {
            return null;
        }
        return item.partnerToken.contains(partnerToken);
    }

    private BooleanExpression statusEq(Status status) {
        return isEmpty(status) ? null : item.status.eq(status);
    }

    private BooleanExpression priceGoe(Integer price) {
        return price == null ? null : item.itemPrice.goe(price);
    }

    private BooleanExpression priceLoe(Integer price) {
        return price == null ? null : item.itemPrice.loe(price);
    }


}
