package com.burchard36.cloudlite;

import com.burchard36.cloudlite.config.MusicListConfig;
import com.burchard36.cloudlite.config.SongData;
import com.burchard36.cloudlite.events.custom.SongEndedEvent;
import com.burchard36.cloudlite.utils.TaskRunner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;

import static com.burchard36.cloudlite.utils.StringUtils.convert;
import static com.burchard36.cloudlite.utils.StringUtils.sendCenteredMessage;

/**
 * This module handles playing music to the player
 *
 * It will NEVER handle anything other than this.
 */
public class MusicPlayer {

    protected final Random random = new Random();
    protected final CloudLiteMusicPlayer moduleInstance;
    protected final HashMap<UUID, SongData> currentSongsPlaying;
    protected final HashMap<UUID, SongData> lastSongsPlayed;
    protected final HashMap<UUID, BukkitTask> songTimers;
    protected final List<UUID> currentlyPlaying;
    @Getter
    @Setter
    protected MusicListConfig musicConfig;

    public MusicPlayer(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musicConfig = this.moduleInstance.getMusicListConfig();
        this.currentSongsPlaying = new HashMap<>();
        this.lastSongsPlayed = new HashMap<>();
        this.songTimers = new HashMap<>();
        this.currentlyPlaying = new ArrayList<>();
    }

    public void forcePlaySongFor(final Player player, final SongData songData) {
        this.stopFor(player);

        Bukkit.getLogger().info(convert("Playing song: %s to player: %s".formatted(songData.getSongDisplayName(), player.getName())));
        sendCenteredMessage(player, "&fNow Playing:&b %s".formatted(songData.getSongDisplayName()));
        sendCenteredMessage(player, "&fby:&b %s".formatted(songData.getSongDisplayName()));
        sendCenteredMessage(player, "&7(( &b&oMute songs by turning &eVoices&b&o to &eOFF&7 ))");
        sendCenteredMessage(player, "&7(( &b&oFor the best experience, turn your &eMusic&b&o volume to &eOFF&7 ))");
        sendCenteredMessage(player, "&fDon't like this song (or don't hear anything)? &b/skipsong");
        player.playSound(player.getLocation(), "simplecore:" + songData.getLocalKey(), SoundCategory.VOICE, 1.0F, 1.0f);
        this.setSongData(player, songData);
        this.startTimerFor(player, songData.getTotalTicks());
    }

    public void playFor(final Player player) {
        if (this.hasSongPlaying(player)) {
            Bukkit.getLogger().info(convert("Player %s already has a song playing! Stopping...".formatted(player.getName())));
            this.stopFor(player);
            this.playFor(player);
            return;
        }

        final SongData randomSong = this.getRandomSong();
        if (this.wasLastSong(this.getLast(player), randomSong)) {
            Bukkit.getLogger().info(convert("&bRe-attempting to get new song for player: " + player.getName()));
            this.stopFor(player);
            this.playFor(player); // don't play the same song twice
            return;
        }

        Bukkit.getLogger().info(convert("Playing song: %s to player: %s".formatted(randomSong.getSongDisplayName(), player.getName())));
        sendCenteredMessage(player, "&fNow Playing:&b %s".formatted(randomSong.getSongDisplayName()));
        sendCenteredMessage(player, "&fby:&b %s".formatted(randomSong.getSongDisplayName()));
        sendCenteredMessage(player, "&7(( &b&oMute songs by turning &eVoices&b&o to &eOFF&7 ))");
        sendCenteredMessage(player, "&7(( &b&oFor the best experience, turn your &eMusic&b&o volume to &eOFF&7 ))");
        sendCenteredMessage(player, "&fDon't like this song (or don't hear anything)? &b/skipsong");
        player.playSound(player.getLocation(), "simplecore:" + randomSong.getLocalKey(), SoundCategory.VOICE, 1.0F, 1.0f);
        this.setSongData(player, randomSong);
        this.startTimerFor(player, randomSong.getTotalTicks());
    }

    public void stopFor(final Player player) {
        if (!this.hasSongPlaying(player)) return;
        player.stopSound(SoundCategory.VOICE);
        this.stopTimerFor(player);
    }

    protected SongData getRandomSong() {
        int maxBounds = this.musicConfig.getSongDataList().size();
        int index = random.nextInt(maxBounds);
        final SongData songData = this.musicConfig.getSongDataList().get(index);
        assert songData != null;
        return songData;
    }

    protected void startTimerFor(final Player player, final long length) {
        final BukkitTask timer = TaskRunner.runSyncTaskLater(() -> {
            Bukkit.getServer().getLogger().info(convert("Song ended for %s, dispatching SongEndedEvent".formatted(player.getName())));
            Bukkit.getPluginManager().callEvent(new SongEndedEvent(player));
        }, length);
        this.songTimers.putIfAbsent(player.getUniqueId(), timer);
    }

    /**
     * Stops a song timer for a given player, this does NOT stop the player
     * from listening to the song. Use {@link MusicPlayer#stopFor(Player)}
     * @param player {@link Player} timer to stop
     */
    protected void stopTimerFor(final Player player) {
        final BukkitTask task = this.songTimers.get(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        this.songTimers.remove(player.getUniqueId());
    }

    /**
     * @param player {@link Player} to get current song of from
     * @return SongData of the currently playing song the player is listening to
     */
    public final @Nullable SongData getCurrentSong(final Player player) {
        return this.currentSongsPlaying.get(player.getUniqueId());
    }

    /**
     * Checks if a player has a song player
     * @param player {@link Player} to check
     * @return true if the player is listening to a song
     */
    public final boolean hasSongPlaying(final Player player) {
        return this.songTimers.get(player.getUniqueId()) != null;
    }

    /**
     * Compared two songs and checks if they are similar
     * @param oldData Old {@link SongData} to check
     * @param newData New {@link SongData} to check
     * @return true if the two data's are similar
     */
    public final boolean wasLastSong(final @Nullable SongData oldData, final @NonNull SongData newData) {
        if (oldData == null) return false;
        return oldData.getLocalKey().equals(newData.getLocalKey());
    }

    /**
     * Gets the last song the player was listening to
     * @param player {@link Player} to check against
     * @return {@link SongData} of the last song listened to, may be null
     */
    public final @Nullable SongData getLast(final Player player) {
        return this.lastSongsPlayed.get(player.getUniqueId());
    }

    /**
     * Sets what song a player is listening to, this does NOT make them listen to the
     * actual song, for that use {@link MusicPlayer#playFor(Player)}
     * @param player {@link Player} to set
     * @param data {@link SongData} to set
     */
    protected final void setSongData(final @NonNull Player player, final SongData data) {
        final SongData oldData = this.currentSongsPlaying.get(player.getUniqueId());
        if (oldData != null) this.lastSongsPlayed.put(player.getUniqueId(), oldData);
        this.currentSongsPlaying.put(player.getUniqueId(), data);
    }

}
