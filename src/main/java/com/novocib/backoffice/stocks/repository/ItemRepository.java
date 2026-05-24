
package com.novocib.backoffice.stocks.repository;

import com.novocib.backoffice.stocks.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item, Long> {}
