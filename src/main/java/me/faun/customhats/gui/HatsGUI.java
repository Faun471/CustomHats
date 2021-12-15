package me.faun.customhats.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

import me.faun.customhats.CustomHats;
import me.faun.customhats.utils.HatUtils;
import me.faun.customhats.utils.ItemUtils;
import me.faun.customhats.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.Set;


public class HatsGUI {
    public void send(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(StringUtils.messageParse(Optional.ofNullable(CustomHats.getConfigManager().getConfig("config").getString("gui-title")).orElse("Hats GUI")))
                .rows(Optional.of(CustomHats.getConfigManager().getConfig("config").getInt("gui-rows")).orElse(3))
                .create();

        gui.setOpenGuiAction(event -> Bukkit.getScheduler().runTaskAsynchronously(CustomHats.getInstance(),
                () -> HatUtils.updateGlow(gui, ((Player) event.getPlayer()))));

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            if (gui.isUpdating()) return;
            else gui.update();
            ((Player) event.getWhoClicked()).updateInventory();
        });

        for (int slot : CustomHats.getConfigManager().getConfig("config").getIntegerList("border-slots")) {
            gui.setItem(slot, ItemUtils.getItemFromConfig(CustomHats.getConfigManager().getConfig("config"),"border-item"));
        }

        putButtons(gui);

        putHats(gui, player);

        gui.open(player);
    }

    private void putButtons(PaginatedGui gui) {
        FileConfiguration config = CustomHats.getConfigManager().getConfig("config");
        GuiItem previousButton = ItemUtils.getItemFromConfig(config,"previous-button");
        GuiItem nextButton = ItemUtils.getItemFromConfig(config,"next-button");
        GuiItem closeButton = ItemUtils.getItemFromConfig(config,"close-button");

        previousButton.setAction(event -> {
            gui.previous();
            HatUtils.updateGlow(gui, ((Player) event.getWhoClicked()));
        });
        nextButton.setAction(event -> {
            gui.next();
            HatUtils.updateGlow(gui, ((Player) event.getWhoClicked()));
        });

        closeButton.setAction(event -> gui.close(event.getWhoClicked()));

        int nextRow = config.getInt("next-button.row");
        int nextColumn = config.getInt("next-button.column");

        int previousRow = config.getInt("previous-button.row");
        int previousColumn = config.getInt("previous-button.column");

        int closeRow = config.getInt("close-button.row");
        int closeColumn = config.getInt("close-button.column");

        // Put the buttons in the GUI
        gui.setItem(nextRow, nextColumn, nextButton);
        gui.setItem(previousRow, previousColumn, previousButton);
        gui.setItem(closeRow,closeColumn, closeButton);
    }

    private void putHats(PaginatedGui gui, Player player) {
        Set<String> hats;

        if (CustomHats.getConfigManager().getConfig("config").getBoolean("only-show-owned-hats")) {
            hats = CustomHats.getHatManager().getAvailableHats(player);
        } else {
            hats = CustomHats.getConfigManager().getConfig("config").getConfigurationSection("hats").getKeys(false);
        }

        for (String hat : hats) {
            GuiItem hatItem = ItemUtils.getItemFromConfig(CustomHats.getConfigManager().getConfig("config"),"hats." + hat);
            hatItem.setAction(event -> {
                Bukkit.getScheduler().runTaskAsynchronously(CustomHats.getInstance(), () -> HatUtils.updateGlow(gui, ((Player) event.getWhoClicked())));

                ItemStack playerHelmet = player.getEquipment().getHelmet();
                ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;
                ItemMeta hatMeta = event.getCurrentItem().getItemMeta();

                if (HatUtils.canEquip(player, hatMeta)) {
                    CustomHats.getCommands().equipCommand.equip(player, hat);
                } else if (HatUtils.comparePDC(HatUtils.getPDC(playerHelmetMeta), HatUtils.getPDC(hatMeta))) {
                    CustomHats.getCommands().unequipCommand.unequip(player, hat);
                }

                gui.update();
            });

            gui.addItem(hatItem);
        }
    }
}