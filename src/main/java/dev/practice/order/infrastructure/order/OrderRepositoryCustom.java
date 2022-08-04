package dev.practice.order.infrastructure.order;

import com.querydsl.core.Tuple;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.order.OrderInfo;
import dev.practice.order.domain.tupleDto.AggregateDto;
import dev.practice.order.interfaces.order.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> getOrderListMine(OrderSearchCondition condition, Pageable pageable);

    Page<AggregateDto.OrderItemListDto> getOrderListMine2(OrderSearchCondition condition, Pageable pageable);

    int getOrderListMineCount(SessionUser user);

    List<AggregateDto.PartnerOrderCountDto> getOrderCountByPartner();

    List<AggregateDto.OrderDateCountDto> getOrderCountByDate();
}
