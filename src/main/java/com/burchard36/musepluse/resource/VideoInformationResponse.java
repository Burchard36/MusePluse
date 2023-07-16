package com.burchard36.musepluse.resource;

import com.burchard36.libs.config.SongData;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A class used for passing VideoInformation between tasks, threads & classes
 * @param videoInfo the {@link VideoInfo} provided
 * @param songData the {@link SongData} of the song
 */
public record VideoInformationResponse(@NonNull VideoInfo videoInfo, @NonNull SongData songData) {
}
