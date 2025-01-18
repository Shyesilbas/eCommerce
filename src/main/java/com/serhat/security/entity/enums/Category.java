package com.serhat.security.entity.enums;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    HOME_AND_KITCHEN("Home and Kitchen"),
    BOOKS_AND_STATIONERY("Books and Stationery"),
    SPORTS_AND_OUTDOORS("Sports and Outdoors"),
    BEAUTY_AND_COSMETICS("Beauty and Cosmetics"),
    TOYS_AND_GAMES("Toys and Games"),
    AUTOMOTIVE("Automotive"),
    HEALTH_AND_WELLNESS("Health and Wellness"),
    GROCERY("Grocery");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public static Category fromDisplayName(String displayName) {
        for (Category category : Category.values()) {
            if (category.getDisplayName().equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name: " + displayName);
    }
}