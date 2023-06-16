package com.burchard36.cloudlite.events;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    protected final CloudLiteMusicPlayer moduleInstance;

    public JoinEvent(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        player.setResourcePack(this.moduleInstance.getMusicListConfig().getResourcePackURL());
    }
}
