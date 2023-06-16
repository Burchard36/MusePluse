package com.burchard36.cloudlite.gui.buttons;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryButton {

    @Getter
    private final ItemStack displayItem;

    public InventoryButton(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public abstract void onClick(final InventoryClickEvent clickEvent);
}
