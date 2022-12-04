package rso.itemscompare.itemlistmanager.models.converters;

import rso.itemscompare.itemlistmanager.lib.ItemList;
import rso.itemscompare.itemlistmanager.models.entities.ItemListEntity;

public class ItemListConverter {
    public static ItemList toDto(ItemListEntity entity) {
        ItemList dto = new ItemList();
        dto.setListId(entity.getListId());
        dto.setUserId(entity.getUserId());
        dto.setListName(entity.getListName());

        return dto;
    }

    public static ItemListEntity toEntity(ItemList dto) {
        ItemListEntity entity = new ItemListEntity();
        entity.setListName(dto.getListName());

        return entity;
    }
}
