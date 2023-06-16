package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class TexturePackLoadEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;

    public TexturePackLoadEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
    }

    @EventHandler
    public void onTexturePackLoad(final PlayerResourcePackStatusEvent changeEvent) {
        final Player player = changeEvent.getPlayer();
        switch (changeEvent.getStatus()) {
            case DECLINED -> player.sendMessage(convert("&cResource pack loading failed! Try &e/reloadresources&c if you change your mind!"));
            case FAILED_DOWNLOAD -> player.sendMessage(convert("&cDownload failed! Ensure that the server administrator has set the configuration correctly."));
            case SUCCESSFULLY_LOADED -> {
                if (musePluseSettings.isNeedsPermissionToPlayOnJoin() && player.hasPermission("musepluse.playonjoin")) {
                    this.moduleInstance.getMusicPlayer().playRandomQueueFor(player);
                }
            }
        }
    }
}
