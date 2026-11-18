
package com.mynpcs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                player.sendMessage(
                    ChatColor.RED + "Usage: /npc create <name>"
                );
                return true;
            }

            String name = args[1];

            if (npcManager.exists(name)) {
                player.sendMessage(
                    ChatColor.RED + "NPC already exists!"
                );
                return true;
            }

            if (npcManager.createNPC(player, name) == null) {
                player.sendMessage(
                    ChatColor.RED + "NPC creation failed!"
                );
                return true;
            }

            player.sendMessage(
                ChatColor.GREEN + "NPC created: "
                + ChatColor.YELLOW + name
            );

            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "====== MyNPCs ======");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc create <name>");
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
                Arrays.asList("create"),
                new ArrayList<>()
            );
        }

        return Collections.emptyList();
    }
}
