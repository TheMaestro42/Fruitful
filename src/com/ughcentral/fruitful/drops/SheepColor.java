package com.ughcentral.fruitful.drops;

import java.util.Random;

import org.bukkit.DyeColor;

enum SheepColor {
    
    WHITE(DyeColor.WHITE, 2046), LIGHT_GREY(DyeColor.SILVER, 125), GREY(DyeColor.GRAY, 125), BLACK(DyeColor.BLACK, 125), BROWN(DyeColor.BROWN, 75), PINK(DyeColor.PINK, 4);
    
    private final DyeColor color;
    private final int chance;
    private static final int TOTAL_CHANCE = 2500;
    
    private SheepColor(final DyeColor color, final int chance) {
        this.color = color;
        this.chance = chance;
    }
    
    static DyeColor getColor() {
        final int random = new Random().nextInt(TOTAL_CHANCE) + 1;
        int current = 0;
        for (final SheepColor sheepColor : SheepColor.values()) {
            current = current + sheepColor.chance;
            if (random <= current) {
                return sheepColor.color;
            }
        }
        return DyeColor.WHITE;
    }
    
}
