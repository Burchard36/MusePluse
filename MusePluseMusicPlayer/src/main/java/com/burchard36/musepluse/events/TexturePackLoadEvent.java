package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.MusicListener;
import com.burchard36.musepluse.MusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.ResourcePackEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class TexturePackLoadEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;
    protected final ResourcePackEngine resourcePackEngine;
    protected final MusicPlayer musicPlayer;
    protected final List<UUID> hasFailed;

    public TexturePackLoadEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musicPlayer = this.moduleInstance.getMusicPlayer();
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
        this.resourcePackEngine = this.moduleInstance.getResourcePackEngine();
        this.hasFailed = new ArrayList<>();
    }

    @EventHandler
    public void onTexturePackLoad(final PlayerResourcePackStatusEvent changeEvent) {
        final Player player = changeEvent.getPlayer();
        switch (changeEvent.getStatus()) {
            case DECLINED -> player.sendMessage(convert("&cResource pack loading failed! Try &e/reloadresources&c if you change your mind!"));
            case FAILED_DOWNLOAD -> player.sendMessage(convert("&cDownload failed! Try relogging to fix this!"));
            case SUCCESSFULLY_LOADED -> {
                if (!musePluseSettings.isPlayOnJoin()) return;
                if (musePluseSettings.isNeedsPermissionToPlayOnJoin() && player.hasPermission("musepluse.playonjoin")) {
                    if (!this.musicPlayer.hasAutoPlayEnabled(player)) return;
                    this.musicPlayer.playNextSong(player);
                }
            }
        }
    }
}
