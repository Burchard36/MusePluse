package com.burchard36.libs.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public interface GuiHandler {

    void onInventoryClick(final InventoryClickEvent event);

    void onInventoryClose(final InventoryCloseEvent event);

    void onInventoryOpen(final InventoryOpenEvent event);

}
