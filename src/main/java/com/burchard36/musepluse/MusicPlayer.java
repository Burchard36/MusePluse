package com.burchard36.musepluse;

import com.burchard36.libs.config.MusePluseConfig;
import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.libs.config.SongData;
import com.burchard36.musepluse.radio.RadioStation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

import static com.burchard36.libs.utils.StringUtils.convert;


public class MusicPlayer {

    protected final MusePlusePlugin pluginInstance;
    @Getter
    protected final MusePluseConfig musicConfig;
    @Getter
    protected final MusePluseSettings musePluseSettings;
    protected final HashMap<UUID, MusicListener> musicPlayers;
    protected final HashMap<UUID, RadioStation> radioStations;

    public MusicPlayer(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.musePluseSettings = this.pluginInstance.getMusePluseSettings();
        this.musicConfig = this.pluginInstance.getMusicListConfig();
        this.musicPlayers = new HashMap<>();
        this.radioStations = new HashMap<>();
    }

    /**
     * Enables Auto-Play for the player
     * @param player a {@link Player}
     * @param isAutoplay true if the player should auto player
     */
    public final void setAutoPlayEnabled(final Player player, final boolean isAutoplay) {
        final MusicListener listener = this.getListener(player);
        listener.setAutoPlayEnabled(isAutoplay);
    }

    public final boolean hasAutoPlayEnabled(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.hasAutoPlayEnabled();
    }


    /**
     * Plays the next song in queue for a player
     * <br>
     * if there is no songs left in the queue they get new re-shuffled queue
     * @param player {@link Player}
     */
    public void playNextSong(final Player player) {
        final MusicListener listener = this.getListener(player);
        listener.playNext();
    }

    public void insertSongIntoQueue(final Player player, final SongData songData) {
        final MusicListener listener = this.getListener(player);
        listener.insertSongIntoQueue(songData);
    }

    /**
     * Plays a specific song to the player
     * @param player {@link Player} to play the song to
     * @param songData {@link SongData} to play to the player
     */
    public void playSongTo(final Player player, final SongData songData) {
        final MusicListener listener = this.getListener(player);
        listener.playSpecificSong(songData);
    }

    public void stopFor(final Player player) {
        new ArrayList<Player>(Bukkit.getOnlinePlayers());
        final MusicListener listener = this.getListener(player);
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
        final MusicListener listener = this.getListener(player);
        return listener.currentSong();
    }

    /**
     * Gets the next song in queue for the player
     * @param player {@link Player}
     * @return {@link SongData}
     */
    public final SongData getNextSong(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.nextSong();
    }

    public List<SongData> getPermissibleSongsFor(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.getPermissibleSongs();
    }

    /**
     * Will make the player stop listening to the current radio station, and go back to listening to their own PermissibleSongs (If auto player is enabled)
     * @param player {@link Player}
     */
    public void quitRadioStation(final Player player) {
        final MusicListener listener = this.getListener(player);
        listener.removeAttachedRadioStation();
        if (listener.hasAutoPlayEnabled()) listener.playNext(); // play the next song they have in queue to have a seamless transition
    }

    public void joinRadioStation(final Player player, final Player targetStation) {
        final MusicListener listener = this.getListener(player);
        final MusicListener target = this.getListener(targetStation);

        if (!target.hasAttachedRadioStation()) {
            player.sendMessage(convert("&cThis player isn't listening to a radio station!"));
            return;
        }

        //TODO Keep going
    }

    /**
     * A private helper method to prevent redundant code
     * @param player {@link Player}
     * @return a {@link MusicListener}
     */
    protected final MusicListener getListener(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        return this.musicPlayers.get(player.getUniqueId());
    }


    public final void clear(final Player player) {
        this.stopFor(player);
        this.musicPlayers.remove(player.getUniqueId());
    }

}
