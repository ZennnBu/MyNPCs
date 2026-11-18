
package com.mynpcs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {

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
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "MyNPCs Commands:");
            player.sendMessage(ChatColor.GRAY + "/npc create <name>");
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
                    ChatColor.RED + "An NPC with this name already exists!"
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
                ChatColor.GREEN + "Successfully created NPC: "
                + ChatColor.YELLOW + name
            );

            return true;
        }

        player.sendMessage(
            ChatColor.RED + "Unknown command. Use /npc"
        );

        return true;
    }
}
