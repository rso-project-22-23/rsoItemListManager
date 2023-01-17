package rso.itemscompare.itemlistmanager.services.beans;

import rso.itemscompare.itemlistmanager.models.entities.BasketEntryEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

@RequestScoped
public class BasketEntryBean {
    @Inject
    private EntityManager em;

    public List<BasketEntryEntity> getBasketEntriesForUser(int userId) {
        return em.createNamedQuery("BasketEntryEntity.getForUser", BasketEntryEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<BasketEntryEntity> getBasketEntry(int userId, int itemId) {
        return em.createNamedQuery("BasketEntryEntity.getEntry", BasketEntryEntity.class)
                .setParameter("userId", userId)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public int deleteBasketEntry(int userId, int itemId) {
        Query query = em.createNativeQuery("delete from basket_entry where user_id = ?userId and item_id = ?itemId")
                .setParameter("userId", userId)
                .setParameter("itemId", itemId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        tx.commit();
        return queryResult;
    }

    public int addBasketEntry(int userId, int itemId) {
        Query query = em.createNativeQuery("insert into basket_entry(user_id, item_id) values(?userId, ?itemId)")
                .setParameter("userId", userId)
                .setParameter("itemId", itemId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        tx.commit();
        return queryResult;
    }
}
