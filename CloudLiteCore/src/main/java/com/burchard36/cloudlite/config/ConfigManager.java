package com.burchard36.cloudlite.config;

import com.burchard36.cloudlite.utils.StringUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final JavaPlugin pluginInstance;
    private final List<Config> configurationFiles;

    public ConfigManager(final JavaPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.configurationFiles = new ArrayList<>();
    }

    @SneakyThrows
    public <T extends Config> T getConfig(final T config) {
        if (this.configurationFiles.contains(config)) throw new IllegalStateException("Config %s is already registered".formatted(config.getClass().getName()));
        final File configFile = new File(this.pluginInstance.getDataFolder(), config.getFileName());
        if (!configFile.exists()) {
            this.pluginInstance.saveResource(config.getFileName(), false);
            Bukkit.getLogger().info(StringUtils.convert("&bCreating new config file&f%s&b...").formatted(config.getFileName()));
            return this.getConfig(config);
        }

        final FileConfiguration configData = YamlConfiguration.loadConfiguration(configFile);
        config.deserialize(configData);
        this.configurationFiles.add(config);
        return config;
    }

    public void reloadAll() {
        this.configurationFiles.forEach(config -> {
            final File configFile = new File(this.pluginInstance.getDataFolder(), config.getFileName());
            final FileConfiguration configData = YamlConfiguration.loadConfiguration(configFile);
            config.deserialize(configData);
        });
    }

}
