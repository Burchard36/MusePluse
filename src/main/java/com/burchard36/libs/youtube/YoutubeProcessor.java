package com.burchard36.libs.youtube;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.libs.ffmpeg.FFExecutor;
import com.burchard36.libs.ffmpeg.FFMPEGDownloader;
import com.burchard36.libs.ffmpeg.FFTask;
import com.burchard36.libs.ffmpeg.events.FFMPEGInitializedEvent;
import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static com.burchard36.musepluse.MusePlusePlugin.*;
import static com.burchard36.libs.utils.StringUtils.convert;

public class YoutubeProcessor implements Listener {

    protected final Config youtubeConfiguration;
    protected final YoutubeDownloader youtubeRequester;
    protected final FFMPEGDownloader ffmpegDownloader;
    protected FFExecutor ffExecutor;
    /* If FFMPEG Is not installed, download requests will be put into a List until it */
    protected final List<FFTask> queuedOGGConversions;
    protected final File mediaOutput;
    protected final File oggOutput;
    protected final File m4aOutput;
    protected File ffmpegFile;

    public YoutubeProcessor(final MusePlusePlugin pluginInstance) {
        MusePlusePlugin.registerEvent(this);
        this.queuedOGGConversions = new ArrayList<>();
        this.youtubeConfiguration = new Config.Builder()
                .executorService((ExecutorService) MAIN_THREAD_POOL)
                .maxRetries(0)
                .build();
        this.youtubeRequester = new YoutubeDownloader(this.youtubeConfiguration);
        this.ffmpegDownloader = pluginInstance.getFfmpegDownloader();
        this.mediaOutput = new File(pluginInstance.getDataFolder(), "/media");
        this.oggOutput = new File(this.mediaOutput, "/ogg");
        this.m4aOutput = new File(this.mediaOutput, "/m4a");

        if (this.ffmpegDownloader.ffmpegIsInstalled()) this.initializeFFMPEG();
    }

