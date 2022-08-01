package dev.practice.order.domain.tupleDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PartnerItemCountDto {

    private Long partnerId;
    private String partnerName;
    private Long itemCount;

    @QueryProjection
    public PartnerItemCountDto(Long partnerId, String partnerName, Long itemCount) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.itemCount = itemCount;
    }
}
