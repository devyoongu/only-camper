package dev.practice.order.domain.item;

import dev.practice.order.interfaces.item.ItemDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    String registerItem(ItemDto.RegisterItemRequest request, String partnerToken);
    void changeOnSale(String itemToken);
    void changeEndOfSale(String itemToken);
    ItemInfo.Main retrieveItemInfo(String itemToken);

    void updateItem(String itemToken, ItemDto.UpdateItemRequest request);

    List<Item> getItems();

    FileInfo fileSaveAndParsing(MultipartFile image) throws IOException;
}
