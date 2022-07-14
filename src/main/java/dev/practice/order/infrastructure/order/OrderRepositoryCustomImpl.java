package dev.practice.order.infrastructure.order;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderInfo;
import dev.practice.order.domain.order.OrderInfoMapper;
import dev.practice.order.domain.order.QOrder;
import dev.practice.order.interfaces.order.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static dev.practice.order.domain.item.QItem.item;
import static dev.practice.order.domain.order.QOrder.*;

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
    public Page<OrderInfo.Main> getOrderListMine(OrderSearchCondition condition, Pageable pageable) {

        QueryResults<Order> results = queryFactory.selectFrom(order)
                .where(
                        userIdEq(condition.getUserId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();// result + count

        List<Order> contents = results.getResults();
        long total = results.getTotal();


        List<OrderInfo.Main> orderInfoList = contents.stream()
                .map(o -> orderInfoMapper.of(o, o.getOrderItemList()))
                .collect(Collectors.toList());



        return new PageImpl<>(orderInfoList, pageable, total);

    }

    @Override
    public int getOrderListMineCount(SessionUser user) {

        List<Order> orders = queryFactory.selectFrom(order)
                .where(
                        userIdEq(user.getId())
                ).fetch();

        int size = orders.size();

        return size;
    }

    private BooleanExpression userIdEq(Long userId) {
        if (userId == null) {
            return null;
        }

        return order.userId.eq(userId);
    }
}
