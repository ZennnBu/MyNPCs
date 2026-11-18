
package com.mynpcs;

import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    private final Map<String, Mannequin> npcs = new HashMap<>();

    public Mannequin createNPC(Player player, String name) {

        if (exists(name)) return null;

        Location location = player.getLocation();

        Mannequin npc = location.getWorld().spawn(
            location,
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

        if (source == null || !source.isValid()) {
            return null;
        }

        String copyName = sourceName + "_copy";
        int number = 2;

        while (exists(copyName)) {
            copyName = sourceName + "_copy" + number;
            number++;
        }

        Location location = player.getLocation();

        Mannequin copy = location.getWorld().spawn(
            location,
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
