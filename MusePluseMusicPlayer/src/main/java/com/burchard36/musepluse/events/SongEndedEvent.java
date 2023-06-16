package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SongEndedEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    public SongEndedEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @EventHandler
    public void onSongEnded(final com.burchard36.musepluse.events.custom.SongEndedEvent endEvent) {
        this.moduleInstance.getMusicPlayer().playNextSong(endEvent.getPlayer());
    }
}
