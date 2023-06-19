package com.burchard36.musepluse.youtube;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.ffmpeg.events.FFMPEGInitializedEvent;
import com.burchard36.musepluse.utils.TaskRunner;
import com.burchard36.musepluse.youtube.events.VideoDownloadedEvent;
import com.burchard36.musepluse.youtube.events.VideoInformationReceivedEvent;
import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.request.RequestVideoStreamDownload;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.burchard36.musepluse.MusePlusePlugin.IS_WINDOWS;
import static com.burchard36.musepluse.utils.StringUtils.convert;

/**
 * The async implementation for downloading OGG Files from YouTube
 *
 * Please note that this class is for informational purpose only
 *
 * If you were expecting docs explaining this code, then no
 */

public abstract class YoutubeGetRequester implements Listener {

    protected final Config youtubeConfiguration;
    protected final YoutubeDownloader youtubeRequester;
    protected final File outputDirectory;
    protected final MusePlusePlugin plugin;
    protected FFmpeg ffmpeg;
    protected FFprobe ffprobe;
    protected FFmpegExecutor fFmpegExecutor;
    /* If FFMPEG Is not installed, download requests will be put into a List until it */
    protected final List<PausedOGGConversion> queuedOGGConversions;
    protected boolean ffmpegInitialized = false;

    public YoutubeGetRequester(final MusePlusePlugin plugin) {
        MusePlusePlugin.registerEvent(this);
        this.plugin = plugin;
        this.queuedOGGConversions = new ArrayList<>();
        this.outputDirectory = new File(plugin.getDataFolder(), "/media");
        this.youtubeConfiguration = new Config.Builder()
                .executorService(Executors.newCachedThreadPool())
                .maxRetries(0)
                .build();
        this.youtubeRequester = new YoutubeDownloader(this.youtubeConfiguration);
    }

    @EventHandler
    @SneakyThrows
    public void onFFMPEGInitialization(final FFMPEGInitializedEvent initializedEvent) {
        if (IS_WINDOWS) {
            this.ffmpeg = new FFmpeg(plugin.getDataFolder().getPath() + "\\ffmpeg\\bin\\ffmpeg.exe");
            this.ffprobe = new FFprobe(plugin.getDataFolder().getPath() + "\\ffmpeg\\bin\\ffprobe.exe");
        } else { // Only support windows and linux, this will likely throw errors on apple and solaris systems but fuck em for now
            this.ffmpeg = new FFmpeg(plugin.getDataFolder().getPath() + "\\ffmpeg\\ffmpeg");
            this.ffprobe = new FFprobe(plugin.getDataFolder().getPath() + "\\ffmpeg\\ffprobe");
        }
        this.fFmpegExecutor = new FFmpegExecutor(this.ffmpeg, this.ffprobe);
        this.ffmpegInitialized = true;
        CompletableFuture.runAsync(() -> {
            this.queuedOGGConversions.forEach((entry) -> {
                Bukkit.getConsoleSender().sendMessage(convert("Resuming OGG File conversion for &b%s&f".formatted(entry.convertedFile().getPath())));
                this.fFmpegExecutor.createJob(entry.builder()).run();
                Bukkit.getConsoleSender().sendMessage(convert("&fSuccessfully converted file &b%s&f! Cleaning up...").formatted(entry.convertedFile().getPath()));
                entry.callback().accept(entry.convertedFile());
            });
        });
    }

