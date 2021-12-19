package me.faun.customhats.utils;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.faun.customhats.CustomHats;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HatUtils {

    /**
     *  Returns an ItemStack's PersistentDataContainer if it has one or an empty string if it doesn't.
     *
     *  @param meta  The ItemMeta that we'll get the PersistentDataContainer from.
     */
    public static String getPDC(ItemMeta meta) {
        if (!isHat(meta)) return "";
        return meta.getPersistentDataContainer().getOrDefault(CustomHats.getHatManager().key, PersistentDataType.STRING,"");
    }

    /**
     *  This will either make an item glow or remove its glow effect.
     *
     *  @param item  The item that will be changed.
     *  @param glow  This will determine whether the item should glow or not.
     *               Additionally, this will remove all the item's enchantments.
     */
    public static void changeGlow(@NotNull ItemStack item, boolean glow) {
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

    /**
     *  This will manually update the items in the gui that should or shouldn't glow.
     *
     *  @param gui      The gui that the player is viewing.
     *  @param player   The player that is viewing the gui.
     */
    public static void updateGlow(PaginatedGui gui, Player player) {
        if (gui.isUpdating()) return;
        for (GuiItem g : gui.getCurrentPageItems().values()) {
            ItemStack itemStack = g.getItemStack();
            ItemMeta meta = itemStack.getItemMeta();

            changeGlow(itemStack, player.getEquipment().getHelmet() != null
                    && HatUtils.getPDC(player.getEquipment().getHelmet().getItemMeta()).equals(
                    HatUtils.getPDC(meta)));

            gui.update();
        }
    }

    /**
     *  Returns true if the item is a hat by checking if it has the correct PersistentDataContainer.
     *
     *  @param meta  The item that will be checked.
     */
    public static boolean isHat(@Nullable ItemMeta meta) {
        if (meta == null|| !meta.hasCustomModelData()) return false;
        return meta.getPersistentDataContainer().has(CustomHats.getHatManager().key, PersistentDataType.STRING);
    }

    /**
     *  Returns true if a specified player can equip the hat.
     *
     *  @param player   The player that will be checked.
     *  @param hatMeta  The hat that the player will equip.
     */
    public static boolean canEquip(Player player, ItemMeta hatMeta) {
        ItemStack playerHelmet = player.getEquipment().getHelmet();
        ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;

        return (playerHelmetMeta == null || (HatUtils.isHat(playerHelmetMeta) && !(HatUtils.getPDC(playerHelmetMeta).equals(HatUtils.getPDC(hatMeta)))));
    }
}
