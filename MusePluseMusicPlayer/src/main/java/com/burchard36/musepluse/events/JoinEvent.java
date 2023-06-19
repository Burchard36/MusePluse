package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.ResourcePackFactory;
import com.burchard36.musepluse.resource.ResourcePackServer;
import com.burchard36.musepluse.resource.events.MusePluseResourcePackLoadedEvent;
import com.burchard36.musepluse.utils.TaskRunner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class JoinEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;
    protected final ResourcePackFactory resourcePackFactory;
    /* Since the resource pack may not be created when the player joins we need to queue them if the file isnt created yet */
    protected final List<UUID> queuedPlayers;

    public JoinEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.resourcePackFactory = this.moduleInstance.getResourcePackFactory();
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
        this.queuedPlayers = new ArrayList<>();
    }

    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        if (!this.musePluseSettings.isPlayOnJoin()) return;
        if (this.musePluseSettings.isNeedsPermissionToPlayOnJoin() && !player.hasPermission("musepluse.playonjoin"))
            return;

        if (this.resourcePackFactory.isCreatingResourcePack()) {
            this.queuedPlayers.add(player.getUniqueId());
            player.sendMessage(convert("&cIt appears the server hasn't finished creating the resource pack yet!"));
            player.sendMessage(convert("&cOnce the resource pack creation is finished your game will automagically update!"));
            return;
        }

        player.setResourcePack(this.musePluseSettings.getResourcePack(), this.resourcePackFactory.getResourcePatchHash());
    }

    @EventHandler
    public void onResourcePackCreated(final MusePluseResourcePackLoadedEvent loadedEvent) {
        Bukkit.getConsoleSender().sendMessage(convert("&fResource pack will now be sent to any queued players..."));
        this.queuedPlayers.forEach(uuid -> {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(convert("&aThe resource pack has been created! It is being sent to you now."));
                if (this.musePluseSettings.isResourcePackServerEnabled())
                    ResourcePackServer.startServer(this.moduleInstance);
                TaskRunner.runSyncTaskLater(() -> {
                    player.setResourcePack(this.musePluseSettings.getResourcePack(), this.resourcePackFactory.getResourcePatchHash());
                }, 1);
            }
        });

        this.queuedPlayers.clear();
    }
}
