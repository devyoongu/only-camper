package dev.practice.order.domain.partner;

import java.util.List;

public interface PartnerReader {
    Partner getPartner(Long partnerId);
    Partner getPartner(String partnerToken);
    List<Partner> getPartnerList();
}
