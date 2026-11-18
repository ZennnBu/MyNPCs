
package com.mynpcs;

import org.bukkit.plugin.java.JavaPlugin;

public final class MyNPCs extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MyNPCs has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyNPCs has been disabled!");
    }
}
