package com.burchard36.cloudlite.gui;

import com.burchard36.cloudlite.gui.buttons.InventoryButton;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public abstract class InventoryGui implements GuiHandler {

    @Getter
    private final Inventory inventory;
    private final HashMap<Integer, InventoryButton> inventoryButtons = new HashMap<>();

    public InventoryGui() {
        this.inventory = this.createInventory();
    }

    public abstract Inventory createInventory();


    public void fillButtons() {
        this.inventoryButtons.forEach((slot, button) -> this.inventory.setItem(slot, button.getDisplayItem()));
    }

    public void addButton(final int slot, InventoryButton button) {
        this.inventoryButtons.put(slot, button);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final int slotClicked = event.getSlot();
        final InventoryButton inventoryButton = this.inventoryButtons.get(slotClicked);
        if (inventoryButton != null) inventoryButton.onClick(event);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent event) {
        this.fillButtons();
    }
}