    /**
     * Downloads a YouTube video as a m4a and converts it to OGG
     *
     * This method WILL wait until FFMPEG initializes
     *
     * @param videoInfo {@link VideoInfo} the information to use, can be gotten from: {@link YoutubeProcessor#getVideoInformation(String, Consumer)}
     * @param newFileName {@link String} the new file name to save the file may or may not include the .ogg extension
     * @param callback {@link Consumer} the callback used when this video & conversion is finished
     */
    public final void downloadYouTubeAudioAsOGG(final VideoInfo videoInfo, String newFileName, final Consumer<File> callback) {
        /* For use in Async */
        final String finalNewFileName = newFileName;
        // TODO: There is a videoInfo.details().downloadable() method, maybe this can help solve some issues with people needing to refresh theyre MP Plugin generation

        final RequestVideoFileDownload downloadRequest = new RequestVideoFileDownload(videoInfo.bestAudioFormat())
                .callback(new YoutubeProgressCallback<>() {
                    @Override
                    public void onDownloading(int progress) {
                        //Bukkit.getConsoleSender().sendMessage("Song downloading... %s Progress: %s".formatted(finalNewFileName, progress));
                    }

                    @Override
                    public void onFinished(File data) {
                        Bukkit.getConsoleSender().sendMessage(convert("&fYouTube video &b%s&f has finished downloading!").formatted(finalNewFileName));
                        Bukkit.getConsoleSender().sendMessage(convert("&fAttempting to convert &b%s&f to OGG file format...").formatted(data.getPath()));
                        final File outputFile = new File(oggOutput.getPath() + "/%s.ogg".formatted(finalNewFileName));
                        if (ffmpegDownloader.isDownloading()) {
                            Bukkit.getConsoleSender().sendMessage(convert("&fPausing conversion of file &b%s&f as it appears FFMPEG is not initializated! (is it still installing?\nThis task will automatically resume! This is not an error!"));
                            queuedOGGConversions.add(new FFTask(data, outputFile, callback));
                        } else {
                            Bukkit.getLogger().info("Running");
                            Bukkit.getConsoleSender().sendMessage("Running with console sender....");
                            ffExecutor.convertToOgg(data, outputFile, () -> {
                                Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully &fconverted file &b%s&f! Cleaning up...").formatted(finalNewFileName));
                                //if (data.delete())
                                    //Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f cleaned up file &b%s&f").formatted(finalNewFileName));
                                callback.accept(data); // use a different executor for callbacks
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Bukkit.getConsoleSender().sendMessage(convert("&cERROR WITH %s".formatted(finalNewFileName)));
                        throwable.printStackTrace();
                        callback.accept(null);
                    }


                })
                .saveTo(this.m4aOutput)
                .renameTo(finalNewFileName)
                .async();

        this.youtubeRequester.downloadVideoFile(downloadRequest);
    }

    /**
     * Gets information about a YouTube video, this can range from the video's length, video name, video/audio formats
     * and much much more
     * @param youtubeLink a full YouTube video link
     * @param callback A callback to accept the {@link VideoInfo} that was requested
     */
    public final void getVideoInformation(final String youtubeLink, final Consumer<VideoInfo> callback) {
        final RequestVideoInfo videoInfoRequest = new RequestVideoInfo(this.getVideoId(youtubeLink))
                .callback(new YoutubeCallback<>() {
                    @Override
                    public void onFinished(VideoInfo videoInfo) {
                        Bukkit.getConsoleSender().sendMessage(convert("&fVideo information for song &b%s&f received!").formatted(youtubeLink));
                        callback.accept(videoInfo); // callbacks happen off the main thread executor
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Bukkit.getConsoleSender().sendMessage(convert("&cERROR WITH &b%s".formatted(youtubeLink)));
                        Bukkit.getConsoleSender().sendMessage(convert("&cThe plugin will attempt to skips this song and continue loading!"));
                        Bukkit.getConsoleSender().sendMessage(convert("&cIf you encounter any issues, please try removing this song from songs.yml first!"));
                        callback.accept(null);
                    }
                })
                .async();

        this.youtubeRequester.getVideoInfo(videoInfoRequest);
    }


    /**
     * Splits a youtube link into two parts as show below
     *
     * https://www.youtube.com/watch?v lSALzr_vs_M
     *
     * The second argument is what is returned (AKA Video ID)
     * @param youtubeLink A Full youtube link
     * @return The YouTube video ID
     */
    private String getVideoId(final String youtubeLink) {
        final String[] splitLink = youtubeLink.split("=");
        if (splitLink.length != 2) throw new RuntimeException("Youtube link for video is invalid: %s".formatted(youtubeLink));
        return splitLink[1];
    }

    /**
     * Deletes all the files inside /media
     */
    public final void cleanupOutputs() {
        if (this.mediaOutput.delete()) Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f deleted /media outputs!"));
    }

    @EventHandler
    public void onFFMPEGInitialization(final FFMPEGInitializedEvent initializedEvent) {
        this.initializeFFMPEG();
    }

    protected void initializeFFMPEG() {
        if (IS_WINDOWS) {
            this.ffmpegFile = new File(MusePlusePlugin.INSTANCE.getDataFolder().getPath() + "\\ffmpeg\\bin\\ffmpeg.exe");
        } else { // Only support windows and linux, this will likely throw errors on apple and solaris systems but fuck em for now
            final File ffmpegForLinux = new File(MusePlusePlugin.INSTANCE.getDataFolder().getPath() + "/ffmpeg/ffmpeg");
            final File ffprobeForLinux = new File(MusePlusePlugin.INSTANCE.getDataFolder().getPath() + "/ffmpeg/ffprobe");
            ffmpegForLinux.setExecutable(true, false);
            ffprobeForLinux.setExecutable(true, false);
            this.ffmpegFile = ffmpegForLinux;
        }

        this.ffExecutor = new FFExecutor(this.ffmpegFile);
        Bukkit.getLogger().info(convert("&cFFMPEG Instance successfully set"));
        this.queuedOGGConversions.forEach((entry) -> {
            Bukkit.getConsoleSender().sendMessage(convert("Resuming OGG File conversion for &b%s&f".formatted(entry.to().getPath())));
            ffExecutor.convertToOgg(entry.from(), entry.to(), () -> {
                Bukkit.getConsoleSender().sendMessage(convert("&fSuccessfully converted file &b%s&f! Cleaning up...").formatted(entry.to().getPath()));
                entry.callback().accept(entry.to());
            });
        });
    }
}
