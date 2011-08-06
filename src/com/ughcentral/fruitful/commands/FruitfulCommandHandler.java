package com.ughcentral.fruitful.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ughcentral.fruitful.Fruitful;

public class FruitfulCommandHandler implements CommandExecutor {
    private final Fruitful plugin;
    private static final String chatPrefix = ChatColor.GREEN + "[" + ChatColor.GOLD + "Fruitful" + ChatColor.GREEN + "] " + ChatColor.WHITE;
    private static final String commandName = ChatColor.GREEN + "/" + ChatColor.GOLD + "fruitful ";
    
    public FruitfulCommandHandler(final Fruitful instance) {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        String subCommand = "none";
        if (args.length > 0) {
            subCommand = args[0].toLowerCase();
        }
        if (subCommand.equals("reload")) {
            final String permission = "fruitful.command.reload";
            boolean hasPermission = false;
            if (sender instanceof Player) {
                hasPermission = plugin.hasPermission(permission, (Player) sender);
            } else {
                hasPermission = sender.hasPermission(permission);
            }
            if (hasPermission) {
                plugin.reload();
                sender.sendMessage(chatPrefix + "Plugin has been reloaded.");
                return true;
            } else {
                sender.sendMessage(chatPrefix + "You do not have permission to use this command.");
                return true;
            }
        } else {
            sender.sendMessage(chatPrefix + "Command help:");
            // sender.sendMessage("/fruitful drop <world> <type> <drop> true/false");
            // sender.sendMessage("/fruitful chance <world> <type> <drop> <chance>");
            // sender.sendMessage("/fruitful secure <world> <type> <drop> true/false");
            sender.sendMessage(commandName + "reload" + ChatColor.WHITE + " - Reloads the configuration file");
            // sender.sendMessage("<world> is Case Sensitive, or use default");
            // sender.sendMessage("<type> is generic, birch, redwood, or crop");
            // sender.sendMessage("<drop> is the name of the drop");
            return true;
        }
    }
}
