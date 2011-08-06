package com.ughcentral.fruitful.drops;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import com.ughcentral.fruitful.Keyword;

public abstract class Drop {
    
    private final int[] range;
    private final double chance;
    private final HashSet<Keyword> keywords;
    private final String name;
    
    public Drop(final String name, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        this.name = name;
        this.range = range;
        this.chance = chance;
        this.keywords = keywords;
    }
    
    public boolean canDrop(final Keyword event) {
        if (event == Keyword.BURNABLE) {
            return hasKeyword(event);
        }
        if (event == Keyword.SHEARABLE) {
            if (hasKeyword(Keyword.NO_BREAK)) {
                return false;
            } else {
                return (hasKeyword(event));
            }
        }
        if (hasKeyword(event)) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean hasKeyword(final Keyword keyword) {
        return keywords.contains(keyword);
    }
    
    boolean checkEvent(final Keyword event) {
        if (hasKeyword(Keyword.DISABLED)) {
            return false;
        }
        switch (event) {
        case SHEARABLE:
            if (!hasKeyword(event) || hasKeyword(Keyword.NO_BREAK)) {
                return false;
            }
            break;
        case BURNABLE:
            if (!hasKeyword(event) || hasKeyword(Keyword.SECURE)) {
                return false;
            }
            break;
        case NO_BREAK:
            if (hasKeyword(event)) {
                return false;
            }
            break;
        default:
            if (hasKeyword(event) || hasKeyword(Keyword.SECURE)) {
                return false;
            }
        }
        return true;
    }
    
    public double getChance() {
        return chance;
    }
    
    public String getName() {
        return name;
    }
    
    int getNumber() {
        if (range.length == 1) {
            return range[0];
        }
        return new Random().nextInt((range[1] - range[0]) + 1) + range[0];
    }
    
    public abstract void dropIt(World world, Location location, Keyword event);
    
}
