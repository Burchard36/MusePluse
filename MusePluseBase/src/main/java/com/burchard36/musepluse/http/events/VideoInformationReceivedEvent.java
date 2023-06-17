package com.burchard36.musepluse.http.events;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VideoInformationReceivedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    protected final VideoInfo videoInfo;

    public VideoInformationReceivedEvent(final VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
