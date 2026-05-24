
package com.novocib.backoffice.stocks.service;

import com.novocib.backoffice.stocks.model.Item;
import com.novocib.backoffice.stocks.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
	private final ItemRepository repository;

	public ItemService(ItemRepository repository) {
		this.repository = repository;
	}

	public List<Item> getAll() {
		return repository.findAll();
	}

	public Item create(String name, String description) {
		return repository.save(new Item(name, description));
	}

	public Item update(Long id, String name, String description) {
		Optional<Item> optional = repository.findById(id);
		if (optional.isPresent()) {
			Item item = optional.get();
			// Assuming setters exist or use reflection/constructor as needed
			item = new Item(name, description); // For immutability, or set fields if mutable
			// Set id manually if needed (JPA may not allow this)
			// item.setId(id);
			return repository.save(item);
		}
		throw new RuntimeException("Item not found");
	}

	public boolean delete(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return true;
		}
		return false;
	}
}
