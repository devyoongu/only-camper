package dev.practice.order.domain.partner;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PartnerInfo {
    private final Long id;
    private final String partnerToken;
    private final String partnerName;
    private final String businessNo;
    private final String email;
    private final Partner.Status status;

    public PartnerInfo(Partner partner) {
        this.id = partner.getId();
        this.partnerToken = partner.getPartnerToken();
        this.partnerName = partner.getPartnerName();
        this.businessNo = partner.getBusinessNo();
        this.email = partner.getEmail();
        this.status = partner.getStatus();
    }

    //yg 추가
    public Partner toEntity() {
        return Partner.builder()
                .partnerName(getPartnerName())
                .businessNo(getBusinessNo())
                .email(getEmail())
                .build();
    }
}
