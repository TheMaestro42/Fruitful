package com.ughcentral.fruitful.drops;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import com.ughcentral.fruitful.Keyword;

public class Group extends Drop {
    
    HashSet<Drop> drops;
    
    public Group(final String name, final HashSet<Drop> drops, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        super(name, range, chance, keywords);
        this.drops = drops;
    }
    
    private double totalWeight(final HashSet<Drop> dropsToCheck) {
        double result = 0.0;
        for (final Drop drop : dropsToCheck) {
            result = result + drop.getChance();
        }
        return result;
    }
    
    @Override
    public void dropIt(final World world, final Location location, final Keyword event) {
        if (!checkEvent(event)) {
            return;
        }
        final HashSet<Drop> modifiedDrops = new HashSet<Drop>();
        for (final Drop originalDrop : drops) {
            if (originalDrop.checkEvent(event)) {
                modifiedDrops.add(originalDrop);
            }
        }
        if (modifiedDrops.isEmpty()) {
            return;
        }
        final int loops = super.getNumber();
        int loop = 0;
        final double total = totalWeight(modifiedDrops);
        final Random random = new Random();
        while (loop < loops) {
            double current = 0.0;
            final double r = random.nextDouble();
            for (final Drop drop : modifiedDrops) {
                current = current + (drop.getChance() / total);
                if (r <= current) {
                    drop.dropIt(world, location, event);
                    break;
                }
            }
            loop++;
        }
    }
    
}
