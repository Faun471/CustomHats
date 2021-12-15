package me.faun.customhats.commands;

import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.HatUtils;
import me.faun.customhats.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnequipCommand extends Command {
    public UnequipCommand() {
        super(Collections.singletonList("unequip"), "This will remove the player's hat.", "", "/hats unequip <player> <hat>", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0:
            case 1:
                if (sender instanceof Player) unequip((Player) sender, null);
                else StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("not-a-player"));
                return true;
            case 2:
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) unequip(player, null);
                else StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("no-hat"));
                return true;
            case 3:
                if (!sender.hasPermission( "customhats.unequip.other")) {
                    StringUtils.sendComponent(sender,CustomHats.getConfigManager().getConfig("messages").getString("no-perms"));
                    return true;
                }
                player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("not-online"));
                    return false;
                } else {
                    unequip(player, args[2], sender);
                    return true;
                }
        }
        return true;
    }

    public void unequip(Player player, @Nullable String hatName) {
        ItemStack hat = hatName != null ? CustomHats.getHatManager().getHats().get(hatName) : null;
        ItemStack playerHelmet = player.getEquipment().getHelmet();
        ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;

        if (!HatUtils.isHat(playerHelmetMeta)) {
            StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("no-hat"));
            return;
        } else if (HatUtils.comparePDC(HatUtils.getPDC(playerHelmetMeta), HatUtils.getPDC(hat.getItemMeta()))) {
            player.getEquipment().setHelmet(new ItemStack(Material.AIR));
            StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("hat-unequip-success")
                    .replace("%hat%",hat.getItemMeta().getDisplayName()));
            return;
        }

        StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("invalid-hat")
                .replace("%hat%", hatName));

    }

    public void unequip(Player player, String hatName, CommandSender sender) {
        ItemStack playerHelmet = player.getEquipment().getHelmet();

        ItemStack hat = CustomHats.getHatManager().getHats().get(hatName);
        if (hat == null) {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("invalid-hat")
                    .replace("%hat%", hatName));
            return;
        }

        ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;
        if (!HatUtils.isHat(playerHelmetMeta)) {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("no-hat-other"));
        } else if (HatUtils.comparePDC(HatUtils.getPDC(playerHelmetMeta), HatUtils.getPDC(hat.getItemMeta()))) {
            player.getEquipment().setHelmet(new ItemStack(Material.AIR));
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("hat-unequip-success-other")
                    .replace("%hat%",hat.getItemMeta().getDisplayName()));
        } else {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("doesnt-match-hat")
                    .replace("%hat%",hat.getItemMeta().getDisplayName()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 2) {
            return StringUtils.onlinePlayerList();
        }
        if (args.length == 3 && sender.hasPermission("customhats.unequip.other")) {
            return CustomHats.getConfigManager().getConfig("config").getBoolean("only-show-owned-hats")
                    ? new ArrayList<>(CustomHats.getHatManager().getAvailableHats(sender)) : new ArrayList<>(CustomHats.getHatManager().getHats().keySet());
        }
        return Collections.emptyList();
    }
}
