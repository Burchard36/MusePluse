package com.burchard36.musepluse;

import com.burchard36.libs.config.ConfigManager;
import com.burchard36.libs.config.MusePluseConfig;
import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.libs.ffmpeg.FFMPEGDownloader;
import com.burchard36.libs.ffmpeg.events.FFMPEGInitializedEvent;
import com.burchard36.libs.gui.GuiEvents;
import com.burchard36.libs.gui.GuiManager;
import com.burchard36.musepluse.commands.SkipSongCommand;
import com.burchard36.musepluse.events.TexturePackLoadEvent;
import lombok.Getter;
import com.burchard36.musepluse.commands.MusicGuiCommand;
import com.burchard36.musepluse.commands.ReloadMusicCommand;
import com.burchard36.musepluse.events.JoinEvent;
import com.burchard36.musepluse.events.RespawnEvent;
import com.burchard36.musepluse.events.SongEndedEvent;
import com.burchard36.musepluse.resource.ResourcePackEngine;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.burchard36.libs.utils.StringUtils.convert;

/**
 * Main class for MusePluse, this was converted from a module project to a normal project for easier management of code
 * As the modules aren't really being used, we will only support latest version of minecraft & use no NMS so we are fine
 * without maven modules :)) - Dalton
 *
 * @since 2.1.0
 */
public class MusePlusePlugin extends JavaPlugin implements Listener {
    /* Thread pool to use for heavy tasks */
    public static Executor MAIN_THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
    public static MusePlusePlugin INSTANCE;
    @Getter
    private FFMPEGDownloader ffmpegDownloader;
    @Getter
    private ConfigManager configManager;
    @Getter
    private Random random;
    @Getter
    private GuiManager guiManager;
    @Getter
    private Permission vaultPermissions;
    private GuiEvents guiEvents;
    @Getter
    private MusePlusePlugin pluginInstance;
    @Getter
    private MusePluseSettings musePluseSettings;
    @Getter
    private MusePluseConfig musicListConfig;
    @Getter
    private ResourcePackEngine resourcePackEngine;
    @Getter
    private MusicPlayer musicPlayer;

    protected static String OS = System.getProperty("os.name").toLowerCase();
    public static boolean IS_WINDOWS = (OS.contains("win"));
    public static boolean IS_MAC = (OS.contains("mac"));
    public static boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    protected static boolean IS_SOLARIS = (OS.contains("sunos"));
    public static boolean IS_AARCH_64 = System.getProperty("os.arch").startsWith("aarch");
    public static boolean IS_ARM_64 = System.getProperty("os.arch").startsWith("arm");

    @Override
    public void onLoad() {
        // First initialize the base libraries and utilities
        INSTANCE = this;
        random = new Random();
        this.ffmpegDownloader = new FFMPEGDownloader(this);
        this.ffmpegDownloader.installFFMPEG();
        this.configManager = new ConfigManager(this);
        this.guiManager = new GuiManager();

        // Then we can
        this.pluginInstance = this;
        this.musePluseSettings = this.pluginInstance.getConfigManager().getConfig(new MusePluseSettings(), false);
        this.musicListConfig = this.pluginInstance.getConfigManager().getConfig(new MusePluseConfig(), false);
        this.musicPlayer = new MusicPlayer(this);

        Bukkit.getConsoleSender().sendMessage("Done! MusePluse has finished its onLoad initialization!");
        Bukkit.getConsoleSender().sendMessage("If there was any errors please contact a developer.");
    }
    @Override
    public void onEnable() {
        registerEvent(this);
        this.guiEvents = new GuiEvents(guiManager);
        registerEvent(this.guiEvents);

        if (this.ffmpegDownloader.ffmpegIsInstalled() && !this.ffmpegDownloader.isDownloading())
            Bukkit.getPluginManager().callEvent(new FFMPEGInitializedEvent()); // we need to make sure this event gets fired on enable

        this.resourcePackEngine = new ResourcePackEngine(this);
        MusePlusePlugin.registerCommand("skipsong", new SkipSongCommand(this));
        MusePlusePlugin.registerCommand("musicgui", new MusicGuiCommand(this));
        MusePlusePlugin.registerCommand("reloadmusic", new ReloadMusicCommand(this));

        MusePlusePlugin.registerEvent(new JoinEvent(this));
        MusePlusePlugin.registerEvent(new RespawnEvent(this));
        MusePlusePlugin.registerEvent(new SongEndedEvent(this));
        MusePlusePlugin.registerEvent(new TexturePackLoadEvent(this));

        new BStatsImpl(this);
    }

    @Override
    public void onDisable() {

    }

    public static void registerEvent(final Listener eventListener) {
        Bukkit.getConsoleSender().sendMessage(convert("Registering event &b%s&f.".formatted(eventListener.getClass().getName())));
        INSTANCE.getServer().getPluginManager().registerEvents(eventListener, INSTANCE);
    }

    public static void registerCommand(final String commandName, final CommandExecutor executor) {
        Bukkit.getConsoleSender().sendMessage(convert("&fAttempting to register command&b%s".formatted(commandName)));
        INSTANCE.getCommand(commandName).setExecutor(executor);
    }
}
