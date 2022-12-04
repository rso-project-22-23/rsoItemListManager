package rso.itemscompare.itemlistmanager.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "item_list")
@NamedQueries(value = {
        @NamedQuery(name = "ItemListEntity.getAll",
                query = "SELECT il FROM ItemListEntity il")
})
public class ItemListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", nullable = false)
    private Integer listId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "list_name", nullable = false)
    private String listName;

    public Integer getListId() {
        return listId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
