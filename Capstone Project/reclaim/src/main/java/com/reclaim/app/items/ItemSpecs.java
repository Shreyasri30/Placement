package com.reclaim.app.items;

import org.springframework.data.jpa.domain.Specification;

public class ItemSpecs {

    public static Specification<Item> typeEquals(String type) {
        return (root, query, cb) -> (type == null || type.isBlank()) ? cb.conjunction()
                : cb.equal(root.get("type"), Item.Type.valueOf(type));
    }

    public static Specification<Item> statusEquals(String status) {
        return (root, query, cb) -> (status == null || status.isBlank()) ? cb.conjunction()
                : cb.equal(root.get("status"), Item.Status.valueOf(status));
    }

    public static Specification<Item> categoryEquals(String category) {
        return (root, query, cb) -> (category == null || category.isBlank()) ? cb.conjunction()
                : cb.like(cb.lower(root.get("category")), "%" + category.trim().toLowerCase() + "%");
    }

    public static Specification<Item> locationLike(String location) {
        return (root, query, cb) -> (location == null || location.isBlank()) ? cb.conjunction()
                : cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase() + "%");
    }

    public static Specification<Item> keyword(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank())
                return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like));
        };
    }
}
