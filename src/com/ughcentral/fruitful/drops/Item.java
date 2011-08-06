package com.ughcentral.fruitful.drops;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;

import com.ughcentral.fruitful.Keyword;
import com.ughcentral.fruitful.valid.ValidItem;

public class Item extends Drop {
    
    private final ValidItem item;
    
    public Item(final ValidItem item, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        super(item.getName(), range, chance, keywords);
        this.item = item;
    }
    
    @Override
    public void dropIt(final World world, final Location location, final Keyword event) {
        if (!checkEvent(event)) {
            return;
        }
        final int number = getNumber();
        world.dropItemNaturally(location, item.getItemStack(number));
    }
    
}
