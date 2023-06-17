package com.burchard36.musepluse.http;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.http.events.VideoInformationReceivedEvent;
import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executors;

public abstract class YoutubeGetRequester implements Listener {

    protected final Config youtubeConfiguration;
    protected final YoutubeDownloader youtubeRequester;
    protected final File outputDirectory;

    public YoutubeGetRequester(final JavaPlugin plugin) {
        MusePlusePlugin.registerEvent(this);

        this.outputDirectory = new File(plugin.getDataFolder(), "/")
        this.youtubeConfiguration = new Config.Builder()
                .executorService(Executors.newCachedThreadPool())
                .header("Accept-language", "en-US,en;")
                .proxy("192.168.0.1", 2005)
                .build();
        this.youtubeRequester = new YoutubeDownloader(this.youtubeConfiguration);
    }

    @EventHandler
    public void onInformationReceived(final VideoInformationReceivedEvent receivedEvent) {
        final VideoInfo videoInfo = receivedEvent.getVideoInfo();
    }

    public void getVideoInformation(final String youtubeLink) {

    }

    public void downloadMP3FromYoutube(final String youtubeLink) {
        RequestVideoFileDownload request = new RequestVideoFileDownload()
                // optional params
                .saveTo(outputDir) // by default "videos" directory
                .renameTo("video") // by default file name will be same as video title on youtube
                .overwriteIfExists(true); // if false and file with such name already exits sufix will be added video(1).mp4
    }

    protected final String getVideoId(final String youtubeLink) {
        final String[] splitLink = youtubeLink.split("=");
        if (splitLink.length != 2) throw new RuntimeException("Youtube link for video is invalid: %s".formatted(youtubeLink));
        return splitLink[1];
    }

}
