package com.burchard36.musepluse.youtube.events;

import lombok.Getter;

import java.io.File;
import java.util.function.Consumer;


/**
 * Calls when a video gets downloaded from YouTube
 *
 * Please note that this should only be used for informational purposes only
 *
 * {@link File} the file that was created, this will be a MP3 file
 * and should typically be ignored and only used for OGG file conversion with FFMPEG
 *
 * {@link Consumer<File>} this is the callback that was supplied from the origin method call of {@link com.burchard36.musepluse.youtube.YoutubeGetRequester#downloadYoutubeVideo(String, String, Consumer)}
 *
 * If i srsly have to explain the string you shouldn't be viewing this file.
 *
 * This event used to extend event but it was removed in favor of just a basic data holder class for a method
 */
public class VideoDownloadedEvent {
    @Getter
    protected final File file;
    @Getter
    protected final String outputFileName;
    @Getter
    protected final Consumer<File> callback;


    public VideoDownloadedEvent(
            final File file,
            String outputFileName,
            final Consumer<File> onCompletionCallback) {
        this.file = file;
        this.outputFileName = outputFileName;
        this.callback = onCompletionCallback;
    }
}
