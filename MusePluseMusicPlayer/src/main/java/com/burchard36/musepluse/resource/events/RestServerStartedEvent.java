package com.burchard36.musepluse.resource.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is only sent if the server had to wait for the ResourcePack to be created
 * otherwise, its is not sent and the server is immediantly started
 *
 * THe servers files don't update fast enough hence why we need to start the rest server AFTER the packs been completely generated
 * and the writers have been closed, or else you may get mismatching SHA1's
 */
public class RestServerStartedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public RestServerStartedEvent() {

    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
