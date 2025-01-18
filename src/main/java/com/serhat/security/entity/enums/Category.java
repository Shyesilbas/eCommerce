package com.serhat.security.entity.enums;

public enum Category {
    ELECTRONICS,
    CLOTHING,
    HOME_AND_KITCHEN,
    BOOKS_AND_STATIONERY,
    SPORTS_AND_OUTDOORS,
    BEAUTY_AND_COSMETICS,
    TOYS_AND_GAMES,
    AUTOMOTIVE,
    HEALTH_AND_WELLNESS,
    GROCERY;

    public String toDisplayName() {
        return this.name().replace("_", " ")
                .toLowerCase()
                .replace("ı","i")
                .replaceFirst(
                        this.name().substring(0, 1).toLowerCase(),
                        this.name().substring(0, 1)
                );
    }
}
