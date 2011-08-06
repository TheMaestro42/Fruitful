package com.ughcentral.fruitful;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.ughcentral.fruitful.commands.FruitfulCommandHandler;
import com.ughcentral.fruitful.drops.Drop;
import com.ughcentral.fruitful.valid.ValidBlockType;
import com.ughcentral.fruitful.valid.ValidDrop;

public class Fruitful extends JavaPlugin {
    
    private FruitfulBlockListener blockListener;
    private FruitfulEntityListener entityListener;
    private FruitfulCommandHandler commandHandler;
    private static PermissionHandler permissionHandler = null;
    private static boolean usePermissions;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static String chatHeader = null;
    private static HashSet<Permission> permissionSet = new HashSet<Permission>();
    private static final Permission COMMAND_RELOAD = new Permission("fruitful.command.reload", "Access to the /fruitful reload command.");
    private static final Permission COMMAND_PARENT = new Permission("fruitful.command.*", "Allows access to all of Fruitful's commands.", PermissionDefault.OP);
    private static final Permission DROP_PARENT = new Permission("fruitful.drop.*", "Allows access to all of Fruitful's drops.", PermissionDefault.TRUE);
    private static final Permission ROOT = new Permission("fruitful.*", "Allows access to all of Fruitful's drops and commands.");
    private File main;
    FruitfulConfiguration configuration;
    
