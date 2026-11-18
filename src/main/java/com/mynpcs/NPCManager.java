
package com.mynpcs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    private final Map<String, Mannequin> npcs = new HashMap<>();
    private final Map<String, Team> glowTeams = new HashMap<>();

    public Mannequin createNPC(Player player, String name) {
        if (exists(name)) return null;

        Mannequin npc = player.getWorld().spawn(
            player.getLocation(),
            Mannequin.class,
            mannequin -> {
                mannequin.setCustomName(name);
                mannequin.setCustomNameVisible(true);
                mannequin.setInvulnerable(true);
                mannequin.setImmovable(true);
                mannequin.setGravity(false);
                mannequin.setPersistent(true);
                mannequin.setProfile(Mannequin.defaultProfile());
            }
        );

        npcs.put(name.toLowerCase(), npc);
        return npc;
    }

    public String copyNPC(Player player, String sourceName) {
        Mannequin source = getNPC(sourceName);

        if (source == null || !source.isValid()) return null;

        String copyName = sourceName + "_copy";
        int number = 2;

        while (exists(copyName)) {
            copyName = sourceName + "_copy" + number++;
        }

        Mannequin copy = player.getWorld().spawn(
            player.getLocation(),
            Mannequin.class,
            mannequin -> {
                mannequin.setCustomName(copyName);
                mannequin.setCustomNameVisible(true);
                mannequin.setInvulnerable(source.isInvulnerable());
                mannequin.setImmovable(source.isImmovable());
                mannequin.setGravity(source.hasGravity());
                mannequin.setPersistent(true);
                mannequin.setProfile(source.getProfile());

                copyEquipment(
                    source.getEquipment(),
                    mannequin.getEquipment()
                );
            }
        );

        npcs.put(copyName.toLowerCase(), copy);
        return copyName;
    }

    public boolean lookMe(Player player, String name) {
        Mannequin npc = getNPC(name);

        if (npc == null || !npc.isValid()) return false;

        Location direction = npc.getLocation();

        direction.setDirection(
            player.getLocation().toVector()
                .subtract(direction.toVector())
        );

        npc.teleport(direction);
        return true;
    }

    public boolean renameNPC(String oldName, String newName) {
        Mannequin npc = getNPC(oldName);

        if (npc == null || !npc.isValid() || exists(newName)) {
            return false;
        }

        npcs.remove(oldName.toLowerCase());
        npc.setCustomName(newName);
        npc.setCustomNameVisible(true);
        npcs.put(newName.toLowerCase(), npc);

        Team oldTeam = glowTeams.remove(oldName.toLowerCase());

        if (oldTeam != null) {
            oldTeam.unregister();
        }

        return true;
    }

    public boolean addGlow(String name, String colorName) {
        Mannequin npc = getNPC(name);

        if (npc == null || !npc.isValid()) return false;

        ChatColor color;

        try {
            color = switch (colorName.toUpperCase()) {
                case "YELLOW" -> ChatColor.YELLOW;
                case "BLACK" -> ChatColor.BLACK;
                case "WHITE" -> ChatColor.WHITE;
                case "PURPLE" -> ChatColor.LIGHT_PURPLE;
                case "RED" -> ChatColor.RED;
                case "GREEN", "LIME" -> ChatColor.GREEN;
                case "BLUE" -> ChatColor.BLUE;
                case "AQUA" -> ChatColor.AQUA;
                case "CYAN" -> ChatColor.DARK_AQUA;
                default -> null;
            };
        } catch (Exception exception) {
            return false;
        }

        if (color == null) return false;

        Scoreboard scoreboard = Bukkit.getScoreboardManager()
            .getMainScoreboard();

        String teamName = "mn" +
            npc.getUniqueId().toString().replace("-", "").substring(0, 14);

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        Team previousTeam = glowTeams.put(name.toLowerCase(), team);

        if (previousTeam != null && previousTeam != team) {
            previousTeam.unregister();
        }

        team.setColor(color);
        team.addEntry(npc.getUniqueId().toString());

        npc.setGlowing(true);

        return true;
    }

    private void copyEquipment(
        EntityEquipment source,
        EntityEquipment target
    ) {
        if (source == null || target == null) return;

        target.setArmorContents(cloneItems(source.getArmorContents()));
        target.setItemInMainHand(cloneItem(source.getItemInMainHand()));
        target.setItemInOffHand(cloneItem(source.getItemInOffHand()));
    }

    private ItemStack[] cloneItems(ItemStack[] items) {
        ItemStack[] cloned = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            cloned[i] = cloneItem(items[i]);
        }

        return cloned;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item == null ? null : item.clone();
    }

    public Mannequin getNPC(String name) {
        return npcs.get(name.toLowerCase());
    }

    public boolean exists(String name) {
        return npcs.containsKey(name.toLowerCase());
    }

    public Map<String, Mannequin> getNPCs() {
        return npcs;
    }
}
