package rso.itemscompare.itemlistmanager.api.v1.resources;

import rso.itemscompare.itemlistmanager.lib.ItemList;
import rso.itemscompare.itemlistmanager.lib.ListEntry;
import rso.itemscompare.itemlistmanager.services.beans.ItemListBean;
import rso.itemscompare.itemlistmanager.services.beans.ListEntryBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/itemlists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemListManagerResource {
    private Logger log = Logger.getLogger(ItemListManagerResource.class.getName());

    @Inject
    private ItemListBean itemListBean;
    @Inject
    private ListEntryBean listEntryBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getItemList() {
        List<ItemList> itemLists = itemListBean.getItemListFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(itemLists).build();
    }

    @GET
    @Path("/{itemListId}")
    public Response getItemList(@PathParam("itemListId") Integer itemListId) {
        ItemList itemList = itemListBean.getItemList(itemListId);
        if (itemList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(itemList).build();
    }

    @GET
    @Path("{itemListId}/entries")
    public Response getItemListEntries(@PathParam("itemListId") Integer itemListId) {
        List<ListEntry> entries;
        try {
            entries = listEntryBean.getListEntriesForList(itemListId);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(entries).build();
    }
}
