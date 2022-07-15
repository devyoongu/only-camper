package dev.practice.order;

import dev.practice.order.application.item.ItemFacade;
import dev.practice.order.domain.item.Item;
import dev.practice.order.domain.item.ItemCommand;
import dev.practice.order.domain.item.Status;
import dev.practice.order.domain.partner.PartnerCommand;
import dev.practice.order.domain.partner.PartnerInfo;
import dev.practice.order.domain.partner.PartnerService;
import dev.practice.order.interfaces.item.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
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
            String[] partners = {"강원건기", "춘천건기","파란자전거", "노랑풍선"};
            String[] items = {"게이밍의자", "알파스캔모니터","미니벨로","우산"};
//            for (int i = 0; i < 10; i++) {
                for (String partner : partners) {
                    int index = Arrays.asList(partners).indexOf(partner);

                    //파트너 생성
                    PartnerCommand partnerCommand = createPartner(partner, "11111-11111", "lee36656@gmail.com");
                    PartnerInfo partnerInfo = partnerService.registerPartner(partnerCommand);
                    String partnerToken = partnerInfo.getPartnerToken();

                    Item item = Item.builder()
                            .partnerToken(partnerToken)
                            .partnerId(partnerInfo.getId())
                            .itemName(items[index])
                            .itemPrice(10000L)
                            .stockQuantity(100L)
                            .representImageName(index+1+".jpeg")
                            .representImagePath("/images/represent/"+(index+1)+".jpeg")
                            .representImageSize(0)
                            .build();

                    //item 생성
                    ItemDto.RegisterItemRequest itemCommand = createItem(item);
                    itemFacade.registerItem(itemCommand, partnerToken);

                }
//            }
        }

        public PartnerCommand createPartner(String partnerName, String businessNo, String email) {
            return PartnerCommand.builder()
                    .partnerName(partnerName)
                    .businessNo(businessNo)
                    .email(email)
                    .build();
        }

        /**item 생성*/
        public ItemDto.RegisterItemRequest createItem(Item item) {

            ItemDto.RegisterItemRequest itemDto = new ItemDto.RegisterItemRequest(item.getPartnerToken(), item.getItemToken()
                    , item.getItemName(), item.getItemPrice(), item.getStockQuantity(), "PREPARE"
                    , item.getRepresentImagePath(), item.getRepresentImageSize(), item.getRepresentImageName()
                    , createOptionGroupList());

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
