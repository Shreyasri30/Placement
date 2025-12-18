package com.reclaim.app.dashboard;

import com.reclaim.app.items.ItemRepository;
import com.reclaim.app.claims.Claim;
import com.reclaim.app.claims.ClaimRepository;
import com.reclaim.app.user.User;
import com.reclaim.app.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ItemRepository items;
    private final ClaimRepository claims;
    private final UserRepository users;

    public DashboardController(ItemRepository i, ClaimRepository c, UserRepository u) {
        this.items = i;
        this.claims = c;
        this.users = u;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "totalItems", items.count(),
                "totalClaims", claims.count());
    }

    @GetMapping("/my-stats")
    public Map<String, Object> myStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Map.of();
        }

        String email = "";
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        User me = users.findByEmail(email).orElse(null);
        if (me == null)
            return Map.of();

        long myItemsCount = items.findByCreatedBy(me).size();
        long myClaimsCount = claims.findByClaimant(me).size();
        long pendingOnMyItemsCount = claims.countByItemCreatedByAndStatus(me, Claim.Status.PENDING);

        return Map.of(
                "myItemsCount", myItemsCount,
                "myClaimsCount", myClaimsCount,
                "pendingOnMyItemsCount", pendingOnMyItemsCount);
    }
}
