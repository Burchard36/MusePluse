package com.burchard36.cloudlite;

import com.burchard36.cloudlite.commands.MusicGuiCommand;
import com.burchard36.cloudlite.commands.SkipSongCommand;
import com.burchard36.cloudlite.config.MusicListConfig;
import com.burchard36.cloudlite.events.JoinEvent;
import com.burchard36.cloudlite.events.SongEndedEvent;
import com.burchard36.cloudlite.events.TexturePackLoadEvent;
import com.burchard36.cloudlite.module.PluginModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public final class CloudLiteMusicPlayer implements PluginModule {
    @Getter
    private CloudLiteCore pluginInstance;
    @Getter
    private MusicListConfig musicListConfig;
    @Getter
    private MusicPlayer musicPlayer;

    @Override
    public void loadModule(final CloudLiteCore coreInstance) {
        Bukkit.getLogger().info(convert("&fLoading &bCloudLiteMusicPlayer"));
        this.pluginInstance = coreInstance;
        this.musicListConfig = this.pluginInstance.getConfigManager().getConfig(new MusicListConfig());
        this.musicPlayer = new MusicPlayer(this);
    }

    @Override
    public void enableModule() {
        CloudLiteCore.registerCommand("skipsong", new SkipSongCommand(this));
        CloudLiteCore.registerCommand("musicgui", new MusicGuiCommand(this));

        CloudLiteCore.registerEvent(new JoinEvent(this));
        CloudLiteCore.registerEvent(new SongEndedEvent(this));
        CloudLiteCore.registerEvent(new TexturePackLoadEvent(this));
        Bukkit.getLogger().info(convert("&bCloudLiteMusicPlayer&f successfully&aenabled&f."));
    }

    @Override
    public void disableModule() {
        Bukkit.getLogger().info(convert("&cDisabling CloudLiteMusicPlayer"));
    }

    @Override
    public void reload() {
        Bukkit.getLogger().info(convert("&fReloading &bCloudLiteMusicPlayer"));
        this.musicListConfig = this.pluginInstance.getConfigManager().getConfig(new MusicListConfig());

        this.musicPlayer.setMusicConfig(this.musicListConfig);
        Bukkit.getLogger().info(convert("&bCloudLiteMusicPlayer&f reloaded!"));
    }
}