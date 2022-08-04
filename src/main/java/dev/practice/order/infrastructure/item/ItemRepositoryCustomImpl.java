package dev.practice.order.infrastructure.item;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.QOrder;
import dev.practice.order.domain.tupleDto.*;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.QItem;
import dev.practice.order.domain.item.Status;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static dev.practice.order.domain.item.QItem.*;
import static dev.practice.order.domain.order.QOrder.order;
import static dev.practice.order.domain.order.item.QOrderItem.*;
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
                .orderBy(item.id.desc())
                .limit(pageable.getPageSize())
                .fetchResults();// result + count

        List<Item> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Aggregate - 판매자별 아이템 등록현황
     */
    @Override
    public List<AggregateDto.PartnerItemCountDto> findPartnerWithItemCount() {
        StringPath itemCount = Expressions.stringPath("itemCount");

        return queryFactory.select(
                new QAggregateDto_PartnerItemCountDto(
                        partner.id, partner.partnerName
                        , ExpressionUtils.as(item.count(),"itemCount")
                )
        )
                .from(item)
                .leftJoin(item.partner, partner)
                .where(statusEq(Status.valueOf("ON_SALE")))
                .groupBy(item.partner)
                .orderBy(itemCount.desc())
                .limit(7)
                .fetch();
    }

    /**
     * Aggregate - 아이템별 판매내역
     * item, order-item은 관계를 갖고 있지 않으므로 sub 쿼리로 진행
     * */
    @Override
    public List<AggregateDto.ItemOrderCountDto> findItemOrderCountList(int limit) {

        StringPath orderCount = Expressions.stringPath("orderCount");

        return queryFactory.select(
                Projections.constructor(AggregateDto.ItemOrderCountDto.class,
                        item.itemName
                        , item.id
                        , ExpressionUtils.as(JPAExpressions
                                .select(orderItem.itemName.count())
                                .from(orderItem)
                                .join(orderItem.order, order)
                                .where(orderItem.itemId.eq(item.id)
                                        ,order.status.eq(Order.Status.valueOf("ORDER_COMPLETE"))
                                ), "orderCount")
                        ,item.itemToken
                        ,item.itemPrice
                        ,item.representImagePath
                )
        )
                .from(item)
                .groupBy(item.itemName, item.id)
                .orderBy(orderCount.desc())
                .limit(limit)
                .fetch();
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
        return item.partnerToken.eq(partnerToken);
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
