package rso.itemscompare.itemlistmanager.api.v1.resources;

import org.json.JSONObject;
import rso.itemscompare.itemlistmanager.lib.ItemList;
import rso.itemscompare.itemlistmanager.lib.ListEntry;
import rso.itemscompare.itemlistmanager.models.entities.BasketEntryEntity;
import rso.itemscompare.itemlistmanager.models.entities.FavouritesEntryEntity;
import rso.itemscompare.itemlistmanager.services.beans.BasketEntryBean;
import rso.itemscompare.itemlistmanager.services.beans.FavouritesEntryBean;
import rso.itemscompare.itemlistmanager.services.beans.ItemListBean;
import rso.itemscompare.itemlistmanager.services.beans.ListEntryBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemListManagerResource {
    private Logger log = Logger.getLogger(ItemListManagerResource.class.getName());

    @Inject
    private ItemListBean itemListBean;
    @Inject
    private ListEntryBean listEntryBean;
    @Inject
    private BasketEntryBean basketEntryBean;
    @Inject
    private FavouritesEntryBean favouritesEntryBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("/itemlists")
    public Response getItemList() {
        List<ItemList> itemLists = itemListBean.getItemListFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(itemLists).build();
    }

    @GET
    @Path("/itemlists/{itemListId}")
    public Response getItemList(@PathParam("itemListId") Integer itemListId) {
        ItemList itemList = itemListBean.getItemList(itemListId);
        if (itemList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(itemList).build();
    }

    @GET
    @Path("/itemlists/{itemListId}/entries")
    public Response getItemListEntries(@PathParam("itemListId") Integer itemListId) {
        List<ListEntry> entries;
        try {
            entries = listEntryBean.getListEntriesForList(itemListId);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(entries).build();
    }

    @GET
    @Path("/basket/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBasketForUser(@PathParam("userId") int userId) {
        List<BasketEntryEntity> l = basketEntryBean.getBasketEntriesForUser(userId);
        if (l.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ArrayList<Integer> itemIds = new ArrayList<>();
        for (BasketEntryEntity e : l) {
            itemIds.add(e.getItemId());
        }

        return Response.status(Response.Status.OK).entity(itemIds).build();
    }

    @POST
    @Path("/basket/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteFromBasket(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<BasketEntryEntity> l = basketEntryBean.getBasketEntry(userId, itemId);

        if (l.size() != 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        int result = basketEntryBean.deleteBasketEntry(userId, itemId);
        if (result != 1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorResponse("Error when deleting entry from basket"))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @POST
    @Path("/basket/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addToBasket(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<BasketEntryEntity> l = basketEntryBean.getBasketEntry(userId, itemId);
        if (!l.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Basket entry already exists"))
                    .build();
        }

        int result = basketEntryBean.deleteBasketEntry(userId, itemId);
        if (result != 1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorResponse("Error when adding entry to basket"))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @GET
    @Path("/favourites/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFavouritesForUser(@PathParam("userId") int userId) {
        List<FavouritesEntryEntity> l = favouritesEntryBean.getFavouritesEntriesForUser(userId);
        if (l.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ArrayList<Integer> itemIds = new ArrayList<>();
        for (FavouritesEntryEntity e : l) {
            itemIds.add(e.getItemId());
        }

        return Response.status(Response.Status.OK).entity(itemIds).build();
    }

    @POST
    @Path("/favourites/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteFromFavourites(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<FavouritesEntryEntity> l = favouritesEntryBean.getFavouritesEntry(userId, itemId);

        if (l.size() != 1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        int result = favouritesEntryBean.deleteFavouritesEntry(userId, itemId);
        if (result != 1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorResponse("Error when deleting entry from favourites"))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @POST
    @Path("/favourites/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addToFavourites(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<FavouritesEntryEntity> l = favouritesEntryBean.getFavouritesEntry(userId, itemId);
        if (!l.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Favourites entry already exists"))
                    .build();
        }

        int result = favouritesEntryBean.deleteFavouritesEntry(userId, itemId);
        if (result != 1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorResponse("Error when adding entry to favourites"))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    private JsonObject buildErrorResponse(String message) {
        return Json.createObjectBuilder().add("Error", message).build();
    }
}
