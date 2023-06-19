package com.burchard36.musepluse.ffmpeg.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FFMPEGInitializedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public FFMPEGInitializedEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