    private void onDownloadFinished(final VideoDownloadedEvent downloadedEvent) {
        final File file = downloadedEvent.getFile();

        Bukkit.getConsoleSender().sendMessage(convert("Attempting to convert &b%s&f to OGG file format...").formatted(file.getPath()));
        FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
                .setInput(file.getPath())
                .overrideOutputFiles(true)
                .addOutput(this.outputDirectory.getPath() + "\\ogg\\" + downloadedEvent.getOutputFileName() + ".ogg")
                .setFormat("ogg")
                .done();
        if (this.plugin.getFfmpegDownloader().isDownloading()) {
            Bukkit.getConsoleSender().sendMessage(convert("&fPausing conversion of file &b%s&f as it appears FFMPEG is not initializated! (is it still installing?\nThis task will automatically resume! This is not an error!"));
            this.queuedOGGConversions.add(new PausedOGGConversion(ffmpegBuilder, file, downloadedEvent.getCallback()));
            return;
        }

        fFmpegExecutor.createJob(ffmpegBuilder).run();
        Bukkit.getConsoleSender().sendMessage(convert("&fSuccessfully converted file &b%s&f! Cleaning up...").formatted(file.getPath()));
        downloadedEvent.getCallback().accept(file);
    }


    @SneakyThrows
    private void onInformationReceived(final VideoInformationReceivedEvent receivedEvent) {
        final VideoInfo videoInfo = receivedEvent.getVideoInfo();
        Bukkit.getConsoleSender().sendMessage(convert("Information for video &b%s&f received! Downloading Audio... This song is %s seconds long").formatted(receivedEvent.getOutputFileName(), videoInfo.details().lengthSeconds()));

        final File outputFile = new File(this.outputDirectory, "/m4a/" + receivedEvent.getOutputFileName() + ".m4a");
        final File outputDirectory = new File(this.outputDirectory, "/m4a");
        outputDirectory.mkdirs();
        Throwable throwable = this.youtubeRequester.downloadVideoFile(
                new RequestVideoFileDownload(videoInfo.bestAudioFormat())
                        .callback(new YoutubeProgressCallback<>() {
                            @Override
                            public void onDownloading(int progress) {
                                //Bukkit.getConsoleSender()
                                //.sendMessage(convert("&b%s%%&f for video &b%s").formatted(progress, receivedEvent.getOutputFileName()));
                            }

                            @Override
                            public void onFinished(File data) {
                                Bukkit.getConsoleSender().sendMessage(convert("&fYouTube video &b%s&f has finished downloading!").formatted(receivedEvent.getOutputFileName()));
                                onDownloadFinished(new VideoDownloadedEvent(outputFile, receivedEvent.getOutputFileName(), receivedEvent.getCallback()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throw new RuntimeException(throwable);
                            }
                        })
                        .saveTo(new File(this.outputDirectory, "/m4a"))
                        .renameTo(receivedEvent.getOutputFileName())
                        .async()
        ).error();

        if (throwable != null) throwable.printStackTrace();

    }

    /**
     * Asynchronously downloads a YouTube Video as an MP3 file
     *
     * Do note, that this should only be used for information purposes and should not be used in any commercial manner
     * this method will fire a series of chain reaction events that result in a mp3 file being generated in the /media director
     * of this plugin's data folder
     * @param youtubeLink a full youtube link
     */
    public void downloadYoutubeVideo(final String youtubeLink, final String renameFileTo, final Consumer<File> callback) {
        Bukkit.getConsoleSender().sendMessage(convert("Attempting to download youtube video &b%s&f this task will be async.").formatted(youtubeLink));
        this.youtubeRequester.getVideoInfo(
                new RequestVideoInfo(this.getVideoId(youtubeLink))
                        .callback(new YoutubeCallback<>() {
                            @Override
                            public void onFinished(VideoInfo videoInfo) {
                                Bukkit.getConsoleSender().sendMessage(convert("&fVideo information for song &b%s&f received!").formatted(youtubeLink));
                                onInformationReceived(new VideoInformationReceivedEvent(videoInfo, renameFileTo, callback));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        })
                        .async()
        );
    }

    private String getVideoId(final String youtubeLink) {
        final String[] splitLink = youtubeLink.split("=");
        if (splitLink.length != 2) throw new RuntimeException("Youtube link for video is invalid: %s".formatted(youtubeLink));
        return splitLink[1];
    }

}
