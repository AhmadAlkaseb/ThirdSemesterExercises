package Controllers;

import daos.ItemDAO;

import dtos.ItemDTO;
import persistence.Model.Item;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class ItemController {
    public static Handler getAll(ItemDAO dao) {
        return ctx -> {
            if (dao.getAll().isEmpty()) {
                ctx.status(HttpStatus.NOT_FOUND).result("No hotels were found.");
            } else {
                ctx.status(HttpStatus.OK).json(dao.getAll());
            }
        };
    }

    public static Handler delete(ItemDAO dao) {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("user_id"));
            Item foundItem = dao.getById(id);
            if (foundItem != null) {
                ItemDTO itemDTO = ItemDTO.builder()
                        .id(foundItem.getId())
                        .fullName(foundItem.getTitle())
                        .address(foundItem.getAddress())
                        //.rooms(foundItem.getRooms())
                        .build();

                dao.delete(foundItem.getId());

                ctx.status(HttpStatus.OK).json(itemDTO);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Item was not found.");
            }
        };
    }


    public static Handler getById(ItemDAO dao) {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (dao.getById(id) != null) {
                Item foundItem = dao.getById(id);
                ItemDTO itemDTO = ItemDTO.builder()
                        .id(foundItem.getId())
                        .title(foundItem.getTitle())
                        .description(foundItem.getDescription())
                        .price(foundItem.getPrice())
                        .fullName(foundItem.getFullName())
                        .address(foundItem.getAddress())
                        .phoneNr(foundItem.getPhoneNr())
                        .user(foundItem.getUser()) //har vi brug for en user også???

                       // .rooms(foundItem.getRooms())

                        .build();
                ctx.status(HttpStatus.OK).json(itemDTO);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("The item you are looking for does not exist.");
            }
        };
    }

    public static Handler create(ItemDAO dao) {
        return ctx -> {
            Item item = ctx.bodyAsClass(Item.class);
            if (item != null) {
                dao.create(item);
                ctx.status(HttpStatus.OK).json(item);
            } else {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Couldn't create the item with the given data.");
            }
        };
    }

    public static Handler update(ItemDAO dao) {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("user_id"));
            ItemDTO updatedItemDTO = ctx.bodyAsClass(ItemDTO.class);

            // Fetch the hotel from the database
            Item foundItem = dao.getById(id);
            //updatedItemDTO.setRooms(foundItem.getRooms());

            if (foundItem != null) {
                foundItem.setTitle(updatedItemDTO.getFullName());
                foundItem.setAddress(updatedItemDTO.getAddress());

                // Save the updated hotel to the database
                dao.update(foundItem);
                updatedItemDTO.setId(id);

                // Return the updated hotelDTO object as JSON response
                ctx.json(updatedItemDTO);
            } else {
                // Item with the provided ID not found
                ctx.status(HttpStatus.NOT_FOUND).result("Item not found.");
            }
        };
    }
}

