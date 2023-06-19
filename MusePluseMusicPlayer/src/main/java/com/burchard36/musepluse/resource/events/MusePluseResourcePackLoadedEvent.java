package com.burchard36.musepluse.resource.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MusePluseResourcePackLoadedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public MusePluseResourcePackLoadedEvent() {

    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}