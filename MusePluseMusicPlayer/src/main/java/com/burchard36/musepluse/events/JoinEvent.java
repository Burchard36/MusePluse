package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.ResourcePackEngine;
import com.burchard36.musepluse.resource.events.MusePluseResourcePackLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class JoinEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;
    protected final ResourcePackEngine resourcePackEngine;
    /* Since the resource pack may not be created when the player joins we need to queue them if the file isnt created yet */
    protected final List<UUID> queuedPlayers;

    public JoinEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.resourcePackEngine = this.moduleInstance.getResourcePackEngine();
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
        this.queuedPlayers = new ArrayList<>();
    }

    @EventHandler
    public final void onPlayerLeave(final PlayerQuitEvent quitEvent) {
        final Player quitter = quitEvent.getPlayer();
        this.moduleInstance.getMusicPlayer().clear(quitter);
    }

    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
        if (!this.musePluseSettings.isPlayOnJoin()) return;
        if (this.musePluseSettings.isNeedsPermissionToPlayOnJoin() && !player.hasPermission("musepluse.playonjoin"))
            return;

        if (this.musePluseSettings.isDoItYourselfMode()) {
            return; // server owners are managing themselves let them do theyre thing
        }

        if (this.resourcePackEngine.isResourcePackGenerating()) {
            this.queuedPlayers.add(player.getUniqueId());
            player.sendMessage(convert("&cIt appears the server hasn't finished creating the resource pack yet!"));
            player.sendMessage(convert("&cOnce the resource pack creation is finished your game will automagically update!"));
            return;
        }
        final File file = this.resourcePackEngine.resourcePackFileFromDisk();
        if (file == null) throw new RuntimeException("The resource pack in /resource-pack does not exist! Why? Did you delete it? Restart your server!");
        this.musePluseSettings.getResourcePack(file, player::setResourcePack);
    }

    @EventHandler
    public void onResourcePackCreated(final MusePluseResourcePackLoadedEvent loadedEvent) {
        Bukkit.getConsoleSender().sendMessage(convert("&fResource pack will now be sent to any queued players..."));
        this.queuedPlayers.forEach(uuid -> {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(convert("&aThe resource pack has been created! It is being sent to you now."));
                if (this.musePluseSettings.isResourcePackServerEnabled()) {
                    final File file = this.resourcePackEngine.resourcePackFileFromDisk();
                    if (file == null) throw new RuntimeException("The resource pack in /resource-pack does not exist! Why? Did you delete it? Restart your server!");
                    this.musePluseSettings.getResourcePack(file, player::setResourcePack);
                } else Bukkit.getConsoleSender().sendMessage(convert("&cSeems the embedded resource pack serer is disabled, the resource pack set in the settings.yml will be sent to your players isntead!"));
            }
        });

        this.queuedPlayers.clear();
    }
}
