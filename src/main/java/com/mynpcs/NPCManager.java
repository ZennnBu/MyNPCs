
package com.mynpcs;

import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    private final Map<String, Mannequin> npcs = new HashMap<>();

    public Mannequin createNPC(Player player, String name) {

        if (npcs.containsKey(name.toLowerCase())) {
            return null;
        }

        Location location = player.getLocation();

        Mannequin npc = location.getWorld().spawn(
            location,
            Mannequin.class,
            mannequin -> {
                mannequin.setCustomName(Component.text(name));
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
