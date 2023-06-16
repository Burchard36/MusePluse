package com.burchard36.cloudlite.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GuiEvents implements Listener  {

    protected final GuiManager manager;

    public GuiEvents(final GuiManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent clickEvent) {
        this.manager.handleInventoryClick(clickEvent);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent openEvent) {
        this.manager.handleInventoryOpen(openEvent);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent closeEvent) {
        this.manager.handleInventoryClose(closeEvent);
    }

}
