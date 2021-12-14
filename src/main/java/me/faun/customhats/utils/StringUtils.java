package me.faun.customhats.utils;

import me.faun.customhats.CustomHats;
import me.mattstudios.msg.adventure.AdventureMessage;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class StringUtils {
    public static Component messageParse(String text) {
        AdventureMessage message = AdventureMessage.create();
        return message.parse(text.replace("%prefix%",
                Optional.ofNullable(CustomHats.getConfigManager().getConfig("messages").getString("prefix")).orElse("Hats")));
    }

    public static void sendComponent(Player player, String message) {
        CustomHats.getInstance().adventure().player(player).sendMessage(messageParse(message));
    }

    public static void sendComponent(CommandSender sender, String message) {
        CustomHats.getInstance().adventure().sender(sender).sendMessage(messageParse(message));
    }

    public static List<String> onlinePlayerList() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

}
