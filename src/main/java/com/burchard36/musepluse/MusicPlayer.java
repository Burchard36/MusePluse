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
     * @since 2.0.5
     */
    public final void setAutoPlayEnabled(final Player player, final boolean isAutoplay) {
        final MusicListener listener = this.getListener(player);
        listener.setAutoPlayEnabled(isAutoplay);
    }

    /**
     * Checks if a player has the AutoPlayers enabled
     * @param player {@link Player}
     * @return true if the AutoPlay is enabled
     * @since 2.0.5
     */
    public final boolean hasAutoPlayEnabled(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.hasAutoPlayEnabled();
    }


    /**
     * Plays the next song in queue for a player
     * <br>
     * if there is no songs left in the queue they get new re-shuffled queue
     * @param player {@link Player}
     * @since 2.0.5
     */
    public void playNextSong(final Player player) {
        final MusicListener listener = this.getListener(player);
        listener.playNext();
    }

    /**
     * Inserts a Song into the Player'sSong Queue, this DOES NOT force player the song, only inserts it as the next song
     * in there list
     * @param player {@link Player} to insert the song into
     * @param songData {@link SongData} of the song to add
     * @since 2.0.5
     */
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
     *
     * @since 2.0.5
     */
    public void stopForAll() {
        this.musicPlayers.forEach((uuid, listener) -> listener.stopCurrentSong());

        this.musicPlayers.clear();
    }
    /**
     * @param player {@link Player} to get current song of from
     * @return SongData of the currently playing song the player is listening to, may be null if they currently have the music player stopped
     * @since 2.0.5
     */
    public final @Nullable SongData getCurrentSong(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.currentSong();
    }

    /**
     * Gets the next song in queue for the player
     * @param player {@link Player}
     * @return {@link SongData}
     * @since 2.0.5
     */
    public final SongData getNextSong(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.nextSong();
    }

    /**
     * Gets a list of songs that the Player has permissions for
     * @param player {@link Player} to get a list of songs from
     * @return A List of songs that the privded Player has permissions to
     * @since 2.0.5
     */
    public List<SongData> getPermissibleSongsFor(final Player player) {
        final MusicListener listener = this.getListener(player);
        return listener.getPermissibleSongs();
    }

    /**
     * Will make the player stop listening to the current radio station, and go back to listening to their own PermissibleSongs (If auto player is enabled)
     * @param player {@link Player}
     * @since 2.0.8
     */
    public void quitRadioStation(final Player player) {
        final MusicListener listener = this.getListener(player);
        listener.removeAttachedRadioStation();
        if (listener.hasAutoPlayEnabled()) listener.playNext(); // play the next song they have in queue to have a seamless transition
    }

    /**
     * Method to allow player's to join an already running radio station. If the target doesnt have a radio station, nothing will happen.
     * @param player a {@link Player}
     * @param targetStation {@link Player} to target
     *
     * @since 2.0.8
     */
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
     * Creates a new radio station for a {@link Player} and sets them as the owner of said channel
     * @param creator THe creating {@link Player}
     * @since 2.0.6
     */
    public final void createRadioStation(final Player creator) {
        final MusicListener listener = this.getListener(creator);

        if (listener.hasAttachedRadioStation()) {
            creator.sendMessage(convert("&cYou already own a radio station!"));
            return;
        }

        final RadioStation station = new RadioStation(creator);

        listener.playNext();

    }

    /**
     * A private helper method to prevent redundant code
     * @param player {@link Player}
     * @return a {@link MusicListener}
     * @since 2.0.6
     */
    protected final MusicListener getListener(final Player player) {
        this.musicPlayers.putIfAbsent(player.getUniqueId(), new MusicListener(player, this.pluginInstance));
        return this.musicPlayers.get(player.getUniqueId());
    }

    /**
     * Clears the provided {@link Player} from the MusePlayer list & removed them from the radio station
     * @param player the {@link Player} to clear
     */
    public final void clear(final Player player) {
        this.stopFor(player);
        this.musicPlayers.remove(player.getUniqueId());
    }

}
