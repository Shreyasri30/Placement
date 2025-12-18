package com.reclaim.app.claims;

import com.reclaim.app.items.Item;
import com.reclaim.app.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="claims")
public class Claim {

    public enum Status { PENDING, ACCEPTED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Item item;

    @ManyToOne(optional=false)
    private User claimant;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId(){ return id; }
    public Item getItem(){ return item; }
    public User getClaimant(){ return claimant; }
    public Status getStatus(){ return status; }

    public void setItem(Item item){ this.item = item; }
    public void setClaimant(User claimant){ this.claimant = claimant; }
    public void setStatus(Status status){ this.status = status; }
}
