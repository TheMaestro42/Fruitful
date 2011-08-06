package com.ughcentral.fruitful.drops;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;
import com.ughcentral.fruitful.Keyword;

public class Jockey extends Drop {
    
    private final Creature mount;
    private final Creature rider;
    
    public Jockey(final String name, final Creature mount, final Creature rider, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        super(name, range, chance, keywords);
        this.mount = mount;
        this.rider = rider;
    }
    
    @Override
    public void dropIt(final World world, final Location location, final Keyword event) {
        if (!checkEvent(event)) {
            return;
        }
        final int loops = getNumber();
        int loop = 0;
        while (loop < loops) {
            mount.spawnIt(world, location).setPassenger(rider.spawnIt(world, location));
            loop++;
        }
        
    }
    
}
