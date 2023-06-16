package com.burchard36.cloudlite.events;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import com.burchard36.cloudlite.MusicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class SongEndedEvent implements Listener {
    protected final CloudLiteMusicPlayer moduleInstance;

    public SongEndedEvent(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @EventHandler
    public void onSongEnded(final com.burchard36.cloudlite.events.custom.SongEndedEvent endEvent) {
        final Player player = endEvent.getPlayer();
        Bukkit.getLogger().info(convert("SongEndedEvent received for %s, processing next song. . .".formatted(player.getName())));
        this.moduleInstance.getMusicPlayer().playFor(endEvent.getPlayer());
    }
}
