package com.burchard36.cloudlite.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class GuiManager {

    protected final HashMap<Inventory, GuiHandler> activeInventories;

    public GuiManager() {
        this.activeInventories = new HashMap<>();
    }

    public void openInventoryTo(final Player player, InventoryGui inventoryGui) {
        this.registerInventory(inventoryGui.getInventory(), inventoryGui);
        player.openInventory(inventoryGui.getInventory());
    }

    public void openPaginatedTo(final Player player, final int page, PaginatedInventory paginatedInventory) {
        final Inventory inventory = paginatedInventory.getPage(page).getInventory();
        if (inventory == null) throw new IllegalArgumentException("Paginated Inventory may not be null!");
        this.openInventoryTo(player, paginatedInventory.getPage(page));
    }

    public void registerInventory(final Inventory inventory, final GuiHandler guiHandler) {
        this.activeInventories.put(inventory, guiHandler);
    }

    public final void unRegisterInventory(final Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    public final void handleInventoryClick(final InventoryClickEvent clickEvent) {
        final GuiHandler guiHandler = this.activeInventories.get(clickEvent.getClickedInventory());
        if (guiHandler != null) guiHandler.onInventoryClick(clickEvent);
    }

    public final void handleInventoryClose(final InventoryCloseEvent closeEvent) {
        final GuiHandler guiHandler = this.activeInventories.get(closeEvent.getInventory());
        if (guiHandler != null) {
            guiHandler.onInventoryClose(closeEvent);
            this.unRegisterInventory(closeEvent.getInventory());
        }
    }

    public final void handleInventoryOpen(final InventoryOpenEvent openEvent) {
        final GuiHandler guiHandler = this.activeInventories.get(openEvent.getInventory());
        if (guiHandler != null) guiHandler.onInventoryOpen(openEvent);
    }

}
