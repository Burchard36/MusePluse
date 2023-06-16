package com.burchard36.cloudlite.events;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class TexturePackLoadEvent implements Listener {
    protected final CloudLiteMusicPlayer moduleInstance;

    public TexturePackLoadEvent(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @EventHandler
    public void onTexturePackLoad(final PlayerResourcePackStatusEvent changeEvent) {
        final Player player = changeEvent.getPlayer();
        switch (changeEvent.getStatus()) {
            case DECLINED -> {
                player.sendMessage(convert("&cIt seems you rejected the resource pack!"));
                player.sendMessage(convert("&cIf you change your mind rejoin and/or visit&b /discord"));
                player.sendMessage(convert("&cTo manually download it!"));
            }
            case FAILED_DOWNLOAD -> {
                player.sendMessage(convert("&cUnfortunatly it seems the resource pack has failed to download"));
                player.sendMessage(convert("&cTry relogging or visiting our &b/discord&c to manually download it!"));
            }
            case SUCCESSFULLY_LOADED -> {
                this.moduleInstance.getMusicPlayer().playFor(player);
            }
        }
    }
}
