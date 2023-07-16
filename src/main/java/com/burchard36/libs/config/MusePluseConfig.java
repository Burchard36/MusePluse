package com.burchard36.libs.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MusePluseConfig implements Config {

    @Getter
    protected final List<SongData> songDataList;

    public MusePluseConfig() {
        this.songDataList = new ArrayList<>();
    }

    @Override
    public @NonNull String getFileName() {
        return "songs.yml";
    }

    @Override
    public void deserialize(FileConfiguration config) {
        this.songDataList.clear();
        for (String songLocalKey : config.getKeys(false)) {
            final ConfigurationSection songConfig = config.getConfigurationSection(songLocalKey);
            assert songConfig != null;
            this.songDataList.add(new SongData(songConfig, songLocalKey));
        }
    }
}
