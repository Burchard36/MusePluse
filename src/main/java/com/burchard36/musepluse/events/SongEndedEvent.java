package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePlusePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SongEndedEvent implements Listener {
    protected final MusePlusePlugin pluginInstance;
    public SongEndedEvent(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onSongEnded(final com.burchard36.musepluse.events.custom.SongEndedEvent endEvent) {
        this.pluginInstance.getMusicPlayer().playNextSong(endEvent.getPlayer());
    }
}
