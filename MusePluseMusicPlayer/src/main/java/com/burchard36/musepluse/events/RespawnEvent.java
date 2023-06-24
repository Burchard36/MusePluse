package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.MusicPlayer;
import com.burchard36.musepluse.utils.TaskRunner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {
    protected final MusicPlayer musicPlayer;

    public RespawnEvent(final MusePluseMusicPlayer moduleInstance) {
        this.musicPlayer = moduleInstance.getMusicPlayer();
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent deathEvent) {
        final Player player = deathEvent.getPlayer();

        if (this.musicPlayer.hasAutoPlayEnabled(player)) {
            TaskRunner.runSyncTaskLater(() -> this.musicPlayer.playNextSong(player), 1);
        }
    }

}
