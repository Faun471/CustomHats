package me.faun.customhats.commands;

import me.faun.customhats.CustomHats;

import me.faun.customhats.utils.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    public final List<Command> commands = new ArrayList<>();
    FileConfiguration messages = CustomHats.getConfigManager().getConfig("messages");

    public CommandManager(String command) {
        CustomHats.getInstance().getCommand(command).setExecutor(this);
        CustomHats.getInstance().getCommand(command).setTabCompleter(this);
        registerCommands();
    }


    public void registerCommands() {
        Commands commands = CustomHats.getCommands();
        // This code registers all commands from the Command config automatically
        for (Field field : commands.getClass().getFields()) {
            try {
                Command command = (Command) field.get(commands);
                registerCommand(command);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void registerCommand(Command command) {
        int index = Collections.binarySearch(commands, command, Comparator.comparing(cmd -> cmd.aliases.get(0)));
        commands.add(index < 0 ? -(index + 1) : index, command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                CustomHats.getCommands().menuCommand.execute(player, new String[]{});
            } else {
                StringUtils.sendComponent(sender, CustomHats.getConfigManager().getConfig("messages").getString("not-a-player"));
            }
            return true;
        }

        for (Command command : commands) {
            // We don't want to execute other commands or ones that are disabled
            if (!(command.aliases.contains(args[0]))) {
                continue;
            }
            Command executingCommand = findExecutingCommand(command, args);
            if (executionBlocked(executingCommand, sender)) {
                return false;
            }
            return executingCommand.execute(sender, args);
        }
        CustomHats.getCommands().helpCommand.sendHelp(sender);
        return false;
    }

    private boolean executionBlocked(Command command, CommandSender sender) {
        // Check if this command is only for players
        if (command.onlyForPlayers && !(sender instanceof Player)) {
            StringUtils.sendComponent(sender, messages.getString("not-a-player"));
            return true;
        }

        // Check permissions
        if (!hasPermissions(sender, command)) {
            StringUtils.sendComponent(sender, messages.getString("no-perms"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String[] args) {
        // Handle the tab completion if it's the sub-command selection
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();

            for (Command command : commands) {
                for (String alias : command.aliases) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && hasPermissions(sender, command)) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        // Let the sub-command handle the tab completion
        for (Command command : commands) {
            if (command.aliases.contains(args[0])) {
                Command executingCommand = findExecutingCommand(command, args);
                if (hasPermissions(sender, executingCommand)) {
                    List<String> tabCompletion = new ArrayList<>(executingCommand.onTabComplete(sender, cmd, label, args));
                    tabCompletion.addAll(executingCommand.getChildNames());
                    return filterTabCompletionResults(tabCompletion, args);
                }
            }
        }

        // Return a new List, so it isn't a list of online players
        return Collections.emptyList();
    }

    private boolean hasPermissions(@NotNull CommandSender sender, Command command) {
        return sender.hasPermission(command.permission)
                || command.permission.equalsIgnoreCase("")
                || command.permission.equalsIgnoreCase("customhats.");
    }

    private Command findExecutingCommand(Command baseCommand, String[] args) {
        Command executingCommand = baseCommand;

        // Check for each argument if it's a child of the previous command
        for (int currentArgument = 1; currentArgument < args.length; currentArgument++) {
            Optional<Command> child = executingCommand.getChildByName(args[currentArgument]);
            if (!child.isPresent()) break;

            executingCommand = child.get();
        }
        return executingCommand;
    }

    private List<String> filterTabCompletionResults(List<String> tabCompletion, String[] arguments) {
        return tabCompletion.stream()
                .filter(completion -> completion.toLowerCase().contains(arguments[arguments.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Command> getAvailableCommands(CommandSender sender) {
        List<Command> availableCommands = new ArrayList<>();
        List<Command> commands = CustomHats.getCommandManager().commands;
        for (Command cmd : commands) {
            if (sender.hasPermission(cmd.permission) || cmd.permission.isEmpty()) {
                availableCommands.add(cmd);
            }
        }
        return availableCommands;
    }

}
