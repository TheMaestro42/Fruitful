package com.ughcentral.fruitful;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.ughcentral.fruitful.valid.ValidBlockType;

public class FruitfulEntityListener extends EntityListener {
    
    private final Fruitful plugin;
    
    public FruitfulEntityListener(final Fruitful instance) {
        plugin = instance;
    }
    
    @Override
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (event.isCancelled() || !plugin.isEnabled()) {
            return;
        }
        final List<Block> blockList = event.blockList();
        final List<Block> targetBlocks = new ArrayList<Block>();
        for (final Block block : blockList) {
            final String blocktype = ValidBlockType.getMatch(block);
            final World world = block.getWorld();
            if ((blocktype == null) || !plugin.configuration.isDefined(world, blocktype)) {
                continue;
            }
            final HashSet<Keyword> blockKeywords = plugin.configuration.getBlockKeywords(world, blocktype);
            final Keyword keyword = Keyword.NO_BOOM;
            final float y = event.getYield();
            final Random random = new Random();
            if (!blockKeywords.contains(keyword) && !blockKeywords.contains(Keyword.SECURE) && !blockKeywords.contains(Keyword.DISABLED)) {
                if (random.nextFloat() < y) {
                    final Location location = block.getLocation();
                    plugin.chanceIt(world, location, null, blocktype, keyword);
                }
            }
            if (!blockKeywords.contains(Keyword.NO_OVERRIDE)) {
                targetBlocks.add(block);
            }
        }
        if (!targetBlocks.isEmpty()) {
            for (final Block target : targetBlocks) {
                blockList.remove(target);
            }
        }
    }
    
}