    @Override
    public void onDisable() {
        duringDisable();
    }
    
    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();
        blockListener = new FruitfulBlockListener(this);
        entityListener = new FruitfulEntityListener(this);
        commandHandler = new FruitfulCommandHandler(this);
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.LEAVES_DECAY, blockListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
        duringEnable();
    }
    
    @Override
    public void onLoad() {
    }
    
    public void reload() {
        duringDisable();
        duringEnable();
    }
    
    private void duringEnable() {
        chatHeader = new String("[" + getDescription().getFullName() + "] ");
        main = new File(getDataFolder() + File.separator + "config.yml");
        configuration = new FruitfulConfiguration(main, this);
        configuration.load();
        configuration.loadBlockTypes();
        configuration.loadValidDrops();
        setupPermissions();
        loadSuperPerms();
        getCommand("fruitful").setExecutor(commandHandler);
        logInfo("Plugin has been enabled.");
    }
    
    private void duringDisable() {
        if (configuration.hasChanged) {
            configuration.save();
        }
        configuration = null;
        main = null;
        ValidBlockType.unRegisterAll();
        ValidDrop.unRegisterAll();
        unloadSuperPerms();
        logInfo("Plugin has been disabled.");
    }
    
    static void logInfo(final String message) {
        log.info(chatHeader + message);
    }
    
    static void logWarning(final String message) {
        log.warning(chatHeader + message);
    }
    
    private void loadSuperPerms() {
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.addPermission(ROOT);
        pluginManager.addPermission(DROP_PARENT);
        pluginManager.addPermission(COMMAND_PARENT);
        pluginManager.addPermission(COMMAND_RELOAD);
        final Permission root = pluginManager.getPermission(ROOT.getName());
        final Permission dropParent = pluginManager.getPermission(DROP_PARENT.getName());
        final Permission commandParent = pluginManager.getPermission(COMMAND_PARENT.getName());
        final Permission commandReload = pluginManager.getPermission(COMMAND_RELOAD.getName());
        final Map<String, Boolean> rootChildren = root.getChildren();
        final Map<String, Boolean> dropChildren = pluginManager.getPermission(DROP_PARENT.getName()).getChildren();
        final Map<String, Boolean> commandChildren = pluginManager.getPermission(COMMAND_PARENT.getName()).getChildren();
        rootChildren.clear();
        rootChildren.put(commandParent.getName(), true);
        rootChildren.put(dropParent.getName(), true);
        root.recalculatePermissibles();
        commandChildren.clear();
        commandChildren.put(commandReload.getName(), true);
        commandParent.recalculatePermissibles();
        dropChildren.clear();
        // Now for the mess
        final HashSet<String> blocktypes = new HashSet<String>();
        final HashMap<String, HashSet<String>> drops = new HashMap<String, HashSet<String>>();
        for (final String defaultBlock : configuration.getKeys("defaults")) {
            blocktypes.add("fruitful.drop." + defaultBlock + ".*");
            final HashSet<String> defaultDrops = new HashSet<String>();
            for (final String defaultDrop : configuration.getKeys("defaults." + defaultBlock)) {
                if (defaultDrop.equals("keywords")) {
                    continue;
                }
                defaultDrops.add("fruitful.drop." + defaultBlock + "." + defaultDrop);
            }
            drops.put("fruitful.drop." + defaultBlock + ".*", defaultDrops);
        }
        if (configuration.getKeys("worlds") != null) {
            for (final String world : configuration.getKeys("worlds")) {
                for (final String worldBlock : configuration.getKeys("worlds." + world)) {
                    final HashSet<String> worldDrops = new HashSet<String>();
                    final String prefix = "fruitful.drop." + worldBlock;
                    for (final String worldDrop : configuration.getKeys("worlds." + world + "." + worldBlock)) {
                        if (worldDrop.equals("keywords")) {
                            continue;
                        }
                        worldDrops.add(prefix + "." + worldDrop);
                    }
                    if (drops.get(prefix + ".*") == null) {
                        drops.put(prefix + ".*", worldDrops);
                    } else {
                        final HashSet<String> dropValue = drops.get(prefix + ".*");
                        dropValue.addAll(worldDrops);
                        drops.put(prefix + ".*", dropValue);
                    }
                }
            }
        }
        
        for (final String blockName : drops.keySet()) {
            dropChildren.put(blockName, true);
            final Permission blockParent = new Permission(blockName);
            pluginManager.addPermission(blockParent);
            permissionSet.add(blockParent);
            final Map<String, Boolean> blockChildren = blockParent.getChildren();
            for (final String dropName : drops.get(blockName)) {
                blockChildren.put(dropName, true);
                final Permission dropPermission = new Permission(dropName);
                pluginManager.addPermission(dropPermission);
                permissionSet.add(dropPermission);
            }
            blockParent.recalculatePermissibles();
        }
        dropParent.recalculatePermissibles();
        permissionSet.add(root);
        permissionSet.add(commandParent);
        permissionSet.add(commandReload);
        permissionSet.add(dropParent);
    }
    
    private void unloadSuperPerms() {
        final PluginManager pluginManager = getServer().getPluginManager();
        for (final Permission permission : permissionSet) {
            pluginManager.removePermission(permission);
        }
        permissionSet.clear();
    }
    
    private void setupPermissions() {
        usePermissions = false;
        if (configuration.getOption("Permissions")) {
            final Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
            
            if (permissionsPlugin == null) {
                logInfo("Permissions not detected, defaulting to SuperPerms.");
                return;
            }
            
            permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            logInfo("Found " + ((Permissions) permissionsPlugin).getDescription().getFullName());
            usePermissions = true;
        }
    }
    
    public boolean hasPermission(final String permission, final Player player) {
        if (player != null) {
            if (usePermissions) {
                return permissionHandler.has(player, permission);
            } else {
                return player.hasPermission(permission);
            }
        }
        return true;
    }
    
    void chanceIt(final World world, final Location location, final Player player, final String blocktype, final Keyword event) {
        final HashSet<Drop> drops = configuration.getDropList(world, blocktype, event);
        final Random random = new Random();
        for (final Drop drop : drops) {
            if (hasPermission("fruitful.drop." + blocktype + "." + drop.getName(), player)) {
                if (random.nextDouble() < drop.getChance()) {
                    drop.dropIt(world, location, event);
                }
            }
        }
    }
    
}
