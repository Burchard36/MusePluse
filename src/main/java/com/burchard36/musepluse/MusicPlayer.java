package com.burchard36.musepluse;

import com.burchard36.libs.config.MusePluseConfig;
import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.libs.config.SongData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class MusicPlayer {

    protected final MusePlusePlugin pluginInstance;
    @Getter
    protected MusePluseConfig musicConfig;
    @Getter
    protected MusePluseSettings musePluseSettings;
    protected HashMap<UUID, MusicListener> musicPlayers;

    public MusicPlayer(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.musePluseSettings = this.pluginInstance.getMusePluseSettings();
        this.musicConfig = this.pluginInstance.getMusicListConfig();
        this.musicPlayers = new HashMap<>();
    }

    /**
     * Enables Auto-Play for the player
     * @param player a {@link Player}
     * @param isAutoplay true if the player should auto player
     */
    public final void setAutoPlayEnabled(final Player player, final boolean isAutoplay) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        listener.setAutoPlayEnabled(isAutoplay);
    }

    public final boolean hasAutoPlayEnabled(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        return listener.hasAutoPlayEnabled();
    }


    /**
     * Plays the next song in queue for a player
     * <br>
     * if there is no songs left in the queue they get new re-shuffled queue
     * @param player {@link Player}
     */
    public void playNextSong(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        listener.playNext();
    }

    public void insertSongIntoQueue(final Player player, final SongData songData) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        listener.insertSongIntoQueue(songData);
    }

    /**
     * Plays a specific song to the player
     * @param player {@link Player} to play the song to
     * @param songData {@link SongData} to play to the player
     */
    public void playSongTo(final Player player, final SongData songData) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        listener.playSpecificSong(songData);
    }

    public void stopFor(final Player player) {
        new ArrayList<Player>(Bukkit.getOnlinePlayers());
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        listener.stopCurrentSong();
    }

    /**
     * Stops playing music to all players on the server and clears the
     * currently playing song list
     */
    public void stopForAll() {
        this.musicPlayers.forEach((uuid, listener) -> listener.stopCurrentSong());

        this.musicPlayers.clear();
    }
    /**
     * @param player {@link Player} to get current song of from
     * @return SongData of the currently playing song the player is listening to, may be null if they currently have the music player stopped
     */
    public final @Nullable SongData getCurrentSong(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        return listener.currentSong();
    }

    /**
     * Gets the next song in queue for the player
     * @param player {@link Player}
     * @return {@link SongData}
     */
    public final SongData getNextSong(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        return listener.nextSong();
    }

    public List<SongData> getPermissibleSongsFor(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        final MusicListener listener = this.musicPlayers.get(player.getUniqueId());
        return listener.getPermissibleSongs();
    }

    public final void clear(final Player player) {
        this.stopFor(player);
        this.musicPlayers.remove(player.getUniqueId());
    }

}
