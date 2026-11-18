
package com.mynpcs;

import org.bukkit.plugin.java.JavaPlugin;

public final class MyNPCs extends JavaPlugin {

    private NPCManager npcManager;

    @Override
    public void onEnable() {

        getLogger().info("Starting MyNPCs...");

        npcManager = new NPCManager();

        NPCCommand npcCommand = new NPCCommand(npcManager);

        if (getCommand("npc") != null) {
            getCommand("npc").setExecutor(npcCommand);
            getCommand("npc").setTabCompleter(npcCommand);
        } else {
            getLogger().severe("NPC command missing!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("MyNPCs enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyNPCs disabled!");
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }
}
