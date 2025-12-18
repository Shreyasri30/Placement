package com.reclaim.app.admin;

import com.reclaim.app.items.Item;
import com.reclaim.app.items.ItemCreateRequest;
import com.reclaim.app.items.ItemRepository;
import com.reclaim.app.claims.ClaimRepository;
import com.reclaim.app.user.User;
import com.reclaim.app.user.UserRepository;
import com.reclaim.app.user.Role;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemRepository items;
    private final UserRepository users;
    private final ClaimRepository claims;

    public AdminController(ItemRepository items, UserRepository users, ClaimRepository claims) {
        this.items = items;
        this.users = users;
        this.claims = claims;
    }

    @GetMapping("/items")
    public ResponseEntity<?> allItems() {
        return ResponseEntity.ok(items.findAll(Sort.by("createdAt").descending()));
    }

    @GetMapping("/users")
    public ResponseEntity<?> allUsers() {
        return ResponseEntity.ok(users.findAll(Sort.by("id").ascending()));
    }

    @PostMapping("/users/{id}/toggle-block")
    public ResponseEntity<?> toggleBlock(@PathVariable long id) {
        User u = users.findById(id).orElseThrow();
        u.setBlocked(!u.isBlocked());
        users.save(u);
        return ResponseEntity.ok(Map.of("message", u.isBlocked() ? "User blocked" : "User unblocked"));
    }

    private Long uid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return users.findByEmail(email).map(User::getId).orElse(null);
        }
        return null;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        User u = users.findById(id).orElseThrow();

        // Ensure we don't delete an admin via this generic endpoint accidentally
        if (u.getRole() == Role.ADMIN && users.countByRole(Role.ADMIN) <= 1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot delete the last administrator"));
        }

        // 1. Delete all items created by this user
        items.deleteAll(items.findByCreatedBy(u));

        // 2. Delete all claims submitted by this user
        claims.deleteAll(claims.findByClaimant(u));

        // 3. Delete the user
        users.delete(u);
        return ResponseEntity.ok(Map.of("message", "User and all their activity have been deleted permanently"));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody ItemCreateRequest req) {
        Item item = items.findById(id).orElseThrow();
        item.setTitle(req.title);
        item.setDescription(req.description);
        item.setCategory(req.category);
        item.setLocation(req.location);
        item.setEventDate(req.eventDate);
        item.setContactValue(req.contactValue);
        items.save(item);
        return ResponseEntity.ok(Map.of("message", "Item updated by admin"));
    }

    @PostMapping("/items/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable long id) {
        Item item = items.findById(id).orElseThrow();
        item.setStatus(Item.Status.RESOLVED);
        items.save(item);
        return ResponseEntity.ok(Map.of("message", "Item marked as resolved by admin"));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        items.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Item deleted by admin"));
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "totalItems", items.count(),
                "totalClaims", claims.count(),
                "totalUsers", users.count(),
                "activeItems", items.countByStatus(Item.Status.ACTIVE),
                "resolvedItems", items.countByStatus(Item.Status.RESOLVED));
    }
}
