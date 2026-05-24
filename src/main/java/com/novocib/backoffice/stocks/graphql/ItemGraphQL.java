package com.novocib.backoffice.stocks.graphql;

import com.novocib.backoffice.stocks.model.Item;
import com.novocib.backoffice.stocks.service.ItemService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ItemGraphQL {
    private final ItemService service;

    public ItemGraphQL(ItemService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Item> items() {
        return service.getAll();
    }

    @MutationMapping
    public Item createItem(@Argument String name, @Argument String description) {
        return service.create(name, description);
    }

    @MutationMapping
    public Item updateItem(@Argument Long id, @Argument String name, @Argument String description) {
        return service.update(id, name, description);
    }

    @MutationMapping
    public boolean deleteItem(@Argument Long id) {
        return service.delete(id);
    }
}
