package com.burchard36.musepluse;

import com.burchard36.musepluse.config.MusePluseConfig;
import com.burchard36.musepluse.config.MusePluseSettings;
import com.burchard36.musepluse.config.SongData;
import com.burchard36.musepluse.events.custom.SongEndedEvent;
import com.burchard36.musepluse.exception.MusePluseConfigurationException;
import com.burchard36.musepluse.utils.TaskRunner;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class MusicPlayer {

    protected final MusePluseMusicPlayer moduleInstance;
    /* These songs are what's played when the player joins
       by default. Considering the configuration option is active.
     */
    protected final HashMap<UUID, List<SongData>> queuedPlayerSongs;
    protected final HashMap<UUID, SongData> currentSongsPlaying;
    /* Since players can literally only ever listen to one song at a time
       we can just have one singular map for the tasks that are keeping track
       of songs
     */
    protected final HashMap<UUID, BukkitTask> songTimers;
    @Getter
    protected MusePluseConfig musicConfig;
    @Getter
    protected MusePluseSettings musePluseSettings;

    public MusicPlayer(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
        this.musicConfig = this.moduleInstance.getMusicListConfig();
        this.currentSongsPlaying = new HashMap<>();
        this.songTimers = new HashMap<>();
        this.queuedPlayerSongs = new HashMap<>();
    }

    /**
     * Plays a random set of songs to the player if they have permission to listen to them
     * @param player {@link Player} to player songs to
     */
    public void playRandomQueueFor(final Player player) {
        /* Prevent memory leaks/overwrites of tasks by checking if
           the player
         */
        final UUID playerUUID = player.getUniqueId();
        if (this.queuedPlayerSongs.containsKey(playerUUID)) {
            this.queuedPlayerSongs.remove(playerUUID);
            this.stopFor(player); //NOTE: Yes this removed them from the hashmap of timers to
        }
        final List<SongData> songs = this.getSongQueueFor(player);
        this.queuedPlayerSongs.put(playerUUID, songs);
        this.playNextSong(player);
    }

    /**
     * Plays the next song in queue for a player
     * <br>
     * if there is no songs left in the queue they get restarted
     * @param player {@link Player}
     */
    public void playNextSong(final Player player) {
        this.stopFor(player);
        final List<SongData> songs = this.queuedPlayerSongs.get(player.getUniqueId());
        if (songs == null) {
            this.playRandomQueueFor(player);
            return;
        }

        if (songs.size() <= 0) {
            this.playRandomQueueFor(player);
            return;
        }

        final SongData songData = songs.get(0);
        songs.remove(0);
        this.queuedPlayerSongs.replace(player.getUniqueId(), songs);
        this.playSongTo(player, songData);
    }

    public void insertSongIntoQueue(final Player player, final SongData songData) {
        List<SongData> songs = this.queuedPlayerSongs.get(player.getUniqueId());
        if (songs.size() == 0) {
            songs = List.of(songData);
        } else songs.add(0, songData);
        this.queuedPlayerSongs.replace(player.getUniqueId(), songs);
        /* If the player doesn't have a song playing, play the queue */
        if (!this.hasSongPlaying(player)) this.playNextSong(player);
    }

    public void playSongTo(final Player player, final SongData songData) {
        this.stopFor(player);

        player.playSound(player.getLocation(), "musepluse:" + songData.getLocalKey(), SoundCategory.VOICE, 1.0F, 1.0f);
        this.startTimerFor(player, songData.getTotalTicks());
        this.setSongData(player, songData);
        /* Messaging */
        if (!this.musePluseSettings.isSendNextSongMessage()) return;
        if (this.musePluseSettings.getNextSongMessages() == null) {
            if (this.musePluseSettings.getNextSongMessage() == null) throw new MusePluseConfigurationException("\"Notifications.SongStarted.ActionBar\" was null during runtime! WHAT THE FUCK!!!!??????!!!!!????!!!");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(convert(this.musePluseSettings.getNextSongMessage())));
        } else this.musePluseSettings.getNextSongMessages().forEach(message -> {
                player.sendMessage(convert(message));
            });
    }

    public void stopFor(final Player player) {
        if (!this.hasSongPlaying(player)) return;
        player.stopSound(SoundCategory.VOICE);
        this.currentSongsPlaying.remove(player.getUniqueId());
        this.stopTimerFor(player);
    }

    protected void startTimerFor(final Player player, final long length) {
        final BukkitTask timer = TaskRunner.runSyncTaskLater(() -> {
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
     * Sets what song a player is listening to, this does NOT make them listen to the
     * actual song
     * @param player {@link Player} to set
     * @param data {@link SongData} to set
     */
    protected final void setSongData(final @NonNull Player player, final SongData data) {
        this.currentSongsPlaying.put(player.getUniqueId(), data);
    }

    /**
     * Gets a list of songs that the player is allowed to listen to
     * <br>
     * this means that each song has its permission checked for against the players
     * @param player {@link Player} to create a queue for
     * @return possibly empty list if the player has absolutely no permissions for any songs
     */
    public List<SongData> getSongQueueFor(final @NonNull Player player) {
        final List<SongData> songs = this.getPermissibleSongsFor(player);
        Collections.shuffle(songs);
        return songs;
    }

    /**
     * returns a list of songs that the given player has permissions to, if said song has a permissions set
     * @param player {@link Player} to check against
     * @return List of songs the player has access to
     */
    public List<SongData> getPermissibleSongsFor(final @NonNull Player player) {
        final List<SongData> songs = new ArrayList<>();
        this.musicConfig.getSongDataList().forEach(song -> {
            if (song.getPermission() != null && player.hasPermission(song.getPermission()))
                songs.add(song);
        });
        return songs;
    }

}
