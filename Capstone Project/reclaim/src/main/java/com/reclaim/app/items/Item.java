package com.reclaim.app.items;

import com.reclaim.app.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="items")
public class Item {

    public enum Type { LOST, FOUND }
    public enum Status { ACTIVE, CLAIMED, RESOLVED }
    public enum ContactMethod { EMAIL, PHONE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Status status = Status.ACTIVE;

    @Column(nullable=false, length=120)
    private String title;

    @Column(nullable=false, length=60)
    private String category;

    @Column(nullable=false, columnDefinition="TEXT")
    private String description;

    @Column(nullable=false, length=120)
    private String location;

    @Column(nullable=false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ContactMethod contactMethod;

    @Column(nullable=false, length=150)
    private String contactValue;

    @Column(length=255)
    private String imagePath;

    @ManyToOne(optional=false)
    @JoinColumn(name="created_by")
    private User createdBy;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getEventDate() { return eventDate; }
    public ContactMethod getContactMethod() { return contactMethod; }
    public String getContactValue() { return contactValue; }
    public String getImagePath() { return imagePath; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setType(Type type) { this.type = type; }
    public void setStatus(Status status) { this.status = status; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public void setContactMethod(ContactMethod contactMethod) { this.contactMethod = contactMethod; }
    public void setContactValue(String contactValue) { this.contactValue = contactValue; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
