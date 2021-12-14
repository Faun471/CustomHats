package me.faun.customhats.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public abstract class Command {

    public final @NotNull List<String> aliases;
    public final @NotNull List<Command> childs;
    public final @NotNull String description;
    public final @NotNull String permission;
    public final @NotNull String syntax;
    public final boolean onlyForPlayers;

    public Command(@NotNull List<String> aliases, @NotNull String description, @NotNull String permission, @NotNull String syntax, boolean onlyForPlayers) {
        this.aliases = aliases;
        this.childs = new ArrayList<>();
        this.description = description;
        this.syntax = syntax;
        this.permission = permission;
        this.onlyForPlayers = onlyForPlayers;
    }

    public void addChilds(Command... newChilds) {
        childs.addAll(Arrays.asList(newChilds));
    }

    Optional<Command> getChildByName(String name) {
        return childs.stream()
                .filter(command -> command.aliases.contains(name.toLowerCase()))
                .findAny();
    }

    public List<String> getChildNames() {
        return childs.stream()
                .map(command -> command.aliases.get(0))
                .collect(Collectors.toList());
    }


    public abstract boolean execute(CommandSender sender, String[] arguments);

    public abstract List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

}
