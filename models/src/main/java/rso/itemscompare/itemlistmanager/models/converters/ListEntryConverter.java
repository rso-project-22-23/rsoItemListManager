package rso.itemscompare.itemlistmanager.models.converters;

import rso.itemscompare.itemlistmanager.lib.ListEntry;
import rso.itemscompare.itemlistmanager.models.entities.ListEntryEntity;

public class ListEntryConverter {
    public static ListEntry toDto(ListEntryEntity entity) {

        ListEntry dto = new ListEntry();
        dto.setListId(entity.getListId());
        dto.setitemId(entity.getItemId());

        return dto;

    }

    public static ListEntryEntity toEntity(ListEntry dto) {
        ListEntryEntity entity = new ListEntryEntity();
        entity.setListId(dto.getListId());
        entity.setItemId(dto.getitemId());

        return entity;

    }
}
