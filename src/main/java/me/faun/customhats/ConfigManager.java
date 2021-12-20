package me.faun.customhats;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigManager {

    private final HashMap<String, FileConfiguration> configs;

    public ConfigManager() {
        this.configs = new HashMap<>();
    }

    public void reloadConfigs() {
        configs.clear();
        Bukkit.getLogger().fine("Reloading...");
        loadConfig("config");
        loadConfig("messages");
    }

    public FileConfiguration getConfig(String name) {
        if (!configs.containsKey(name)) {
            loadConfig(name);
        }

        return configs.get(name);
    }

    public void loadConfig(String name) {
        String fileName = name + ".yml";
        File file = new File(CustomHats.getInstance().getDataFolder(), fileName);
        System.out.println(fileName + " path is " + file.getPath());

        if (!file.exists()) {
            System.out.println(fileName + "doesn't exist.");
            file.getParentFile().mkdirs();
            CustomHats.getInstance().saveResource(fileName, false);
        }

        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Successfully loaded " + config);
        }

        configs.put(name, config);
    }
}
