package dev.practice.order.interfaces.partner;

import dev.practice.order.domain.partner.PartnerCommand;
import dev.practice.order.domain.partner.PartnerCommand.PartnerCommandBuilder;
import dev.practice.order.interfaces.partner.PartnerDto.RegisterRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-13T17:05:16+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.1.1.jar, environment: Java 11.0.10 (JetBrains s.r.o.)"
)
@Component
public class PartnerDtoMapperImpl implements PartnerDtoMapper {

    @Override
    public PartnerCommand of(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        PartnerCommandBuilder partnerCommand = PartnerCommand.builder();

        partnerCommand.partnerName( request.getPartnerName() );
        partnerCommand.businessNo( request.getBusinessNo() );
        partnerCommand.email( request.getEmail() );

        return partnerCommand.build();
    }
}
