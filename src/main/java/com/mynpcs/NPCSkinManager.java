
package com.mynpcs;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mannequin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

import java.util.function.Consumer;

public class NPCSkinManager {

    private final JavaPlugin plugin;
    private final NPCManager npcManager;

    public NPCSkinManager(JavaPlugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    public void setSkin(
        String npcName,
        String skinName,
        Consumer<Boolean> callback
    ) {
        Mannequin npc = npcManager.getNPC(npcName);

        if (npc == null || !npc.isValid()) {
            callback.accept(false);
            return;
        }

        try {
            PlayerProfile profile = Bukkit.createProfile(skinName);

            profile.update().whenComplete((updatedProfile, error) -> {
                Bukkit.getScheduler().runTask(plugin, () -> {

                    if (error != null || updatedProfile == null) {
                        callback.accept(false);
                        return;
                    }

                    if (!npc.isValid()) {
                        callback.accept(false);
                        return;
                    }

                    if (!updatedProfile.getTextures().isSigned()) {
                        callback.accept(false);
                        return;
                    }

                    npc.setProfile(
                        ResolvableProfile.resolvableProfile(
                            (com.destroystokyo.paper.profile.PlayerProfile)
                                updatedProfile
                        )
                    );

                    callback.accept(true);
                });
            });

        } catch (IllegalArgumentException exception) {
            callback.accept(false);
        }
    }
}
