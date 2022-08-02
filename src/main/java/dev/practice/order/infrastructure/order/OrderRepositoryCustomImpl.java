package dev.practice.order.infrastructure.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderInfo;
import dev.practice.order.domain.order.OrderInfoMapper;
import dev.practice.order.domain.order.QOrder;
import dev.practice.order.domain.order.item.QOrderItem;
import dev.practice.order.domain.partner.QPartner;
import dev.practice.order.domain.tupleDto.AggregateDto;
import dev.practice.order.interfaces.order.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.practice.order.domain.item.QItem.item;
import static dev.practice.order.domain.order.QOrder.*;
import static dev.practice.order.domain.order.item.QOrderItem.*;
import static dev.practice.order.domain.partner.QPartner.partner;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private final OrderInfoMapper orderInfoMapper;

    public OrderRepositoryCustomImpl(EntityManager em, JPAQueryFactory queryFactory, OrderInfoMapper orderInfoMapper) {
        this.em = em;
        this.queryFactory = queryFactory;
        this.orderInfoMapper = orderInfoMapper;
    }

    @Override
    public Page<Order> getOrderListMine(OrderSearchCondition condition, Pageable pageable) {

        QueryResults<Order> results = queryFactory.selectFrom(order)
                .where(
                        userIdEq(condition.getUserId())
                )
                .offset(pageable.getOffset())
                .orderBy(order.id.desc())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Order> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);

    }

    @Override
    public int getOrderListMineCount(SessionUser user) {

        List<Order> orders = queryFactory.selectFrom(order)
                .where(
                        userIdEq(user.getId())
                ).fetch();

        ArrayList<Order> initOrderList = new ArrayList<>();

        orders.forEach(o-> {
            if (o.getStatus().name() == "INIT") {
                initOrderList.add(o);
            }
        });

        int size = initOrderList.size();

        return size;
    }

    @Override
    public List<AggregateDto.PartnerOrderCountDto> getOrderCountByPartner() {
        StringPath orderCount = Expressions.stringPath("orderCount");

        List<AggregateDto.PartnerOrderCountDto> partnerOrderCountDtoList = queryFactory.select(
                Projections.constructor(AggregateDto.PartnerOrderCountDto.class,
                        partner.partnerName
                        , partner.id
                        , ExpressionUtils.as(JPAExpressions
                                        .select(orderItem.itemName.count())
                                        .from(order)
                                        .join(order.orderItemList, orderItem)
                                        .where(orderItem.partnerId.eq(partner.id))
                                , "orderCount")
                )
        )
                .from(partner)
                .groupBy(partner.partnerName, partner.id)
                .orderBy(orderCount.desc())
                .limit(10)
                .fetch();

        return partnerOrderCountDtoList;
    }

    private BooleanExpression userIdEq(Long userId) {
        if (userId == null) {
            return null;
        }

        return order.userId.eq(userId);
    }
}
