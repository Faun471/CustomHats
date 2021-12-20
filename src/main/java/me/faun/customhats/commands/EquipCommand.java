package me.faun.customhats.commands;

import me.faun.customhats.ConfigManager;
import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.HatUtils;
import me.faun.customhats.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EquipCommand extends Command {

    public EquipCommand() {
        super(Collections.singletonList("equip"), "This will give the player a hat", "", "/hats equip <hat> <player>", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0:
            case 1:
                StringUtils.sendComponent(sender, syntax);
                return false;
            case 2:
                if (sender instanceof Player) {
                    equip((Player) sender, args[1]);
                    return true;
                } else {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("not-a-player"));
                    return false;
                }
            case 3:
                if (!sender.hasPermission("customhats.equip.other")) {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("no-perms"));
                    return false;
                }

                Player player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("not-online"));
                    return false;
                } else if (player.getEquipment().getHelmet() == null || HatUtils.isHat(player.getEquipment().getHelmet().getItemMeta())) {
                    equipOther(player, args[1], sender);
                    return true;
                } else {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("helmet-exist-other"));
                    return false;
                }
        }
        return true;
    }

    public void equip(Player player, String hatName) {
        ItemStack hat = CustomHats.getHatManager().getHats().get(hatName);
        if (hat == null) {
            StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("invalid-hat")
                    .replace("%hat%", Optional.of(hatName).orElse(" ")));
            return;
        }

        if (player.getEquipment().getHelmet() != null && !HatUtils.isHat(player.getEquipment().getHelmet().getItemMeta())) {
            StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("helmet-exist"));
            return;
        }

        if (!player.hasPermission("customhats.hat." + hatName)) {
            StringUtils.sendComponent(player, CustomHats.getConfigManager().getConfig("messages").getString("no-perms-hat"));
            return;
        }

        ItemStack playerHelmet = player.getEquipment().getHelmet();
        ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;
        if (playerHelmetMeta == null || HatUtils.isHat(playerHelmetMeta)) {
            player.getEquipment().setHelmet(hat);
            player.sendMessage(hat.getItemMeta().getDisplayName());
            StringUtils.sendComponent(player, (CustomHats.getConfigManager().getConfig("messages").getString("hat-equip-success"))
                    .replace("%hat%", hat.getItemMeta().getDisplayName()));
        }
    }

    public void equipOther(Player player, String hatName, CommandSender sender) {
        ItemStack hat = CustomHats.getHatManager().getHats().get(hatName);
        if (hat == null) {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("invalid-hat")
                    .replace("%hat%", Optional.of(hatName).orElse(" ")));
            return;
        }

        if (!HatUtils.isHat(player.getEquipment().getHelmet().getItemMeta())) {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("helmet-exist-other"));
            return;
        }

        if (!player.hasPermission("customhats.hat." + hatName)) {
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("no-perms-hat-other"));
            return;
        }

        ItemStack playerHelmet = player.getEquipment().getHelmet();
        ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;
        if (playerHelmetMeta == null || HatUtils.isHat(playerHelmetMeta)) {
            player.getEquipment().setHelmet(hat);
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("hat-equip-success-other")
                    .replace("%hat%", hat.getItemMeta().getDisplayName()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(CustomHats.getConfigManager().getConfig("config").getBoolean("only-show-owned-hats")
                    ? CustomHats.getHatManager().getAvailableHats(sender) : CustomHats.getHatManager().getHats().keySet());
        }
        if (args.length == 3 && sender.hasPermission("customhats.equip.other")) {
            return StringUtils.onlinePlayerList();
        }

        return Collections.emptyList();
    }
}
