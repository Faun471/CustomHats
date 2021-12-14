package me.faun.customhats;

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
        reloadConfigs();
    }

    public void reloadConfigs() {
        configs.clear();
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

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            CustomHats.getInstance().saveResource(fileName, false);
        }

        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        configs.put(name, config);
    }
}
