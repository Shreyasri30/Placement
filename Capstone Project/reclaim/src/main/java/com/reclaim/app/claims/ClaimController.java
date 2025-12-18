package com.reclaim.app.claims;

import com.reclaim.app.items.Item;
import com.reclaim.app.items.ItemRepository;
import com.reclaim.app.user.User;
import com.reclaim.app.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@PreAuthorize("isAuthenticated()")
public class ClaimController {

    private final ClaimRepository claims;
    private final ItemRepository items;
    private final UserRepository users;

    public ClaimController(ClaimRepository c, ItemRepository i, UserRepository u) {
        this.claims = c;
        this.items = i;
        this.users = u;
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

    @PostMapping("/{itemId}")
    public ResponseEntity<?> claim(@PathVariable Long itemId) {
        Item item = items.findById(itemId).orElseThrow();
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        Claim c = new Claim();
        c.setItem(item);
        c.setClaimant(me);
        claims.save(c);

        return ResponseEntity.ok(Map.of("message", "Claim submitted"));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> list(@PathVariable Long itemId) {
        Item item = items.findById(itemId).orElseThrow();
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        // Allow owner or admin
        boolean isOwner = item.getCreatedBy().getId().equals(userId);
        boolean isAdmin = me.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        return ResponseEntity.ok(claims.findByItemId(itemId));
    }

    @GetMapping("/item/{itemId}/check")
    public ResponseEntity<?> check(@PathVariable Long itemId) {
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        Claim claim = claims.findOptionalByItemIdAndClaimant(itemId, me).orElse(null);
        return ResponseEntity.ok(Map.of(
                "hasClaimed", claim != null,
                "status", claim != null ? claim.getStatus() : ""));
    }

    @GetMapping("/my")
    public ResponseEntity<?> myClaims() {
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();
        return ResponseEntity.ok(claims.findByClaimant(me));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        Claim c = claims.findById(id).orElseThrow();
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        // Allow owner or admin
        boolean isOwner = c.getItem().getCreatedBy().getId().equals(userId);
        boolean isAdmin = me.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        c.setStatus(Claim.Status.ACCEPTED);
        claims.save(c);

        // Update item status
        Item item = c.getItem();
        item.setStatus(Item.Status.CLAIMED);
        items.save(item);

        return ResponseEntity.ok(Map.of("message", "Claim accepted"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        Claim c = claims.findById(id).orElseThrow();
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        // Allow owner or admin
        boolean isOwner = c.getItem().getCreatedBy().getId().equals(userId);
        boolean isAdmin = me.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        c.setStatus(Claim.Status.REJECTED);
        claims.save(c);
        return ResponseEntity.ok(Map.of("message", "Claim rejected"));
    }
}
