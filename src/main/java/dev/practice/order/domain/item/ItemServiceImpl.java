package dev.practice.order.domain.item;

import dev.practice.order.domain.partner.PartnerReader;
import dev.practice.order.interfaces.item.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final PartnerReader partnerReader;
    private final ItemStore itemStore;
    private final ItemReader itemReader;
    private final ItemOptionSeriesFactory itemOptionSeriesFactory;

    @Override
    @Transactional
    public String registerItem(ItemDto.RegisterItemRequest itemDto, String partnerToken) {
        //1. get partner
        var partner = partnerReader.getPartner(partnerToken);
        //2. command -> Item 객체 변환 및 partnerId로 item 조회
        var initItem = itemDto.toEntity(partner);
        //3. item 객체 저장
        var item = itemStore.store(initItem);
        //4. factory를 이용한 itemOptionGroup, itemOption 저장
        itemOptionSeriesFactory.store(itemDto, item);
        return item.getItemToken();
    }

    //추가 yg
    @Override
    @Transactional
    public void updateItem(String itemToken, ItemDto.UpdateItemRequest request) {
        Item item = itemReader.getItemBy(itemToken);
        item.updateItem(request);

        itemOptionSeriesFactory.update(request, item);
    }

    @Override
    @Transactional
    public void changeOnSale(String itemToken) {
        var item = itemReader.getItemBy(itemToken);
        item.changeOnSale();
    }

    @Override
    @Transactional
    public void changeEndOfSale(String itemToken) {
        var item = itemReader.getItemBy(itemToken);
        item.changeEndOfSale();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemInfo.Main retrieveItemInfo(String itemToken) {
        //1. item 조회
        var item = itemReader.getItemBy(itemToken);
        //2. item series 조회
        var itemOptionGroupInfoList = itemReader.getItemOptionSeries(item);
        return new ItemInfo.Main(item, itemOptionGroupInfoList);
    }

    @Override
    //todo : dto 변환
    public List<Item> getItems() {
        return itemReader.getItemList();
    }

    @Override
    public FileInfo fileSaveAndParsing(MultipartFile multipartFile) throws IOException {
        FileInfo fileInfo = new FileInfo();

        if (multipartFile.isEmpty()) {
            fileInfo.setFileName("");
            fileInfo.setFilePath("");
            fileInfo.setFileSize(0);
            return fileInfo;
        }

        String[] strArray = multipartFile.getOriginalFilename().split("\\.");
        log.info("origin type : {}", strArray[strArray.length - 1]);
        String fileName = "" + LocalDate.now() + System.nanoTime() + "." + strArray[strArray.length - 1];

        String absolutePath = new File("").getAbsolutePath() + "/src/main/resources/static/images/represent/";
        String path = "/images/represent/" + fileName;

        log.info("type : {}, name : {}, path : {}", multipartFile.getContentType(), fileName, path);

        File file = new File(absolutePath + fileName);

        if (!file.exists()) {
            file.mkdirs();
        }

        multipartFile.transferTo(file);

        fileInfo.setFileName(fileName);
        fileInfo.setFileSize(multipartFile.getSize());
        fileInfo.setFilePath(path);
        return fileInfo;
    }
}
