package rso.itemscompare.itemlistmanager.services.beans;

import rso.itemscompare.itemlistmanager.models.entities.FavouritesEntryEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

@RequestScoped
public class FavouritesEntryBean {
    @Inject
    private EntityManager em;

    public List<FavouritesEntryEntity> getFavouritesEntriesForUser(int userId) {
        return em.createNamedQuery("FavouritesEntryEntity.getForUser", FavouritesEntryEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<FavouritesEntryEntity> getFavouritesEntry(int userId, int itemId) {
        return em.createNamedQuery("FavouritesEntryEntity.getEntry", FavouritesEntryEntity.class)
                .setParameter("userId", userId)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public int deleteFavouritesEntry(int userId, int itemId) {
        Query query = em.createNativeQuery("delete from favourites_entry where user_id = ?userId and item_id = ?itemId")
                .setParameter("userId", userId)
                .setParameter("itemId", itemId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        tx.commit();
        return queryResult;
    }

    public int addFavouritesEntry(int userId, int itemId) {
        Query query = em.createNativeQuery("insert into favourites_entry(user_id, item_id) values(?userId, ?itemId)")
                .setParameter("userId", userId)
                .setParameter("itemId", itemId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        tx.commit();
        return queryResult;
    }
}

