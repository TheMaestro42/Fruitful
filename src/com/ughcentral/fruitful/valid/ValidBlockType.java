package com.ughcentral.fruitful.valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class ValidBlockType {
    
    public static final HashMap<String, ValidBlockType> blockTypes = new HashMap<String, ValidBlockType>();
    
    private final Material material;
    private final HashSet<Integer> data;
    private final HashSet<Biome> biomes;
    private final String name;
    
    public ValidBlockType(final String name, final Material material, final HashSet<Integer> data, final HashSet<Biome> biomes) {
        this.material = material;
        this.data = data;
        this.biomes = biomes;
        this.name = name;
        register(name, this);
    }
    
    public static void unRegister(final String blockName) {
        blockTypes.remove(blockName);
    }
    
    private static void register(final String blockName, final ValidBlockType blocktype) {
        blockTypes.put(blockName, blocktype);
    }
    
    public static void unRegisterAll() {
        final Set<String> keySet = blockTypes.keySet();
        final Set<String> keys = new HashSet<String>();
        keys.addAll(keySet);
        for (final String key : keys) {
            unRegister(key);
        }
    }
    
    private String getName() {
        return name;
    }
    
    public static ValidBlockType getByName(final String blockName) {
        return blockTypes.get(blockName);
    }
    
    public static String getMatch(final Block block) {
        final Material blockMaterial = block.getType();
        int blockData;
        switch (blockMaterial) {
        case LEAVES:
            blockData = block.getData() & 3;
            break;
        case CROPS:
            blockData = block.getData() & 7;
            break;
        case LONG_GRASS:
            blockData = block.getData() & 3;
            break;
        case LOG:
            blockData = block.getData() & 3;
            break;
        case STEP:
            blockData = block.getData() & 3;
            break;
        case DOUBLE_STEP:
            blockData = block.getData() & 3;
            break;
        default:
            blockData = block.getData();
        }
        final Biome blockBiome = block.getBiome();
        final SortedMap<Integer, ValidBlockType> matchMap = new TreeMap<Integer, ValidBlockType>();
        for (final Entry<String, ValidBlockType> entry : blockTypes.entrySet()) {
            int matchLevel = 0;
            final ValidBlockType blocktype = entry.getValue();
            if (!(blocktype.material == blockMaterial)) {
                continue;
            }
            if (blocktype.data.contains(blockData)) {
                matchLevel = matchLevel + (16 - blocktype.data.size());
            } else {
                continue;
            }
            if (blocktype.biomes.contains(blockBiome)) {
                matchLevel = matchLevel + (16 + (Biome.values().length - blocktype.biomes.size()));
            } else {
                continue;
            }
            matchMap.put(matchLevel, blocktype);
        }
        if (matchMap.isEmpty()) {
            return null;
        }
        return matchMap.get(matchMap.lastKey()).getName();
    }
    
}
