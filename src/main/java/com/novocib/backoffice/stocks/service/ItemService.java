
package com.novocib.backoffice.stocks.service;

import com.novocib.backoffice.stocks.model.Item;
import com.novocib.backoffice.stocks.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
	private final ItemRepository repository;

	public ItemService(ItemRepository repository) {
		this.repository = repository;
	}

	public List<Item> getAll() {
		return repository.findAll();
	}

	public Item getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Item not found"));
	}

	public Item create(String name, String description) {
		return repository.save(new Item(name, description));
	}

	public Item update(Long id, String name, String description) {
		Item item = getById(id);
		item.setName(name);
		item.setDescription(description);
		return repository.save(item);
	}

	public boolean delete(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return true;
		}
		return false;
	}
}
