package com.burchard36.musepluse.resource;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.resource.writers.OGGFileWriter;
import com.burchard36.musepluse.utils.ZipUtility;
import com.burchard36.musepluse.youtube.YoutubeProcessor;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class ResourcePackEngine extends OGGFileWriter {

    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings pluginSettings;
    protected final YoutubeProcessor youtubeProcessor;
    protected File resourcePackFile;
    @Getter
    protected final AtomicBoolean creatingTexturePack = new AtomicBoolean(false);

    public ResourcePackEngine(final MusePluseMusicPlayer moduleInstance) {
        super(new GsonBuilder().setPrettyPrinting().create());
        this.moduleInstance = moduleInstance;
        this.pluginSettings = this.moduleInstance.getMusePluseSettings();
        this.youtubeProcessor = new YoutubeProcessor(moduleInstance.getPluginInstance());

        this.tryAutoGenerate(false, (v) -> {
            if (this.pluginSettings.isDoItYourselfMode()) return;
            if (this.pluginSettings.isResourcePackServerEnabled() && this.resourcePackExists()) {
                Bukkit.getConsoleSender().sendMessage(convert("&fStarting resource pack server..."));
                ResourcePackServer.startServer(this.moduleInstance);
            }
        });
    }

    /**
     * Song times need to be received from YouTube in order for them to properly play
     */
    public void loadSongTimes(final Consumer<List<VideoInformationResponse>> callback) {
        final AtomicInteger receivedVideoInformation = new AtomicInteger(0);
        final AtomicInteger failedVideoInformation = new AtomicInteger(0);
        final List<VideoInformationResponse> videoInformationList = new ArrayList<>();
        final int totalSongs = this.moduleInstance.getMusicListConfig().getSongDataList().size();
        this.moduleInstance.getMusicListConfig().getSongDataList().forEach((song) -> {
            this.youtubeProcessor.getVideoInformation(song.getYouTubeLink(), (videoInfo) -> {
                if (videoInfo == null) {
                    failedVideoInformation.incrementAndGet();
                    this.moduleInstance.getMusicListConfig().getSongDataList().remove(song);
                    Bukkit.getConsoleSender().sendMessage(convert("&fSkipping &b%s&f because it doesn't have video data!".formatted(song.getYouTubeLink())));
                } else {
                    receivedVideoInformation.incrementAndGet();
                    song.setSeconds(videoInfo.details().lengthSeconds());
                    videoInformationList.add(new VideoInformationResponse(videoInfo, song));
                }

                if ((receivedVideoInformation.get()) + failedVideoInformation.get() == totalSongs) {
                    callback.accept(videoInformationList);
                }
            });
        });
    }

    /**
     * Will attempt to create the resource pack when called
     * @param force if true it will ignore most config options and regenerate an already existing resource pack (if exists)
     * @param onComplete the callback (Is async)
     */
    public void tryAutoGenerate(boolean force, final Consumer<Void> onComplete) {
        /* If the config for auto generation is turned off & force is
         * false don't try to generate the resource pack
         */
        if (!this.pluginSettings.isAutoGenerateResourcePack() && !force) {
            this.loadSongTimes((songList) -> onComplete.accept(null));
            return;
        }
        /* If the resource pack exists and were not forcing
         * don't try to generate the resoruce pack
         */
        if (this.resourcePackExists() && !force) {
            Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f detected previously created resource pack, enjoy! Delete this file if you want to regenerate it!"));
            this.loadSongTimes((songList) -> onComplete.accept(null));
            onComplete.accept(null); // callbacks get put into a different thread pool
            return;
        }

        if (resourcePackExists() && force) {
            Bukkit.getConsoleSender().sendMessage(convert("&fDeleteing old resource_pack! (Resource pack generation is currently being forced!)"));
            if (this.getResourcePackDirectory().delete())
                Bukkit.getConsoleSender().sendMessage(convert("&aSuccess!&f forcefully creating resource pack..."));
        } else Bukkit.getConsoleSender().sendMessage(convert("&fAttempting to generate fresh resource pack!"));

        this.creatingTexturePack.set(true);
        this.loadSongTimes((songList) -> this.createResourcePack(songList, onComplete));
    }

    public final void createResourcePack(final List<VideoInformationResponse> videoInformationList, final Consumer<Void> onComplete) {
        this.mkdirs();
        this.getResourcePackDirectory().delete(); // Just as a fail safe in case this method is directly called (EG in reload command)
        final AtomicInteger downloadedSongs = new AtomicInteger(0);
        /* Loop through all songs */
        for (int x = 0; x < this.moduleInstance.getMusicListConfig().getSongDataList().size(); x++) {
            final VideoInformationResponse response = videoInformationList.get(x);
            this.youtubeProcessor.downloadYouTubeAudioAsOGG(response.videoInfo(), response.songData().getLocalKey(), (file) -> {
                int currentVideosReceived = downloadedSongs.incrementAndGet();

                /* Always recheck total song count just in case one gets removed */
                int totalSongs = this.moduleInstance.getMusicListConfig().getSongDataList().size();
                if (currentVideosReceived == totalSongs) {
                    Bukkit.getConsoleSender().sendMessage(convert("&aSuccess!&f All %s songs have been downloaded!".formatted(totalSongs)));

                    Bukkit.getConsoleSender().sendMessage(convert("&fWriting &bpack.mcmeta&f..."));
                    this.writeMcMeta();
                    Bukkit.getConsoleSender().sendMessage(convert("&fWriting &bsounds.json&f..."));
                    this.writeSoundsJson(this.moduleInstance.getMusicListConfig());
                    Bukkit.getConsoleSender().sendMessage(convert("&fFlashing OGG Files into resource pack..."));
                    this.flashOGGFilesToTempDirectory();
                    Bukkit.getConsoleSender().sendMessage(convert("&fCompressing temp files..."));
                    this.zipResourcePack();
                    Bukkit.getConsoleSender().sendMessage(convert("&fCleaning up leftover files & directories..."));
                    this.cleanUp();

                    Bukkit.getConsoleSender().sendMessage(convert("&aSuccess!&f Your resource pack is located at &b%s&f".formatted(this.resourcePackFile.getPath())));
                    this.creatingTexturePack.set(false);
                    onComplete.accept(null); // callbacks async already
                }
            });
        }
    }

    /**
     * Checks if the /resource-pack directory contains the zip file for spark to host
     * @return true if the .zip file exists
     */
    protected final boolean resourcePackExists() {
        File[] resourcePackDirectoryFiles = this.getResourcePackDirectory().listFiles();
        if (resourcePackDirectoryFiles == null) return false;
        if (resourcePackDirectoryFiles.length <= 0) return false;
        return resourcePackDirectoryFiles[0].getName().endsWith(".zip");
    }

    /**
     * Gets the resource pack from the disk
     * @return null if the file doesnt exist
     */
    public final File resourcePackFileFromDisk() {
        File[] resourcePackDirectoryFiles = this.getResourcePackDirectory().listFiles();
        if (resourcePackDirectoryFiles == null) return null;
        if (resourcePackDirectoryFiles.length <= 0) return null;
        return resourcePackDirectoryFiles[0];
    }

    /**
     * Checks if the resource pack generation task is currently running
     * @return true if the resource pack is still generating
     */
    public final boolean isResourcePackGenerating() {
        return this.creatingTexturePack.get();
    }

    protected void zipResourcePack() {
        final File file = this.resourcePackFileFromDisk();
        if (file != null) {
            if (file.delete()) {
                Bukkit.getConsoleSender().sendMessage(convert("&cDeleted a pre-existing resource pack before zipping"));
            }
        }
        final ZipUtility zipUtility = new ZipUtility();
        File[] tempFiles = new File(this.getResourcePackTempFilesDirectory(), "/assets").listFiles();
        if (tempFiles == null) throw new RuntimeException("It appears the /resource-pack directory was null when calling zipResourcePack, maybe restart your server?");
        try {
            this.resourcePackFile = new File(this.getResourcePackDirectory(), "%s.zip".formatted(UUID.randomUUID().toString()));
            zipUtility.zip(List.of(tempFiles), this.resourcePackFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
