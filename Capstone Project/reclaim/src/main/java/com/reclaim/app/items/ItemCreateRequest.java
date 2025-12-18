package com.reclaim.app.items;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ItemCreateRequest {
    @NotNull(message = "Type is required")
    public Item.Type type;

    @NotBlank(message = "Title is required")
    public String title;

    @NotBlank(message = "Category is required")
    public String category;

    @NotBlank(message = "Description is required")
    public String description;

    @NotBlank(message = "Location is required")
    public String location;

    @NotNull(message = "Date is required")
    public LocalDate eventDate;

    @NotNull(message = "Contact method is required")
    public Item.ContactMethod contactMethod;

    @NotBlank(message = "Contact value is required")
    public String contactValue;
}
