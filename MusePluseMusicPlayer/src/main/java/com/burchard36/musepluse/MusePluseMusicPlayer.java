package com.burchard36.musepluse;

import com.burchard36.musepluse.commands.MusicGuiCommand;
import com.burchard36.musepluse.commands.SkipSongCommand;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.config.MusePluseConfig;
import com.burchard36.musepluse.events.JoinEvent;
import com.burchard36.musepluse.events.SongEndedEvent;
import com.burchard36.musepluse.events.TexturePackLoadEvent;
import com.burchard36.musepluse.module.PluginModule;
import lombok.Getter;

public final class MusePluseMusicPlayer implements PluginModule {
    @Getter
    private MusePlusePlugin pluginInstance;
    @Getter
    private MusePluseSettings musePluseSettings;
    @Getter
    private MusePluseConfig musicListConfig;
    @Getter
    private MusicPlayer musicPlayer;

    @Override
    public void loadModule(final MusePlusePlugin coreInstance) {
        this.pluginInstance = coreInstance;
        this.musePluseSettings = this.pluginInstance.getConfigManager().getConfig(new MusePluseSettings(), false);
        this.musicListConfig = this.pluginInstance.getConfigManager().getConfig(new MusePluseConfig(), true);
        this.musicPlayer = new MusicPlayer(this);
    }

    @Override
    public void enableModule() {
        MusePlusePlugin.registerCommand("skipsong", new SkipSongCommand(this));
        MusePlusePlugin.registerCommand("musicgui", new MusicGuiCommand(this));

        MusePlusePlugin.registerEvent(new JoinEvent(this));
        MusePlusePlugin.registerEvent(new SongEndedEvent(this));
        MusePlusePlugin.registerEvent(new TexturePackLoadEvent(this));
    }

    @Override
    public void disableModule() {
    }

    @Override
    public void reload() {
        this.musePluseSettings = this.pluginInstance.getConfigManager().getConfig(new MusePluseSettings(), false);
        this.musicListConfig = this.pluginInstance.getConfigManager().getConfig(new MusePluseConfig(), true);
    }
}