
package com.mynpcs;

import org.bukkit.plugin.java.JavaPlugin;

public final class MyNPCs extends JavaPlugin {

    private NPCManager npcManager;

    @Override
    public void onEnable() {

        getLogger().info("Starting MyNPCs...");

        npcManager = new NPCManager();

        if (getCommand("npc") != null) {
            getCommand("npc").setExecutor(
                new NPCCommand(npcManager)
            );
        } else {
            getLogger().severe("NPC command not found in plugin.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("MyNPCs enabled successfully!");
    }

    @Override
    public void onDisable() {

        if (npcManager != null) {
            npcManager.getNPCs().clear();
        }

        getLogger().info("MyNPCs disabled!");
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }
}
