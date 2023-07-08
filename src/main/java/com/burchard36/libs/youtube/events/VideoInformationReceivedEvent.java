package com.burchard36.libs.youtube.events;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import lombok.Getter;

import java.io.File;
import java.util.function.Consumer;

/**
 * Calls when video information is received from YouTube
 *
 * Please note that this is for education purposes only
 *
 * {@link VideoInfo} will be returned in this event, and will typically
 * be used to download the video, however it is possible to get a plethora
 * of other information, may be useful later down the line as a TODO for some gui overhauling for youtube
 *
 * This event used to extend event but it was removed in favor of just a basic data holder class for a method
 */
public class VideoInformationReceivedEvent {
    @Getter
    protected final VideoInfo videoInfo;
    @Getter
    protected final String outputFileName;
    @Getter
    protected final Consumer<File> callback;

    public VideoInformationReceivedEvent(
            final VideoInfo videoInfo,
            final String outputFileName,
            final Consumer<File> onCompletionCallback) {
        this.videoInfo = videoInfo;
        this.outputFileName = outputFileName;
        this.callback = onCompletionCallback;
    }
}
