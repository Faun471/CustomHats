package me.faun.customhats.listeners;

import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        if (ItemUtils.isHat(item.getItemMeta()) && event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            CustomHats.getCommands().unequipCommand.unequip(player);
            return;
        }
        item.setType(Material.AIR);
    }

    @EventHandler
    public void onCreativeClickEvent(InventoryCreativeEvent event) {
        ItemStack stack = event.getCurrentItem();

        if (stack == null || !stack.hasItemMeta()) {
            stack = event.getCursor();
            if (stack.getType() == Material.AIR) {
                return;
            }
        }
        if (ItemUtils.isHat(stack.getItemMeta())) {
            stack.setType(Material.AIR);
            event.setCancelled(true);
        }
    }
}
