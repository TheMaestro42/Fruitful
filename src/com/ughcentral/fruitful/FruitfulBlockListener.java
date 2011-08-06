package com.ughcentral.fruitful;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.LeavesDecayEvent;

import com.ughcentral.fruitful.valid.ValidBlockType;

public class FruitfulBlockListener extends BlockListener {
    
    private final Fruitful plugin;
    
    public FruitfulBlockListener(final Fruitful instance) {
        plugin = instance;
    }
    
    @Override
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Block block = event.getBlock();
        final String blocktype = ValidBlockType.getMatch(block);
        final World world = block.getWorld();
        if ((blocktype == null) || !plugin.configuration.isDefined(world, blocktype)) {
            return;
        }
        final HashSet<Keyword> blockKeywords = plugin.configuration.getBlockKeywords(world, blocktype);
        Keyword keyword = Keyword.NO_BREAK;
        final Player player = event.getPlayer();
        checkEventKeywords: {
            if (blockKeywords.contains(keyword) || blockKeywords.contains(Keyword.DISABLED)) {
                break checkEventKeywords;
            }
            if (player.getItemInHand().getType() == Material.SHEARS) {
                keyword = Keyword.SHEARABLE;
                if (!blockKeywords.contains(keyword)) {
                    break checkEventKeywords;
                }
            }
            final Location location = block.getLocation();
            plugin.chanceIt(world, location, player, blocktype, keyword);
        }
        if (blockKeywords.contains(Keyword.NO_OVERRIDE) || (!blockKeywords.contains(Keyword.SHEARABLE) && (player.getItemInHand().getType() == Material.SHEARS))) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR);
        
    }
    
    @Override
    public void onBlockBurn(final BlockBurnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Block block = event.getBlock();
        final String blocktype = ValidBlockType.getMatch(block);
        final World world = block.getWorld();
        if ((blocktype == null) || !plugin.configuration.isDefined(world, blocktype)) {
            return;
        }
        final HashSet<Keyword> blockKeywords = plugin.configuration.getBlockKeywords(world, blocktype);
        final Keyword keyword = Keyword.BURNABLE;
        if (blockKeywords.contains(keyword) && !blockKeywords.contains(Keyword.SECURE) && !blockKeywords.contains(Keyword.DISABLED)) {
            final Location location = block.getLocation();
            plugin.chanceIt(world, location, null, blocktype, keyword);
        }
        if (blockKeywords.contains(Keyword.NO_OVERRIDE)) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR);
        
    }
    
    @Override
    public void onLeavesDecay(final LeavesDecayEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Block block = event.getBlock();
        final String blocktype = ValidBlockType.getMatch(block);
        final World world = block.getWorld();
        if ((blocktype == null) || !plugin.configuration.isDefined(world, blocktype)) {
            return;
        }
        final HashSet<Keyword> blockKeywords = plugin.configuration.getBlockKeywords(world, blocktype);
        final Keyword keyword = Keyword.NO_DECAY;
        if (!blockKeywords.contains(keyword) && !blockKeywords.contains(Keyword.SECURE) && !blockKeywords.contains(Keyword.DISABLED)) {
            final Location location = block.getLocation();
            plugin.chanceIt(world, location, null, blocktype, keyword);
        }
        if (blockKeywords.contains(Keyword.NO_OVERRIDE)) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR);
        
    }
    
    @Override
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Block block = event.getToBlock();
        final String blocktype = ValidBlockType.getMatch(block);
        final World world = block.getWorld();
        if ((blocktype == null) || !plugin.configuration.isDefined(world, blocktype)) {
            return;
        }
        final HashSet<Keyword> blockKeywords = plugin.configuration.getBlockKeywords(world, blocktype);
        final Keyword keyword = Keyword.NO_WATER;
        if (!blockKeywords.contains(keyword) && !blockKeywords.contains(Keyword.SECURE) && !blockKeywords.contains(Keyword.DISABLED)) {
            final Location location = block.getLocation();
            plugin.chanceIt(world, location, null, blocktype, keyword);
        }
        if (blockKeywords.contains(Keyword.NO_OVERRIDE)) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR);
        
    }
    
}
