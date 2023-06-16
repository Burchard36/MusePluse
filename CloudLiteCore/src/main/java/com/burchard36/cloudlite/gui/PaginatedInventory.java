package com.burchard36.cloudlite.gui;

import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class PaginatedInventory {

    private final HashMap<Integer, InventoryGui> inventoryPages = new HashMap<>();

    public void addPage(final int page, final InventoryGui inventoryGui) {
        this.inventoryPages.put(page, inventoryGui);
    }

    public @Nullable InventoryGui getPage(final int page) {
        return this.inventoryPages.get(page);
    }
}
