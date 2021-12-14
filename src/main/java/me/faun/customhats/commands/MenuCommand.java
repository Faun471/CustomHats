package me.faun.customhats.commands;

import me.faun.customhats.gui.HatsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MenuCommand extends Command {

    public MenuCommand() {
        super(Collections.singletonList("menu"), "This will open the hat selection gui", "", "/hats menu", true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;
        new HatsGUI().send(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
