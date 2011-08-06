package com.ughcentral.fruitful.drops;

import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.World;

import com.ughcentral.fruitful.Keyword;

public class Monstrosity extends Drop {
    
    private final ArrayList<Creature> creatures;
    
    public Monstrosity(final String name, final ArrayList<Creature> creatures, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        super(name, range, chance, keywords);
        this.creatures = creatures;
    }
    
    @Override
    public void dropIt(final World world, final Location location, final Keyword event) {
        if (!checkEvent(event)) {
            return;
        }
        final int loops = getNumber();
        int loop = 0;
        while (loop < loops) {
            int index = 0;
            final int total = creatures.size() - 1;
            final org.bukkit.entity.Creature[] creatureArray = new org.bukkit.entity.Creature[total];
            while (index <= total) {
                creatureArray[index] = creatures.get(index).spawnIt(world, location);
                index++;
            }
            index = 0;
            while (index < total) {
                creatureArray[index].setPassenger(creatureArray[index + 1]);
                index++;
            }
            loop++;
        }
    }
}
