package rso.itemscompare.itemlistmanager.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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
    @Operation(summary = "Gets item lists", description = "Retrieves item lists from DB.")
    @APIResponses({
            @APIResponse(description = "Item lists retrieved", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class, type = SchemaType.OBJECT))),
    })
    public Response getItemList() {
        List<ItemList> itemLists = itemListBean.getItemListFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(itemLists).build();
    }

    @GET
    @Path("/itemlists/{itemListId}")
    @Operation(summary = "Get item list", description = "Gets specific item list by its ID")
    @APIResponses({
            @APIResponse(description = "Item list retrieved", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ItemList.class))),
            @APIResponse(description = "If item list is not found", responseCode = "404"),
    })
    public Response getItemList(@PathParam("itemListId") Integer itemListId) {
        ItemList itemList = itemListBean.getItemList(itemListId);
        if (itemList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(itemList).build();
    }

    @GET
    @Path("/itemlists/{itemListId}/entries")
    @Operation(summary = "Get item list entries", description = "Retrieves all entries in specific item list")
    @APIResponses({
            @APIResponse(description = "Item list entries retrieved", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class, type = SchemaType.OBJECT))),
            @APIResponse(description = "If item list is not found", responseCode = "404"),
    })
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
    @Operation(summary = "Get basket", description = "Retrieves all entries in basket for specific user")
    @APIResponses({
            @APIResponse(description = "Basket entries retrieved", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class, type = SchemaType.INTEGER))),
            @APIResponse(description = "No basket entries for this user id are found", responseCode = "404"),
    })
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
    @Operation(summary = "Delete from basket", description = "Deletes specific entry for specific user from basket")
    @APIResponses({
            @APIResponse(description = "Basket entry successfully deleted", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = boolean.class))),
            @APIResponse(description = "Specified basket entry does not exist", responseCode = "404"),
    })
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
    @Operation(summary = "Add to basket", description = "Adds specific item to basket for specific user")
    @APIResponses({
            @APIResponse(description = "Basket entry successfully added", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = boolean.class))),
            @APIResponse(description = "This entry already exists", responseCode = "400"),
    })
    public Response addToBasket(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<BasketEntryEntity> l = basketEntryBean.getBasketEntry(userId, itemId);
        if (!l.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Basket entry already exists"))
                    .build();
        }

        int result = basketEntryBean.addBasketEntry(userId, itemId);
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
    @Operation(summary = "Get favourites", description = "Retrieves all favourite items for specific user")
    @APIResponses({
            @APIResponse(description = "Favourites entries retrieved", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class, type = SchemaType.INTEGER))),
            @APIResponse(description = "No favourites for this user id are found", responseCode = "404"),
    })
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
    @Operation(summary = "Delete from favourites", description = "Deletes specific favourites entry for specific user")
    @APIResponses({
            @APIResponse(description = "Favourites entry successfully deleted", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = boolean.class))),
            @APIResponse(description = "Specified favourites entry does not exist", responseCode = "404"),
    })
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
    @Operation(summary = "Add to favourites", description = "Adds specific item to favourites for specific user")
    @APIResponses({
            @APIResponse(description = "Favourites entry successfully added", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = boolean.class))),
            @APIResponse(description = "This entry already exists", responseCode = "400"),
    })
    public Response addToFavourites(@HeaderParam("userId") int userId, @HeaderParam("itemId") int itemId) {
        List<FavouritesEntryEntity> l = favouritesEntryBean.getFavouritesEntry(userId, itemId);
        if (!l.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Favourites entry already exists"))
                    .build();
        }

        int result = favouritesEntryBean.addFavouritesEntry(userId, itemId);
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
