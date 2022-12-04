package rso.itemscompare.itemlistmanager.services.beans;

import rso.itemscompare.itemlistmanager.lib.ListEntry;
import rso.itemscompare.itemlistmanager.models.converters.ListEntryConverter;
import rso.itemscompare.itemlistmanager.models.entities.ListEntryEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ListEntryBean {
    private Logger log = Logger.getLogger(ListEntryBean.class.getName());

    @Inject
    private EntityManager em;

    public List<ListEntry> getListEntriesForList(Integer listId) {
        TypedQuery<ListEntryEntity> entriesQuery = em
                .createNamedQuery("ListEntry.getForList", ListEntryEntity.class)
                .setParameter("listId", listId);
        List<ListEntryEntity> entries = entriesQuery.getResultList();

        if (entries == null) {
            throw new NotFoundException();
        }

        return entries.stream().map(ListEntryConverter::toDto).collect(Collectors.toList());
    }
}
