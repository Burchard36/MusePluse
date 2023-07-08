package com.burchard36.libs.config;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

public interface Config {

    @NonNull String getFileName();


    void deserialize(FileConfiguration configuration);
}
