package com.burchard36.cloudlite;

import com.burchard36.cloudlite.config.ConfigManager;
import com.burchard36.cloudlite.gui.GuiEvents;
import com.burchard36.cloudlite.gui.GuiManager;
import com.burchard36.cloudlite.module.ModuleLoader;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

/**
 * Classes wanting to use the API need to extend this class instead
 */
public abstract class CloudLiteCore extends JavaPlugin implements Listener {

    public static CloudLiteCore INSTANCE;
    @Getter
    private ConfigManager configManager;
    @Getter
    private final ModuleLoader moduleLoader = new ModuleLoader(this);
    @Getter
    private Random random;
    @Getter
    private GuiManager guiManager;
    @Getter
    private Permission vaultPermissions;
    private GuiEvents guiEvents;

    @Override
    public void onLoad() {
        INSTANCE = this;
        random = new Random();
        /* use this time to load things that don't need ant major spigot/world implementations */
        /* Looking at your worldguard */
        Bukkit.getLogger().info(convert("&fInitializing &bConfigManager&f..."));
        this.configManager = new ConfigManager(this);
        Bukkit.getLogger().info(convert("&aDone!"));
        Bukkit.getLogger().info(convert("&fInitializing &bGuiManager&f..."));
        this.guiManager = new GuiManager();
        Bukkit.getLogger().info(convert("&aDone!"));
        Bukkit.getLogger().info(convert("&fSending &bonServerLoadUp&f to registered modules..."));
        this.moduleLoader.onServerLoadUp(this);
        Bukkit.getLogger().info(convert("&aDone&f! &bCloudLiteCore&f has finished its onLoad initialization!"));
        Bukkit.getLogger().info(convert("&fIf there was any &cerrors&f please contact a &bdeveloper&f."));
    }
    @Override
    public void onEnable() {
        registerEvent(this); // listen for HeadDatabase
        this.guiEvents = new GuiEvents(guiManager);
        registerEvent(this.guiEvents);
        Bukkit.getLogger().info(convert("&fSending &bonEnable&f to all registered modules..."));
        this.moduleLoader.onServerEnable();
        Bukkit.getLogger().info(convert("&aDone&f! &bCloudLiteCore&f has finished its onEnable initialization!"));
        Bukkit.getLogger().info(convert("&fIf there was any &cerrors&f please review your configs before contacting a &bdeveloper&f."));
    }

    @Override
    public void onDisable() {
        this.moduleLoader.onServerShutdown();
    }

    public static void registerEvent(final Listener eventListener) {
        Bukkit.getLogger().info(convert("Registering event &b%s&f.".formatted(eventListener.getClass().getName())));
        INSTANCE.getServer().getPluginManager().registerEvents(eventListener, INSTANCE);
    }

    public static void registerCommand(final String commandName, final CommandExecutor executor) {
        Bukkit.getLogger().info(convert("&fAttempting to register command&b%s".formatted(commandName)));
        INSTANCE.getCommand(commandName).setExecutor(executor);
    }
}
