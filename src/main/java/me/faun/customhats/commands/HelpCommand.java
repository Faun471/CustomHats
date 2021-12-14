package me.faun.customhats.commands;

import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(Collections.singletonList("help"), "The plugin's help command", "", "/hats help", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Commands commands = CustomHats.getCommands();
        if (args.length < 2) sendHelp(sender);
        else {
            String command = args[1];
            if (command.equalsIgnoreCase("hats")) sendHelp(sender);
            if (command.equalsIgnoreCase("menu")) sendCommandHelp(sender, commands.menuCommand);
            if (command.equalsIgnoreCase("equip")) sendCommandHelp(sender, commands.equipCommand);
            if (command.equalsIgnoreCase("unequip")) sendCommandHelp(sender, commands.unequipCommand);
            if (command.equalsIgnoreCase("reload")) sendCommandHelp(sender, commands.reloadCommand);
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("help-header"));
        for (Command cmd : CustomHats.getCommandManager().getAvailableCommands(sender).stream().distinct().collect(Collectors.toList())) {
                    StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("command-help")
                            .replace("%command%", cmd.aliases.get(0))
                            .replace("%usage%", cmd.syntax)
                            .replace("%description%", cmd.description));
        }
        StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("help-footer"));
    }

    public void sendCommandHelp (CommandSender sender, Command command) {
        StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("help-header"));
            StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("command-help")
                    .replace("%command%", command.aliases.get(0))
                    .replace("%usage%", command.syntax)
                    .replace("%description%", command.description));
        StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("help-footer"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 2) {
            List<String> availableCommands = new ArrayList<>();
            List<Command> commands = CustomHats.getCommandManager().getAvailableCommands(sender);
            for (Command cmd : commands) {
                availableCommands.add(cmd.aliases.get(0));
            }
            return availableCommands;
        }
        return Collections.emptyList();
    }
}
