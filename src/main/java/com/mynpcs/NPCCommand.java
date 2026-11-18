
package com.mynpcs;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class NPCCommand implements CommandExecutor, TabCompleter {

    private final NPCManager npcManager;
    private final NPCSkinManager skinManager;

    private static final List<String> COLORS = Arrays.asList(
        "Yellow", "Black", "White", "Purple", "Red",
        "Green", "Blue", "Aqua", "Cyan", "Lime"
    );

    public NPCCommand(
        NPCManager npcManager,
        NPCSkinManager skinManager
    ) {
        this.npcManager = npcManager;
        this.skinManager = skinManager;
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

            if (npcManager.createNPC(player, args[1]) == null) {
                player.sendMessage(ChatColor.RED + "NPC already exists!");
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

        if (args[0].equalsIgnoreCase("rename")) {
            if (args.length < 3) {
                player.sendMessage(
                    ChatColor.RED + "Usage: /npc rename <old> <new>"
                );
                return true;
            }

            if (!npcManager.renameNPC(args[1], args[2])) {
                player.sendMessage(
                    ChatColor.RED + "NPC not found or new name already exists!"
                );
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "NPC renamed to " + args[2]);
            return true;
        }

        if (args[0].equalsIgnoreCase("glow")) {

            if (args.length < 4 ||
                !args[1].equalsIgnoreCase("add")) {

                player.sendMessage(
                    ChatColor.RED + "Usage: /npc glow add <name> <color>"
                );
                return true;
            }

            if (!COLORS.stream().anyMatch(
                color -> color.equalsIgnoreCase(args[3])
            )) {
                player.sendMessage(
                    ChatColor.RED + "Invalid color! Available: "
                    + String.join(", ", COLORS)
                );
                return true;
            }

            if (!npcManager.addGlow(args[2], args[3])) {
                player.sendMessage(ChatColor.RED + "NPC not found!");
                return true;
            }

            player.sendMessage(
                ChatColor.GREEN + "Glow applied to "
                + ChatColor.YELLOW + args[2]
                + ChatColor.GREEN + " with color "
                + ChatColor.YELLOW + args[3]
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("skin")) {

            if (args.length < 4) {
                player.sendMessage(
                    ChatColor.RED + "Usage: /npc skin <name> <skin>"
                );
                return true;
            }

            player.sendMessage(
                ChatColor.YELLOW + "Loading skin for " + args[2] + "..."
            );

            skinManager.setSkin(args[2], args[3], success -> {

                if (success) {
                    player.sendMessage(
                        ChatColor.GREEN + "NPC skin updated successfully!"
                    );
                } else {
                    player.sendMessage(
                        ChatColor.RED + "Could not load skin. Check NPC and skin name."
                    );
                }
            });

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
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc rename <old> <new>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc glow add <name> <color>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/npc skin <name> <skin>");
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
                Arrays.asList(
                    "create", "movehere", "copy",
                    "lookme", "rename", "glow", "skin"
                ),
                new ArrayList<>()
            );
        }

        if (args.length == 2 &&
            Arrays.asList("movehere", "copy", "lookme", "rename", "skin")
                .contains(args[0].toLowerCase())) {

            return StringUtil.copyPartialMatches(
                args[1],
                npcManager.getNPCs().keySet(),
                new ArrayList<>()
            );
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("glow")) {
            return StringUtil.copyPartialMatches(
                args[1],
                Collections.singletonList("add"),
                new ArrayList<>()
            );
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("glow")
            && args[1].equalsIgnoreCase("add")) {

            return StringUtil.copyPartialMatches(
                args[2],
                npcManager.getNPCs().keySet(),
                new ArrayList<>()
            );
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("glow")
            && args[1].equalsIgnoreCase("add")) {

            return StringUtil.copyPartialMatches(
                args[3],
                COLORS,
                new ArrayList<>()
            );
        }

        return Collections.emptyList();
    }
}
