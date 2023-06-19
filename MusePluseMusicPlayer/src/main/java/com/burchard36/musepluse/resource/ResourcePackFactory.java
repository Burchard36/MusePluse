package com.burchard36.musepluse.resource;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.config.MusePluseConfig;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.events.MusePluseResourcePackLoadedEvent;
import com.burchard36.musepluse.resource.events.RestServerStartedEvent;
import com.burchard36.musepluse.utils.TaskRunner;
import com.burchard36.musepluse.youtube.YoutubeGetRequester;
import com.burchard36.musepluse.utils.ZipUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class ResourcePackFactory extends YoutubeGetRequester {

    protected final Gson gson;
    protected final File oggDirectory;
    protected final File m4aDirectory;
    @Getter
    protected final File resourcePackFile;
    protected final File resourcePackTempFiles;
    protected final File soundsJson;
    protected final File mediaDirectory;
    protected final File mcMetaFile;
    protected final MusePlusePlugin pluginInstance;
    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings moduleSettings;
    protected final MusePluseConfig songConfig;
    protected final List<File> downloadedOggFiles = new ArrayList<>();
    @Getter
    protected final AtomicBoolean creatingTexturePack = new AtomicBoolean(false);

    public ResourcePackFactory(final MusePluseMusicPlayer moduleInstance) {
        super(moduleInstance.getPluginInstance());

        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.moduleInstance = moduleInstance;
        this.pluginInstance = this.moduleInstance.getPluginInstance();
        this.songConfig = this.moduleInstance.getMusicListConfig();
        this.moduleSettings = this.moduleInstance.getMusePluseSettings();
        this.mediaDirectory = new File(this.pluginInstance.getDataFolder(), "/media");
        this.oggDirectory = new File(this.mediaDirectory, "/ogg");
        this.m4aDirectory = new File(this.mediaDirectory, "/m4a");
        this.resourcePackTempFiles = new File(this.pluginInstance.getDataFolder(), "/resource-pack/temp");
        this.soundsJson = new File(this.resourcePackTempFiles, "/assets/assets/musepluse/sounds.json");
        this.mcMetaFile = new File(this.resourcePackTempFiles, "/assets/pack.mcmeta");
        this.resourcePackFile = new File(this.pluginInstance.getDataFolder(), "/resource-pack/resource_pack.zip".formatted(UUID.randomUUID()));

        this.soundsJson.mkdirs();
        this.resourcePackTempFiles.mkdirs();

        if (!this.hasResourcePackBeenCreated() && this.moduleSettings.isAutoGenerateResourcePack()) {
            this.creatingTexturePack.set(true);
            this.createTexturePack();
        }
    }

    /**
     * Asynchronously creates a texture pack in the /resource-pack folder of this plugin's data folder
     * THis will block any resource packs being sent to the client until conversion is complete
     */
    @SneakyThrows
    public final void createTexturePack()  {
        this.downloadedOggFiles.clear();
        if (!this.resourcePackFile.createNewFile()) throw new RuntimeException("Could not create resource-pack file!");
        CompletableFuture.runAsync(() -> {
            AtomicInteger downloadedSongs = new AtomicInteger(0);
            final int totalSongsToDownload = this.moduleInstance.getMusicListConfig().getSongDataList().size();
            flushOGGDirectory();
            flushM4ADirectory();
            moduleInstance.getMusicListConfig().getSongDataList().forEach((song) -> {
                final String youTubeLink = song.getYouTubeLink();
                downloadYoutubeVideo(youTubeLink, song.getLocalKey(), (file) -> {
                    int currentDownloadCount = downloadedSongs.incrementAndGet();
                    this.downloadedOggFiles.add(file);
                    Bukkit.getConsoleSender().sendMessage(convert("Successfully downloaded & converted &b%s&f there is &b%s&f songs to download left!".formatted(youTubeLink, totalSongsToDownload - currentDownloadCount)));
                    /* Songs have all downloaded, create the resource pack */
                    if (currentDownloadCount == totalSongsToDownload) {
                        Bukkit.getConsoleSender().sendMessage(convert("&fAll songs have been downloaded! Writing &bsounds.json&f to &b/temp/assets/musepluse/sounds.json"));
                        writeSoundsJson();
                        Bukkit.getConsoleSender().sendMessage(convert("&aComplete!&f Creating pack.mcmeta file to &b/temp/assets/pack.mcmeta"));
                        writeMCMetaFile();
                        Bukkit.getConsoleSender().sendMessage(convert("&aComplete! &fCopying OGG Files to &b/temp/assets/minecraft/sounds/music"));
                        copyOGGToPack();
                        Bukkit.getConsoleSender().sendMessage(convert("&aComplete! &fZipping resources to /resource-pack/resource_pack.zip"));
                        zipResourcePack();
                        Bukkit.getConsoleSender().sendMessage(convert("&aSuccess! &fYour resource path is now created in &b/resource-pack/resource_pack.zip&f !"));
                        Bukkit.getConsoleSender().sendMessage(convert("&fCleaning up temp files & downloaded files..."));
                        this.resourcePackTempFiles.delete();
                        this.mediaDirectory.delete();
                        this.creatingTexturePack.set(false);
                        Bukkit.getConsoleSender().sendMessage("&aCleanup Complete!&f");
                        if (this.moduleSettings.isResourcePackServerEnabled()) {
                            Bukkit.getConsoleSender().sendMessage(convert("&fIf appears the resource-pack server option is enabled in the config"));
                            Bukkit.getConsoleSender().sendMessage(convert("&fServer will now start."));
                            ResourcePackServer.startServer(this.moduleInstance);
                        } else TaskRunner.runSyncTask(() ->
                                Bukkit.getPluginManager().callEvent(new MusePluseResourcePackLoadedEvent()));
                    }
                });
            });
        });
    }

    @EventHandler
    public void onServerStarted(final RestServerStartedEvent startEvent) {
        TaskRunner.runSyncTaskLater(() ->
                Bukkit.getPluginManager().callEvent(new MusePluseResourcePackLoadedEvent()), 20);
    }

    @SneakyThrows
    protected void zipResourcePack() {
        final ZipUtility zipUtility = new ZipUtility();
        File[] tempFiles = new File(this.resourcePackTempFiles, "/assets").listFiles();
        if (tempFiles == null) throw new RuntimeException("It appears the /resource-pack directory was null when calling zipResourcePack, maybe restart your server?");
        zipUtility.zip(List.of(tempFiles), this.resourcePackFile.getPath());
    }

    @SneakyThrows
    protected void writeSoundsJson() {
        if (this.soundsJson.exists()) {
            if (this.soundsJson.delete()) Bukkit.getConsoleSender().sendMessage(convert("Old sounds.json was found, deleting..."));
            this.soundsJson.createNewFile();
        }

        final JsonObject soundsJsonData = new JsonObject();
        this.songConfig.getSongDataList().forEach((song) -> {
            final JsonObject soundsArray = new JsonObject();
            final JsonArray arrayOfSongs = new JsonArray();
            arrayOfSongs.add("music/" + song.getLocalKey());
            soundsArray.add("sounds", arrayOfSongs);
            soundsJsonData.add(song.getLocalKey(), soundsArray);
        });

        final FileWriter writer = new FileWriter(this.soundsJson);
        this.gson.toJson(soundsJsonData, writer);
        writer.flush();
        writer.close();
    }

    @SneakyThrows
    public void writeMCMetaFile() {
        if (this.mcMetaFile.exists()) {
            if (this.mcMetaFile.delete()) Bukkit.getConsoleSender().sendMessage(convert("&fOld pack.mcmeta was found, deleting..."));
            this.mcMetaFile.createNewFile();
        }

        JsonObject object = new JsonObject();
        JsonObject subObject = new JsonObject();
        object.add("pack", subObject);
        subObject.addProperty("pack_format", 13);
        subObject.addProperty("description", "Muse Pluse Resource Pack!");
        final FileWriter writer = new FileWriter(this.mcMetaFile);
        this.gson.toJson(object, writer);
        writer.flush();
        writer.close();
    }

    /**
     * Returns true if the ResorucePackFactory is currently building the resource pack
     *
     * This method is async-safe
     *
     * @return true if resource pack is building
     */
    public final boolean isCreatingResourcePack() {
        return this.creatingTexturePack.get();
    }

    /**
     * Copies the files from the downloadedOggFiles list to /resource-pack/temp/assets/minecraft/sounds/music
     *
     * this method is not async
     */
    protected void copyOGGToPack() {
        File[] files = this.oggDirectory.listFiles();
        if (files == null) throw new RuntimeException("There was no OGG files found in /media/ogg, if this is intended the plugin will disable.");
        final File musicDirectory = new File(this.resourcePackTempFiles, "/assets/assets/minecraft/sounds/music");
        if (!musicDirectory.exists()) musicDirectory.mkdirs();
        for (File file : files) {
            Bukkit.getConsoleSender().sendMessage(convert("Flashing OGG File &b%s").formatted(file.getPath()));
            file.renameTo(new File(musicDirectory, "/%s".formatted(file.getName())));
        }
    }

    /**
     * Flushes the MP3 directory so new ones may be written
     */
    protected void flushM4ADirectory() {
        if (!this.m4aDirectory.exists()) {
            if (this.m4aDirectory.mkdirs()) return;
        }

        if (this.m4aDirectory.delete()) Bukkit.getConsoleSender().sendMessage(convert("&fThe MP3 Directory was successfully flushed!"));
    }

    /**
     * Flushed the OGG directory so new ones may be written
     */
    protected void flushOGGDirectory() {
        if (!this.oggDirectory.exists()) {
            if (this.oggDirectory.mkdirs()) return;
        }

        if (this.oggDirectory.delete()) Bukkit.getConsoleSender().sendMessage(convert("&fThe OGG Directory was successfully flushed!"));
    }

    /**
     * Checks if the resource pack has been created
     * @return true if the resource pack file exists
     */
    public final boolean hasResourcePackBeenCreated() {
        return this.resourcePackFile.exists();
    }

    /**
     * returns the SHA1 hash of the resource pack file
     * @return a SHA1 byte array
     */
    public byte[] getResourcePatchHash() {
        return this.createSha1(this.resourcePackFile);
    }

    @SneakyThrows
    protected byte[] createSha1(File file) {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = new FileInputStream(file)) {
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
        }
        return digest.digest();
    }
}
