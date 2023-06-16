package com.burchard36.cloudlite.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MusicListConfig implements Config {

    @Getter
    protected final List<SongData> songDataList;
    @Getter
    private String resourcePackURL;

    public MusicListConfig() {
        this.songDataList = new ArrayList<>();
    }

    @Override
    public @NonNull String getFileName() {
        return "music/songs.yml";
    }

    @Override
    public void deserialize(FileConfiguration config) {
        this.songDataList.clear();
        final ConfigurationSection songsConfig = config.getConfigurationSection("MusicPlayer");
        this.resourcePackURL = config.getString("ResourcePack");
        assert songsConfig != null;
        for (String songLocalKey : songsConfig.getKeys(false)) {
            final ConfigurationSection songConfig = songsConfig.getConfigurationSection(songLocalKey);
            assert songConfig != null;
            this.songDataList.add(new SongData(songConfig, songLocalKey));
        }
    }
}
