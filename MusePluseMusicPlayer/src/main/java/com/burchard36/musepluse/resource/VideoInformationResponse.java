package com.burchard36.musepluse.resource;

import com.burchard36.musepluse.config.SongData;
import com.github.kiulian.downloader.model.videos.VideoInfo;

import java.util.List;

public record VideoInformationResponse(VideoInfo videoInfo, SongData songData) {
}
