package me.faun.customhats;

import me.faun.customhats.utils.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class HatManager {

    private final NamespacedKey key;
    private final HashMap<String, ItemStack> allHats;

    public HatManager() {
        this.key = new NamespacedKey(CustomHats.getInstance(),"custom-hat");
        this.allHats = new HashMap<>();
    }

    public NamespacedKey getKey() {
        return key;
    }


    public HashMap<String, ItemStack> getAllHats() {
        return this.allHats;
    }

    public Set<String> getAvailableHats(CommandSender sender) {
        Set<String> hats = new HashSet<>();
        for (String hat : getAllHats().keySet()) {
            if (sender.hasPermission("customhats.hat." + hat)) hats.add(hat);
        }
        return hats;
    }

    public void initHats(){
        allHats.clear();
        FileConfiguration config = CustomHats.getConfigManager().getConfig("config");
        Set<String> hats = config.getConfigurationSection("hats").getKeys(false);

        for (String hat : hats) {
            ItemStack hatItem = (ItemUtils.getItemFromConfig(config,"hats." + hat).getItemStack());
            allHats.put(hat, hatItem);
        }
        Bukkit.getLogger().info("Loaded " + allHats.size() + " hats! ");
    }

}
