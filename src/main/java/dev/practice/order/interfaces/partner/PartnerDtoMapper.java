package dev.practice.order.interfaces.partner;

import dev.practice.order.domain.partner.PartnerCommand;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, //inject 전략
        unmappedTargetPolicy = ReportingPolicy.ERROR // 맵핑이되지 않으면 error 발생
)
public interface PartnerDtoMapper {
    PartnerCommand of(PartnerDto.RegisterRequest request);
}
