package dev.practice.order.domain.partner;

import java.util.List;

public interface PartnerService {
    PartnerInfo registerPartner(PartnerCommand command);
    PartnerInfo getPartnerInfo(String partnerToken);
    PartnerInfo enablePartner(String partnerToken);
    PartnerInfo disablePartner(String partnerToken);

    void updatePartner(String partnerToken, PartnerCommand command);

    List<Partner> getPartners();

}
