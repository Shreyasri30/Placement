package com.reclaim.app.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    long countByStatus(Item.Status status);

    java.util.List<Item> findByCreatedBy(com.reclaim.app.user.User user);
}
