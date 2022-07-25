package dev.practice.order.interfaces.order.gift;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.application.order.OrderFacade;
import dev.practice.order.application.order.gift.GiftFacade;
import dev.practice.order.common.response.CommonResponse;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemReader;
import dev.practice.order.domain.order.gift.GiftApiCaller;
import dev.practice.order.domain.user.User;
import dev.practice.order.interfaces.order.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gift-orders")
public class GiftOrderApiController {
    private final OrderFacade orderFacade;
    private final GiftFacade giftFacade;
    private final GiftOrderDtoMapper giftOrderDtoMapper;
    private final GiftApiCaller giftApiCaller;
    private final ItemReader itemReader;

    private final ItemFacade itemFacade;

    /**
     * gift api 호출
     * addOrder에서 호출
     *  */

    @PostMapping("/gift-init/{itemToken}/{orderCount}")
    public CommonResponse registerGift(@RequestBody @Valid GiftOrderDto.RegisterOrderRequest request, BindingResult bindingResult
            , @PathVariable("itemToken") String itemToken, @PathVariable("orderCount") Long orderCount) {

        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                return CommonResponse.fail(null,error.getDefaultMessage());
            }
        }

        //1. itemToken 통한 item 정보 얻기
        ItemInfo.Main itemInfo = itemFacade.retrieveItemInfo(itemToken);

        //2. itemToken 통한 item의 옵션리스트 얻기
        Item itemBy = itemReader.getItemBy(itemToken);
        List<ItemInfo.ItemOptionGroupInfo> itemOptionSeries = itemReader.getItemOptionSeries(itemBy);

        List<GiftOrderDto.RegisterOrderItem> giftItemList = itemOptionSeries.stream()
                .map(og -> new GiftOrderDto.RegisterOrderItem(og, itemInfo, orderCount))
                .collect(Collectors.toList());

        request.setOrderItemList(giftItemList);

        String s = giftApiCaller.registerGift(request);

        return CommonResponse.success(s);
    }


    /**
     * 선물하기 주문 생성
     * gift에서 호출
     *  */

    @PostMapping("/init")
    public CommonResponse registerOrder(@RequestBody @Valid GiftOrderDto.RegisterOrderRequest request) {
        request.setReceiverName(request.getGiftReceiverName());//임시
        var orderCommand = giftOrderDtoMapper.of(request);
        var result = orderFacade.registerOrder(orderCommand);
        var response = giftOrderDtoMapper.of(result);
        return CommonResponse.success(response);
    }

    /**
     * 선물하기 주문 결제
     * order에서 호출
     * */
    @PostMapping("/payment-order")
    public CommonResponse paymentOrder(@RequestBody @Valid GiftOrderDto.PaymentRequest request, BindingResult bindingResult) {
        if (bindingResult.getAllErrors().size() > 0) {
            log.error("bindingResult error");
        }

        var orderPaymentCommand = giftOrderDtoMapper.of(request);
        giftFacade.paymentOrder(orderPaymentCommand);
        return CommonResponse.success("OK");
    }

    @PostMapping("/{orderToken}/update-receiver-info")
    public CommonResponse updateReceiverInfo(
            @PathVariable String orderToken,
            @RequestBody @Valid GiftOrderDto.UpdateReceiverInfoReq request
    ) {
        var orderCommand = request.toCommand();
        orderFacade.updateReceiverInfo(orderToken, orderCommand);
        return CommonResponse.success("OK");
    }
}
