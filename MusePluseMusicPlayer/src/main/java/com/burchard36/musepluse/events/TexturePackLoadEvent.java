package com.burchard36.musepluse.events;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.ResourcePackEngine;
import com.burchard36.musepluse.utils.TaskRunner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class TexturePackLoadEvent implements Listener {
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;
    protected final ResourcePackEngine resourcePackEngine;
    protected final List<UUID> hasFailed;

    public TexturePackLoadEvent(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
        this.resourcePackEngine = this.moduleInstance.getResourcePackEngine();
        this.hasFailed = new ArrayList<>();
    }

    @EventHandler
    public void onTexturePackLoad(final PlayerResourcePackStatusEvent changeEvent) {
        final Player player = changeEvent.getPlayer();
        switch (changeEvent.getStatus()) {
            case DECLINED -> player.sendMessage(convert("&cResource pack loading failed! Try &e/reloadresources&c if you change your mind!"));
            case FAILED_DOWNLOAD -> {
                if (this.hasFailed.contains(player.getUniqueId())) {
                    player.sendMessage(convert("&cPlease re-log to apply resource pack changes!"));
                    return;
                }

                player.sendMessage(convert("Attempting to re-download resource pack in 3 seconds, please close this window"));
                this.hasFailed.add(player.getUniqueId());
                TaskRunner.runSyncTaskLater(() -> {
                    final File file = this.resourcePackEngine.resourcePackFileFromDisk();
                    player.setResourcePack(this.musePluseSettings.getResourcePack(file), createSha1());
                }, 60);
            }
            case SUCCESSFULLY_LOADED -> {
                if (musePluseSettings.isNeedsPermissionToPlayOnJoin() && player.hasPermission("musepluse.playonjoin")) {
                    this.moduleInstance.getMusicPlayer().playRandomQueueFor(player);
                }
            }

        }
    }

    public byte[] createSha1() {
        try (InputStream fis = new BufferedInputStream(new FileInputStream(this.resourcePackEngine.resourcePackFileFromDisk()))) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
            return digest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
