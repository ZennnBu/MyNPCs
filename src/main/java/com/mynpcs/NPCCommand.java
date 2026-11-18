
package com.mynpcs;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class NPCCommand implements CommandExecutor, TabCompleter {

    private final NPCManager npcManager;

    public NPCCommand(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("mynpcs.admin")) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /npc create <name>");
                return true;
            }

            if (npcManager.exists(args[1])) {
                player.sendMessage(ChatColor.RED + "NPC already exists!");
                return true;
            }

            if (npcManager.createNPC(player, args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Creation failed!");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "NPC created: " + args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("movehere")) {

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /npc movehere <name>");
                return true;
            }

            Mannequin npc = npcManager.getNPC(args[1]);

            if (npc == null || !npc.isValid()) {
                player.sendMessage(ChatColor.RED + "NPC not found!");
                return true;
            }

            npc.teleport(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "NPC moved!");
            return true;
        }

        if (args[0].equalsIgnoreCase("copy")) {

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /npc copy <name>");
                return true;
            }

            String copiedName = npcManager.copyNPC(player, args[1]);

            if (copiedName == null) {
                player.sendMessage(ChatColor.RED + "NPC not found!");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "NPC copied: " + copiedName);
            return true;
        }

        if (args[0].equalsIgnoreCase("lookme")) {

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /npc lookme <name>");
                return true;
            }

            if (!npcManager.lookMe(player, args[1])) {
                player.sendMessage(ChatColor.RED + "NPC not found!");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "NPC is now facing you!");
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {

        player.sendMessage(ChatColor.DARK_PURPLE + "====== MyNPCs ======");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc create <name>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc movehere <name>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc copy <name>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc lookme <name>");
        player.sendMessage(ChatColor.DARK_PURPLE + "===================");
    }

    @Override
    public List<String> onTabComplete(
        CommandSender sender,
        Command command,
        String alias,
        String[] args
    ) {

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(
                args[0],
                Arrays.asList("create", "movehere", "copy", "lookme"),
                new ArrayList<>()
            );
        }

        if (args.length == 2 &&
            Arrays.asList("movehere", "copy", "lookme")
                .contains(args[0].toLowerCase())) {

            return StringUtil.copyPartialMatches(
                args[1],
                npcManager.getNPCs().keySet(),
                new ArrayList<>()
            );
        }

        return Collections.emptyList();
    }
}
