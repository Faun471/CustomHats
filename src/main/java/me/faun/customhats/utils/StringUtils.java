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

    /**
     *  Returns a Component of the parsed message. This will also replace all instances of %prefix%.
     *
     *  @param text     The text to parse.
     */
    public static Component messageParse(String text) {
        AdventureMessage message = AdventureMessage.create();
        return message.parse(text.replace("%prefix%",
                Optional.ofNullable(CustomHats.getConfigManager().getConfig("messages").getString("prefix")).orElse("Hats")));
    }

    /**
     *  This will send a Component to a player
     *
     *  @param player   The player that will receive the message.
     *  @param message  The message that will be sent to the player.
     */
    public static void sendComponent(Player player, String message) {
        CustomHats.getInstance().adventure().player(player).sendMessage(messageParse(message));
    }

    /**
     *  This will send a Component to a CommandSender
     *
     *  @param sender   The CommandSender that will receive the message.
     *  @param message  The message that will be sent to the player.
     */
    public static void sendComponent(CommandSender sender, String message) {
        CustomHats.getInstance().adventure().sender(sender).sendMessage(messageParse(message));
    }

    /**
     *  This will return a list of string containing all online players in the server.
     */
    public static List<String> onlinePlayerList() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
