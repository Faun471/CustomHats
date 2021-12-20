package me.faun.customhats.listeners;

import me.faun.customhats.utils.HatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops =  event.getDrops();
        drops.removeIf(drop -> HatUtils.isHat(drop.getItemMeta()));
    }
}
