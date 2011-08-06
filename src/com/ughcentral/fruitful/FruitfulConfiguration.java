package com.ughcentral.fruitful;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.ughcentral.fruitful.drops.*;
import com.ughcentral.fruitful.valid.*;

public class FruitfulConfiguration extends Configuration {
    
    private static final Integer[] ALL_DATA = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    private static final Biome[] ALL_BIOMES = { Biome.DESERT, Biome.FOREST, Biome.HELL, Biome.ICE_DESERT, Biome.PLAINS, Biome.RAINFOREST, Biome.SAVANNA, Biome.SEASONAL_FOREST, Biome.SHRUBLAND, Biome.SKY, Biome.SWAMPLAND, Biome.TAIGA, Biome.TUNDRA };
    private static final Integer[] DEFAULT_DATA = { 0 };
    private static final int[] ZERO = { 0 };
    private static final String DEFAULT_PREFIX = "defaults.";
    private static final String WORLD_PREFIX = "worlds.";
    private static final String BLOCKTYPES = "valid.blocktypes";
    private static final String ITEMS = "valid.items";
    private static final String CREATURES = "valid.creatures";
    private static final String GROUPS = "valid.groups";
    
    private final File configurationFile;
    private Fruitful plugin;
    boolean hasChanged;
    
    FruitfulConfiguration(final File file, Fruitful instance) {
        super(file);
        configurationFile = file;
        plugin = instance;
    }
    
    @Override
    public void load() {
        if (!configurationFile.exists()) {
            createConfiguration(configurationFile);
            File simpleConfigurationFile = new File(plugin.getDataFolder() + File.separator + "simple_config.yml");
            if (!simpleConfigurationFile.exists()) {
                createConfiguration(simpleConfigurationFile);
            }
        }
        super.load();
        hasChanged = false;
    }
    
    HashSet<Drop> getDropList(final World world, final String blocktype, final Keyword event) {
        final HashSet<Drop> result = new HashSet<Drop>();
        List<String> drops = new ArrayList<String>();
        drops = this.getKeys(WORLD_PREFIX + world.getName() + "." + blocktype);
        boolean useDefault = false;
        if (drops == null) {
            drops = this.getKeys(DEFAULT_PREFIX + blocktype);
            useDefault = true;
        }
        if (drops == null) {
            return result;
        }
        drops.remove("keywords");
        String locationPrefix;
        if (useDefault) {
            locationPrefix = DEFAULT_PREFIX + blocktype + ".";
        } else {
            locationPrefix = WORLD_PREFIX + world.getName() + "." + blocktype + ".";
        }
        for (final String drop : drops) {
            final Drop currentDrop = getDrop(locationPrefix, drop);
            if (currentDrop == null) {
                // Error
                continue;
            } else {
                if (currentDrop.canDrop(event)) {
                    result.add(currentDrop);
                }
            }
        }
        
        return result;
    }
    
    private Drop getDrop(final String prefix, final String drop) {
        final ValidDrop validDrop = ValidDrop.getByName(drop);
        if (validDrop == null) {
            // Error
            return null;
        }
        switch (validDrop.dropType()) {
        case ITEM:
            final ValidItem item = (ValidItem) ValidDrop.getByName(drop);
            final int[] itemRange = integerListToArray(getIntList(prefix + drop + ".range", null));
            final double itemChance = parseChance(this.getString(prefix + drop + ".chance", "0.0"));
            final HashSet<Keyword> itemKeywords = stringToKeyword(getStringList(prefix + drop + ".keywords", null));
            itemKeywords.addAll(item.keywords);
            return new Item(item, itemRange, itemChance, itemKeywords);
        case CREATURE:
            final ValidCreature creature = (ValidCreature) ValidDrop.getByName(drop);
            final int[] creatureRange = integerListToArray(getIntList(prefix + drop + ".range", null));
            final double creatureChance = parseChance(this.getString(prefix + drop + ".chance", "0.0"));
            final HashSet<Keyword> creatureKeywords = stringToKeyword(getStringList(prefix + drop + ".keywords", null));
            creatureKeywords.addAll(creature.keywords);
            return new Creature(creature, creatureRange, creatureChance, creatureKeywords);
        case GROUP:
            final ValidGroup group = (ValidGroup) ValidDrop.getByName(drop);
            final int[] groupRange = integerListToArray(getIntList(prefix + drop + ".range", null));
            final double groupChance = parseChance(this.getString(prefix + drop + ".chance", "0.0"));
            final HashSet<Keyword> groupKeywords = stringToKeyword(getStringList(prefix + drop + ".keywords", null));
            groupKeywords.addAll(group.keywords);
            final HashSet<Drop> groupDrops = new HashSet<Drop>();
            for (final String groupDrop : this.getKeys(GROUPS + "." + drop)) {
                if (groupDrop.equals("keywords")) {
                    continue;
                }
                groupDrops.add(getDrop(GROUPS + "." + drop + ".", groupDrop));
            }
            return new Group(group.getName(), groupDrops, groupRange, groupChance, groupKeywords);
        default:
            return null;
        }
    }
    
    private static double parseChance(final String chanceString) throws NumberFormatException {
        double result = 0.0;
        final int index = chanceString.indexOf('%');
        if (index != -1) {
            if (index == 0) {
                result = Double.parseDouble(chanceString.substring(1)) / 100;
            } else {
                result = Double.parseDouble(chanceString.substring(0, index)) / 100;
            }
        } else {
            if (chanceString.contains(":")) {
                final String[] splitString = chanceString.split("[^0-9.]");
                result = Double.parseDouble(splitString[0]) / Double.parseDouble(splitString[1]);
            } else {
                result = Double.parseDouble(chanceString);
            }
        }
        return result;
    }
    
