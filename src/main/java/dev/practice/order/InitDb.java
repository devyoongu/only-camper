package dev.practice.order;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemCommand;
import dev.practice.order.domain.item.Status;
import dev.practice.order.domain.item.option.ItemOption;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.partner.PartnerCommand;
import dev.practice.order.domain.partner.PartnerInfo;
import dev.practice.order.domain.partner.PartnerService;
import dev.practice.order.interfaces.item.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
//@Profile({"default"})
public class InitDb {
    private final InitService initService;

//    @PostConstruct
    public void init() {
        initService.registerPartnerAndItem();
    }

    @Slf4j
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final PartnerService partnerService;
        private final ItemFacade itemFacade;

        public void registerPartnerAndItem() {
            String[] partners = {"스노우라인", "헬리녹스","고릴라캠핑", "캠핑천국"};
            String[] items = {"캠핑장비", "텐트장비","캠핑용품","텐트"};

            for (String partnerName : partners) {
                int index = Arrays.asList(partners).indexOf(partnerName);

                //파트너 생성
                PartnerCommand partnerCommand = createPartner(partnerName, "11111-11111", "lee36656@gmail.com");
                Partner partner = partnerCommand.toEntity();
                PartnerInfo partnerInfo = partnerService.registerPartner(partnerCommand);
                String partnerToken = partnerInfo.getPartnerToken();

                Item item = Item.builder()
                        .partnerToken(partnerToken)
                        .partner(partner)
                        .itemName(items[index])
                        .itemPrice(10000L)
                        .stockQuantity(100L)
                        .representImageName(index+1+".jpeg")
                        .representImagePath("/images/represent/"+(index+1)+".jpeg")
                        .representImageSize(0)
                        .build();

                ItemDto.RegisterItemRequest requestItem = new ItemDto.RegisterItemRequest(item);

                //item 생성
                ItemDto.RegisterItemRequest itemCommand = createItem(requestItem, partner);
                itemFacade.registerItem(itemCommand, partnerToken);

            }
        }

        public PartnerCommand createPartner(String partnerName, String businessNo, String email) {
            return PartnerCommand.builder()
                    .partnerName(partnerName)
                    .businessNo(businessNo)
                    .email(email)
                    .build();
        }

        /**item 생성*/
        public ItemDto.RegisterItemRequest createItem(ItemDto.RegisterItemRequest requestItem, Partner partner) {
            requestItem.setItemOptionGroupList(createOptionGroupList());

            Item item = requestItem.toEntity(partner);
            ItemDto.RegisterItemRequest itemDto = new ItemDto.RegisterItemRequest(item);

            return itemDto;
        }

        private List<ItemDto.RegisterItemOptionGroupRequest> createOptionGroupList() {
            String[] optionsGroup = {"색상", "사이즈"};

            List<ItemDto.RegisterItemOptionGroupRequest> optionGroupList = new ArrayList<>();

            for (int i = 0; i < optionsGroup.length; i++) {
                ItemDto.RegisterItemOptionGroupRequest option = createOptionGroup(i, optionsGroup[i]);
                optionGroupList.add(option);
            }

            return optionGroupList;
        }


        private ItemDto.RegisterItemOptionGroupRequest createOptionGroup(int ordring, String itemOptionGroupName) {
            return new ItemDto.RegisterItemOptionGroupRequest(ordring, itemOptionGroupName, createOptionList(ordring));
        }

        private List<ItemDto.RegisterItemOptionRequest> createOptionList(int ordring) {
            String[] options1 = {"빨강", "노랑"};
            String[] options2 = {"XL", "XX"};

            List<String[]> _list = new ArrayList<>();
            _list.add(options1);
            _list.add(options2);

            List<ItemDto.RegisterItemOptionRequest> optionList = new ArrayList<>();

            for (int i = 0; i < _list.size(); i++) {
                ItemDto.RegisterItemOptionRequest option = createOption(i, _list.get(ordring)[i], Long.parseLong("1000"), Long.parseLong("100"));
                optionList.add(option);
            }

            return optionList;
        }

        public ItemDto.RegisterItemOptionRequest createOption(Integer ordering, String itemOptionName, Long itemOptionPrice, Long optionStockQuantity) {

            return new ItemDto.RegisterItemOptionRequest(ordering, itemOptionName, itemOptionPrice, optionStockQuantity);
        }

    }

}
