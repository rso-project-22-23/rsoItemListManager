package rso.itemscompare.itemlistmanager.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(BasketKey.class)
@Table(name = "basket_entry")
@NamedQueries(value = {
        @NamedQuery(name = "BasketEntryEntity.getForUser",
                query = "SELECT be FROM BasketEntryEntity be WHERE be.userId = :userId"),
        @NamedQuery(name = "BasketEntryEntity.getEntry",
                query = "SELECT be FROM BasketEntryEntity  be WHERE be.userId = :userId AND be.itemId = :itemId")
})
public class BasketEntryEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Id
    @Column(name = "item_id", nullable = false)
    private int itemId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}

class BasketKey implements Serializable {
    private int userId;
    private int itemId;
}