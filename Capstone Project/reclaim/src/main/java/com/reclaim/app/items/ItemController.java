package com.reclaim.app.items;

import com.reclaim.app.user.User;
import com.reclaim.app.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository items;
    private final UserRepository users;

    public ItemController(ItemRepository items, UserRepository users) {
        this.items = items;
        this.users = users;
    }

    private Long uid() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser"))
            return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return users.findByEmail(email).map(User::getId).orElse(null);
        }
        return null;
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@jakarta.validation.Valid @RequestBody ItemCreateRequest body) {
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();

        Item i = new Item();
        i.setType(body.type);
        i.setTitle(body.title);
        i.setCategory(body.category);
        i.setDescription(body.description);
        i.setLocation(body.location);
        i.setEventDate(body.eventDate);
        i.setContactMethod(body.contactMethod);
        i.setContactValue(body.contactValue);
        i.setCreatedBy(me);

        Item saved = items.save(i);
        return ResponseEntity.ok(Map.of("message", "Item posted", "itemId", saved.getId()));
    }

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sort) {
        Specification<Item> spec = Specification.where(ItemSpecs.typeEquals(type))
                .and(ItemSpecs.statusEquals(status))
                .and(ItemSpecs.categoryEquals(category))
                .and(ItemSpecs.locationLike(location))
                .and(ItemSpecs.keyword(q));

        Sort s = "oldest".equalsIgnoreCase(sort) ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        return ResponseEntity.ok(items.findAll(spec, s));
    }

    @GetMapping("/my")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> myItems() {
        Long userId = uid();
        if (userId == null)
            return ResponseEntity.status(401).build();
        User me = users.findById(userId).orElseThrow();
        return ResponseEntity.ok(items.findByCreatedBy(me));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable long id) {
        return items.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Item not found")));
    }

    @PostMapping("/{id}/resolve")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> resolve(@PathVariable long id) {
        Item item = items.findById(id).orElseThrow();
        Long userId = uid();
        if (userId == null || !item.getCreatedBy().getId().equals(userId))
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        item.setStatus(Item.Status.RESOLVED);
        items.save(item);
        return ResponseEntity.ok(Map.of("message", "Marked as resolved"));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Item item = items.findById(id).orElseThrow();
        Long userId = uid();
        if (userId == null || !item.getCreatedBy().getId().equals(userId))
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        items.delete(item);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    @PostMapping("/{id}/image")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> upload(@PathVariable long id, @RequestParam("file") MultipartFile file) throws Exception {
        Item item = items.findById(id).orElseThrow();
        Long userId = uid();
        if (userId == null || !item.getCreatedBy().getId().equals(userId))
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));

        if (file.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "No file"));

        File dir = new File("uploads");
        if (!dir.exists())
            dir.mkdirs();

        String safeName = "item_" + id + "_" + System.currentTimeMillis() + "_"
                + file.getOriginalFilename().replaceAll("\\s+", "_");
        File out = new File(dir, safeName);
        Files.copy(file.getInputStream(), out.toPath());

        item.setImagePath("/uploads/" + safeName);
        items.save(item);

        return ResponseEntity.ok(Map.of("message", "Image uploaded", "imagePath", item.getImagePath()));
    }
}
