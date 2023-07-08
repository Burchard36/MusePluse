package com.burchard36.musepluse.commands;

import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.resource.ResourcePackEngine;
import com.burchard36.musepluse.MusicPlayer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.burchard36.libs.utils.StringUtils.convert;

public class ReloadMusicCommand implements CommandExecutor {

    protected final MusePlusePlugin pluginInstance;
    protected final MusePluseSettings moduleSettings;
    protected final ResourcePackEngine resourcePackEngine;
    protected final MusicPlayer musicPlayer;

    public ReloadMusicCommand(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.moduleSettings = this.pluginInstance.getMusePluseSettings();
        this.resourcePackEngine = this.pluginInstance.getResourcePackEngine();
        this.musicPlayer = this.pluginInstance.getMusicPlayer();

    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!sender.hasPermission("musepluse.reload")) return false;

        sender.sendMessage(convert("&aMusePluse will now attempt to rebuild the songs.yml into a new texture pack!"));
        sender.sendMessage(convert("&aYour current listening experience will not be interrupted until this process is complete! (This may take a minute!)"));


        this.pluginInstance.getPluginInstance().getConfigManager().reloadAll();
        this.resourcePackEngine.tryAutoGenerate(true, () -> {

            if (this.moduleSettings.isDoItYourselfMode()) {
                sender.sendMessage(convert("&cSeems you have DoItYourself mode enabled! Your players will have to manually download the new resource pack in order to hear new songs!"));
                return;
            }

            if (this.moduleSettings.isResourcePackServerEnabled()) {
                this.moduleSettings.getResourcePack(this.resourcePackEngine.resourcePackFileFromDisk(), (resourcePack) -> {
                    this.musicPlayer.stopForAll(); // stop listening here so plays have a near seamless transition!

                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        player.sendMessage(convert("&aDue to a server reload, your resource pack is being reloaded!"));
                        player.setResourcePack(resourcePack);

                    });

                    sender.sendMessage(convert("&aThe music list has successfully been reloaded and the resource pack has been updated for your players!"));
                });
            }
        });
        return false;
    }
}
