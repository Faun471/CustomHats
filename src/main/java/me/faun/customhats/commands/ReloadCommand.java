package me.faun.customhats.commands;

import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super(Collections.singletonList("reload"), "The plugin's reload command", "reload", "/hats reload", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments) {
        FileConfiguration messages = CustomHats.getConfigManager().getConfig("messages");

        CustomHats.getConfigManager().reloadConfigs();
        CustomHats.getHatManager().initHats();
        StringUtils.sendComponent(sender,messages.getString("plugin-reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
