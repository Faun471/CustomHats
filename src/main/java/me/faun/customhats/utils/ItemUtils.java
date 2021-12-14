package me.faun.customhats.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;

import dev.triumphteam.gui.guis.PaginatedGui;
import me.faun.customhats.CustomHats;
import me.mattstudios.msg.adventure.AdventureMessage;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ItemUtils {

    public static Component messageParse(String text) {
        AdventureMessage message = AdventureMessage.create();
        return message.parse(text);
    }

    public static GuiItem getItemFromConfig (FileConfiguration config, String path) {
        ConfigurationSection configurationSection = config.getConfigurationSection(path);

        Map<String, Object> values = configurationSection.getValues(false);
        String materialName = (String) values.getOrDefault("type","DEAD_BUSH");
        Material material = Optional.ofNullable(Material.matchMaterial(materialName)).orElse(Material.DEAD_BUSH);

        List<Component> lore = new ArrayList<>();
        for (String text : CustomHats.getInstance().getConfig().getStringList(path + ".lore")) {
            lore.add(ItemUtils.messageParse(text));
        }

        if (material == Material.PLAYER_HEAD) {
            return ItemBuilder.skull()
                    .texture((String) values.getOrDefault("texture", ""))
                    .name(ItemUtils.messageParse((String) values.getOrDefault("name", null)))
                    .glow((Boolean) values.getOrDefault("glow", false))
                    .amount((Integer) values.getOrDefault("amount", 1))
                    .model((Integer) values.getOrDefault("model", 0))
                    .lore(Optional.of(lore).orElse(new ArrayList<>()))
                    .pdc(persistentDataContainer -> persistentDataContainer.set(new NamespacedKey(CustomHats.getInstance(),
                            "custom-hat"), PersistentDataType.STRING, path)).asGuiItem();
        }
        return ItemBuilder
                .from(material)
                .name(ItemUtils.messageParse((String) values.getOrDefault("name", null)))
                .glow((Boolean) values.getOrDefault("glow", false))
                .amount((Integer) values.getOrDefault("amount", 1))
                .model((Integer) values.getOrDefault("model", 0))
                .lore(Optional.of(lore).orElse(new ArrayList<>()))
                .pdc(persistentDataContainer -> persistentDataContainer.set(new NamespacedKey(CustomHats.getInstance(),
                        "custom-hat"), PersistentDataType.STRING, path))
                .asGuiItem();
    }

    public static String getPDC(ItemMeta meta) {
        if (!isHat(meta)) return "";
        return meta.getPersistentDataContainer().getOrDefault(CustomHats.getHatManager().key, PersistentDataType.STRING,"");
    }

    public static boolean comparePDC (String pdc1, String pdc2) {
        return pdc1.equals(pdc2);
    }

    public static void changeGlow (ItemStack item, boolean glow) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.hasEnchants() && glow) {
            meta.addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else if (!glow) {
            for (final Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        item.setItemMeta(meta);
    }

    public static void updateGlow (PaginatedGui gui, Player player) {
        for (GuiItem g : gui.getCurrentPageItems().values()) {
            ItemStack itemStack = g.getItemStack();
            ItemMeta meta = itemStack.getItemMeta();

            changeGlow(itemStack, player.getEquipment().getHelmet() != null
                    && ItemUtils.comparePDC(ItemUtils.getPDC(player.getEquipment().getHelmet().getItemMeta()),
                    ItemUtils.getPDC(meta)));
            gui.update();
        }
    }

    public static boolean isHat(@Nullable ItemMeta meta) {
        if (meta == null|| !meta.hasCustomModelData()) return false;
        return meta.getPersistentDataContainer().has(CustomHats.getHatManager().key, PersistentDataType.STRING);
    }

}
