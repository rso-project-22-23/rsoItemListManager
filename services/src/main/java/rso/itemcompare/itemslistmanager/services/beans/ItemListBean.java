package rso.itemscompare.itemlistmanager.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.itemscompare.itemlistmanager.lib.ItemList;
import rso.itemscompare.itemlistmanager.models.converters.ItemListConverter;
import rso.itemscompare.itemlistmanager.models.entities.ItemListEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ItemListBean {
    private Logger log = Logger.getLogger(ItemListBean.class.getName());

    @Inject
    private EntityManager em;

    public List<ItemList> getItemList() {
        TypedQuery<ItemListEntity> query = em.createNamedQuery(
                "ItemListEntity.getAll", ItemListEntity.class);
        List<ItemListEntity> resultList = query.getResultList();
        return resultList.stream().map(ItemListConverter::toDto).collect(Collectors.toList());
    }

    public List<ItemList> getItemListFilter(UriInfo uriInfo) {
        QueryParameters params = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).build();

        return JPAUtils.queryEntities(em, ItemListEntity.class, params).stream()
                .map(ItemListConverter::toDto).collect(Collectors.toList());
    }

    public ItemList getItemList(Integer id) {
        ItemListEntity entity = em.find(ItemListEntity.class, id);
        if (entity == null) {
            throw new NotFoundException();
        }

        return ItemListConverter.toDto(entity);
    }
}
