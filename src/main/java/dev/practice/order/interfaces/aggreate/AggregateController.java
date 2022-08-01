package dev.practice.order.interfaces.aggreate;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemInfo;
import dev.practice.order.domain.item.ItemService;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.partner.PartnerService;
import dev.practice.order.domain.tupleDto.ItemOrderCountDto;
import dev.practice.order.domain.tupleDto.PartnerItemCountDto;
import dev.practice.order.domain.user.User;
import dev.practice.order.infrastructure.aws.S3Uploader;
import dev.practice.order.infrastructure.item.ItemRepository;
import dev.practice.order.infrastructure.partner.PartnerRepository;
import dev.practice.order.infrastructure.user.UserRepository;
import dev.practice.order.interfaces.item.ItemDto;
import dev.practice.order.interfaces.item.ItemDtoMapper;
import dev.practice.order.interfaces.item.ItemSearchCondition;
import dev.practice.order.interfaces.item.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("aggregate")
@RequiredArgsConstructor
public class AggregateController {
    private final ItemRepository itemRepository;

    @GetMapping("/partner-item")
    @ResponseBody
    public List<PartnerItemCountDto> findPartnerWithItemCount() {
        List<PartnerItemCountDto> partnerWithItemList = itemRepository.findPartnerWithItemCount();

        log.error("partnerWithItemList ={}", partnerWithItemList);

        return partnerWithItemList;
    }

    @GetMapping("/item-ordercoount")
    @ResponseBody
    public List<ItemOrderCountDto> findItemOrderStatusList() {
        List<ItemOrderCountDto> itemOrderCountDtoList = itemRepository.findItemOrderStatusList();

        log.error("partnerWithItemList ={}", itemOrderCountDtoList);

        return itemOrderCountDtoList;
    }
}

