package com.reclaim.app.claims;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByItemId(Long itemId);

    java.util.List<Claim> findByClaimant(com.reclaim.app.user.User claimant);

    java.util.Optional<Claim> findOptionalByItemIdAndClaimant(Long itemId, com.reclaim.app.user.User claimant);

    java.util.Optional<Claim> findByItemAndClaimant(com.reclaim.app.items.Item item,
            com.reclaim.app.user.User claimant);

    long countByItemCreatedByAndStatus(com.reclaim.app.user.User user, Claim.Status status);
}