    private void createConfiguration(File file) {
        final InputStream inputFile = this.getClass().getResourceAsStream("/" + file.getName());
        if (inputFile != null) {
            FileOutputStream outputFile = null;
            try {
                outputFile = new FileOutputStream(file);
                final byte[] fileBuffer = new byte[4096];
                int length = 0;
                while ((length = inputFile.read(fileBuffer)) > 0) {
                    outputFile.write(fileBuffer, 0, length);
                }
                Fruitful.logInfo("Default \"" + file.getName() + "\" created.");
            } catch (final IOException e) {
                
            } finally {
                try {
                    inputFile.close();
                } catch (final IOException e) {
                }
                try {
                    if (outputFile != null) {
                        outputFile.close();
                    }
                } catch (final IOException e) {
                }
            }
        }
    }
    
    void loadBlockTypes() {
        final List<String> keys = this.getKeys(BLOCKTYPES);
        for (final String key : keys) {
            final Material type = typeToMaterial(getProperty(BLOCKTYPES + "." + key + ".type"));
            if (type == null) {
                // Error
                continue;
            }
            final HashSet<Integer> data = new HashSet<Integer>(getIntList(BLOCKTYPES + "." + key + ".data", Arrays.asList(ALL_DATA)));
            final HashSet<Biome> biomes = stringToBiome(getStringList(BLOCKTYPES + "." + key + ".biomes", null));
            new ValidBlockType(key, type, data, biomes);
        }
    }
    
    void loadValidDrops() {
        // Items
        final List<String> items = this.getKeys(ITEMS);
        if (items != null) {
            for (final String item : items) {
                final Material itemType = typeToMaterial(getProperty(ITEMS + "." + item + ".type"));
                if (itemType == null) {
                    // Error
                    continue;
                }
                final HashSet<Keyword> keywords = stringToKeyword(getStringList(ITEMS + "." + item + ".keywords", null));
                final List<Integer> itemDataList = getIntList(ITEMS + "." + item + ".data", Arrays.asList(DEFAULT_DATA));
                Integer[] itemData = new Integer[itemDataList.size()];
                itemData = itemDataList.toArray(itemData);
                new ValidItem(item, keywords, itemType, itemData);
            }
        }
        // Creatures
        final List<String> creatures = this.getKeys(CREATURES);
        if (creatures != null) {
            for (final String creature : creatures) {
                final CreatureType creatureType = CreatureType.valueOf(this.getString(CREATURES + "." + creature + ".creature").toUpperCase());
                if (creatureType == null) {
                    // Error
                    continue;
                }
                final HashSet<Keyword> creatureKeywords = stringToKeyword(getStringList(CREATURES + "." + creature + ".keywords", null));
                new ValidCreature(creature, creatureKeywords, creatureType);
            }
        }
        // Groups
        final List<String> groups = this.getKeys(GROUPS);
        if (groups != null) {
            for (final String group : groups) {
                final HashSet<Keyword> groupKeywords = stringToKeyword(getStringList(GROUPS + "." + group + ".keywords", null));
                final HashSet<ValidDrop> groupDrops = new HashSet<ValidDrop>();
                for (final String drop : this.getKeys(GROUPS + "." + group)) {
                    groupDrops.add(ValidDrop.getByName(drop));
                }
                if (groupDrops.isEmpty()) {
                    // Error
                    continue;
                }
                new ValidGroup(group, groupKeywords, groupDrops);
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    HashSet<Keyword> getBlockKeywords(final World world, final String blocktype) throws ClassCastException {
        final HashSet<Keyword> result = new HashSet<Keyword>();
        List<String> keywordProperty = (List<String>) getProperty(WORLD_PREFIX + world.getName() + "." + blocktype + ".keywords");
        if (keywordProperty == null) {
            keywordProperty = (List<String>) getProperty(DEFAULT_PREFIX + blocktype + ".keywords");
            if (keywordProperty == null) {
                return result;
            }
        }
        if (keywordProperty instanceof List) {
            result.addAll(stringToKeyword(keywordProperty));
        }
        
        return result;
    }
    
    boolean isDefined(final World world, final String blocktype) {
        boolean result = true;
        if (this.getKeys(WORLD_PREFIX + world.getName() + "." + blocktype) == null) {
            result = false;
        }
        if (!result) {
            if (this.getKeys(DEFAULT_PREFIX + blocktype) != null) {
                result = true;
            }
        }
        return result;
    }
    
    boolean getOption(final String name) {
        return getBoolean("options." + name, false);
    }
    
    private static Material typeToMaterial(final Object type) {
        if (type instanceof Number) {
            return Material.getMaterial((Integer) type);
        }
        if (type instanceof String) {
            return Material.matchMaterial((String) type);
        }
        return null;
    }
    
    private static HashSet<Biome> stringToBiome(final List<String> biomes) {
        final HashSet<Biome> result = new HashSet<Biome>();
        if (biomes != null) {
            for (final String biome : biomes) {
                result.add(Biome.valueOf(biome.toUpperCase()));
            }
        }
        if (result.isEmpty()) {
            result.addAll(Arrays.asList(ALL_BIOMES));
        }
        return result;
    }
    
    private static HashSet<Keyword> stringToKeyword(final List<String> keywords) {
        final HashSet<Keyword> result = new HashSet<Keyword>();
        if (keywords != null) {
            for (final String keyword : keywords) {
                result.add(Keyword.valueOf(keyword.toUpperCase()));
            }
        }
        return result;
    }
    
    private static int[] integerListToArray(final List<Integer> list) {
        if (list.size() == 0) {
            return ZERO;
        }
        final int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
    
}
