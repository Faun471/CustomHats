package me.faun.customhats.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;

import me.faun.customhats.CustomHats;
import net.kyori.adventure.text.Component;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ItemUtils {

    /**
     *  Returns a GuiItem as configured in the config file.
     *
     *  @param config   The config file where the item can be found
     *  @param path     The path of the item in the config file.
     */
    public static GuiItem getItemFromConfig (FileConfiguration config, String path) {
        ConfigurationSection configurationSection = config.getConfigurationSection(path);

        Map<String, Object> values = configurationSection.getValues(false);
        String materialName = (String) values.getOrDefault("type","DEAD_BUSH");
        Material material = Optional.ofNullable(Material.matchMaterial(materialName)).orElse(Material.DEAD_BUSH);

        List<Component> lore = new ArrayList<>();
        for (String text : CustomHats.getInstance().getConfig().getStringList(path + ".lore")) {
            lore.add(StringUtils.messageParse(text));
        }

        if (material == Material.PLAYER_HEAD) {
            return ItemBuilder.skull()
                    .texture((String) values.getOrDefault("texture", ""))
                    .name(StringUtils.messageParse((String) values.getOrDefault("name", null)))
                    .glow((Boolean) values.getOrDefault("glow", false))
                    .amount((Integer) values.getOrDefault("amount", 1))
                    .model((Integer) values.getOrDefault("model", 0))
                    .lore(Optional.of(lore).orElse(new ArrayList<>()))
                    .pdc(persistentDataContainer -> persistentDataContainer.set(new NamespacedKey(CustomHats.getInstance(),
                            "custom-hat"), PersistentDataType.STRING, path)).asGuiItem();
        }
        return ItemBuilder
                .from(material)
                .name(StringUtils.messageParse((String) values.getOrDefault("name", null)))
                .glow((Boolean) values.getOrDefault("glow", false))
                .amount((Integer) values.getOrDefault("amount", 1))
                .model((Integer) values.getOrDefault("model", 0))
                .lore(Optional.of(lore).orElse(new ArrayList<>()))
                .pdc(persistentDataContainer -> persistentDataContainer.set(new NamespacedKey(CustomHats.getInstance(),
                        "custom-hat"), PersistentDataType.STRING, path))
                .asGuiItem();
    }
}
