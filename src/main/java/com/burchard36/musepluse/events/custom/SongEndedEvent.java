package com.burchard36.musepluse.events.custom;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SongEndedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    protected final Player player;

    public SongEndedEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
