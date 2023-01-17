package rso.itemscompare.itemlistmanager.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(FavouritesKey.class)
@Table(name = "favourites_entry")
@NamedQueries(value = {
        @NamedQuery(name = "FavouritesEntryEntity.getForUser",
                query = "SELECT fe FROM FavouritesEntryEntity fe WHERE fe.userId = :userId"),
        @NamedQuery(name = "FavouritesEntryEntity.getEntry",
                query = "SELECT fe FROM FavouritesEntryEntity  fe WHERE fe.userId = :userId AND fe.itemId = :itemId")
})
public class FavouritesEntryEntity {
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

class FavouritesKey implements Serializable {
    private int userId;
    private int itemId;
}
