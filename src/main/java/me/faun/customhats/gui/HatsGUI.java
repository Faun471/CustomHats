package me.faun.customhats.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

import me.faun.customhats.CustomHats;
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
        FileConfiguration config = CustomHats.getConfigManager().getConfig("config");

        PaginatedGui gui = Gui.paginated()
                .title(StringUtils.messageParse(Optional.ofNullable(config.getString("gui-title")).orElse("Hats GUI")))
                .rows(Optional.of(config.getInt("gui-rows")).orElse(3))
                .create();

        gui.setOpenGuiAction(event -> Bukkit.getScheduler().runTaskAsynchronously(CustomHats.getInstance(),
                () -> ItemUtils.updateGlow(gui, ((Player) event.getPlayer()))));

        // Border
        for (int slot : config.getIntegerList("border-slots")) gui.setItem(slot, ItemUtils.getItemFromConfig(config,"border-item"));

        // Previous and Next buttons
        GuiItem previousButton = ItemUtils.getItemFromConfig(config,"previous-button");
        GuiItem nextButton = ItemUtils.getItemFromConfig(config,"next-button");
        GuiItem closeButton = ItemUtils.getItemFromConfig(config,"close-button");

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            if (gui.isUpdating()) return;
            else gui.update();
            ((Player) event.getWhoClicked()).updateInventory();
        });

        previousButton.setAction(event -> {
            gui.previous();
            ItemUtils.updateGlow(gui, ((Player) event.getWhoClicked()));
        });
        nextButton.setAction(event -> {
            gui.next();
            ItemUtils.updateGlow(gui, ((Player) event.getWhoClicked()));
        });

        closeButton.setAction(event -> gui.close(player));

        int nextRow = config.getInt("next-button.row"), previousRow = config.getInt("previous-button.row"), closeRow = config.getInt("close-button.row");
        int nextColumn = config.getInt("next-button.column"), previousColumn = config.getInt("previous-button.column"), closeColumn = config.getInt("close-button.column");

        // Put the buttons in the GUI
        gui.setItem(nextRow, nextColumn, nextButton);
        gui.setItem(previousRow, previousColumn, previousButton);
        gui.setItem(closeRow,closeColumn, closeButton);

        // Put every hat in the GUI
        Set<String> hats = config.getBoolean("only-show-owned-hats") ?
                CustomHats.getHatManager().getAvailableHats(player) : config.getConfigurationSection("hats").getKeys(false);
        for (String hat : hats) {
            GuiItem hatItem = ItemUtils.getItemFromConfig(config,"hats." + hat);
            hatItem.setAction(event -> {
                Player p = (Player) event.getWhoClicked();
                Bukkit.getScheduler().runTaskAsynchronously(CustomHats.getInstance(), () -> ItemUtils.updateGlow(gui, ((Player) event.getWhoClicked())));

                ItemStack playerHelmet = p.getEquipment().getHelmet();
                ItemMeta playerHelmetMeta = playerHelmet != null ? playerHelmet.getItemMeta() : null;
                ItemMeta hatMeta = event.getCurrentItem().getItemMeta();

                if (playerHelmetMeta == null || (ItemUtils.isHat(playerHelmetMeta) && !ItemUtils.comparePDC(ItemUtils.getPDC(playerHelmetMeta),
                        ItemUtils.getPDC(hatMeta)))) {
                    CustomHats.getCommands().equipCommand.equip(p, hat);
                } else if (ItemUtils.comparePDC(ItemUtils.getPDC(playerHelmetMeta), ItemUtils.getPDC(hatMeta))) {
                    CustomHats.getCommands().unequipCommand.unequip(p, hat);
                }
                gui.update();
            });

            gui.addItem(hatItem);
        }

        gui.open(player);
    }
}