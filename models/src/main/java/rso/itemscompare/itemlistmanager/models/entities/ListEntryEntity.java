package rso.itemscompare.itemlistmanager.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(EntryKey.class)
@Table(name = "list_entry")
@NamedQueries(value = {
        @NamedQuery(name = "ListEntry.getForList",
                query = "SELECT le FROM  ItemListEntity ile JOIN ListEntryEntity le WHERE ile.listId = :listId AND le.listId = :listId")
})
public class ListEntryEntity {
    @Id
    @Column(name = "list_id", nullable = false)
    private Integer listId;

    @Id
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

}

class EntryKey implements Serializable {
    private int listId;
    private int itemId;
}
