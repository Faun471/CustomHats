package me.faun.customhats;

import me.faun.customhats.commands.CommandManager;
import me.faun.customhats.commands.Commands;

import me.faun.customhats.listeners.InventoryClickListener;
import me.faun.customhats.listeners.PlayerDeathListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class CustomHats extends JavaPlugin {

    private static CustomHats instance;
    private static Commands commands;
    private static CommandManager commandManager;
    private static HatManager hatManager;
    private static ConfigManager configManager;

    private BukkitAudiences adventure;

    public @NotNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        commands = new Commands();
        hatManager = new HatManager();
        adventure = BukkitAudiences.create(this);
        commandManager = new CommandManager("customhats");
        commandManager.registerCommands();
        configManager.reloadConfigs();
        hatManager.initHats();
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public static CustomHats getInstance() {
        return instance;
    }

    public static Commands getCommands() {
        return commands;
    }

    public static HatManager getHatManager(){
        return hatManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

}
