package com.burchard36.musepluse;


import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.libs.config.SongData;
import com.burchard36.libs.utils.TaskRunner;
import com.burchard36.musepluse.exception.MusePluseConfigurationException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.burchard36.libs.utils.StringUtils.convert;

public class MusicListener {

    protected final Player player;
    protected final PersistentDataContainer dataContainer;
    protected final MusicPlayer musicPlayerInstance;
    protected final MusePlusePlugin pluginInstance;
    protected final NamespacedKey autoPlayKey;
    protected final MusePluseSettings moduleSettings;
    protected AtomicBoolean isSwitchingSongs;
    protected AtomicReference<SongData> currentlyPlayingSong;
    protected BukkitTask musicTimer;
    protected List<SongData> permissibleSongs;
    protected List<SongData> queuedSongs;
    protected List<SongData> favoriteSongs;

    public MusicListener(final Player player, final MusePlusePlugin pluginInstance) {
        this.player = player;
        this.pluginInstance = pluginInstance;
        this.autoPlayKey = new NamespacedKey(this.pluginInstance.getPluginInstance(), "muse_pluse_auto_play");
        this.dataContainer = this.player.getPersistentDataContainer();
        this.musicPlayerInstance = this.pluginInstance.getMusicPlayer();
        this.moduleSettings = this.pluginInstance.getMusePluseSettings();
        this.isSwitchingSongs = new AtomicBoolean(false);

        this.permissibleSongs = this.getPermissibleSongs();
        this.currentlyPlayingSong = new AtomicReference<>(null);
        this.resetQueue();
    }

    /**
     * inserts a {@link SongData} into the next up queue that the player has
     * @param songData {@link SongData}
     */
    public final void insertSongIntoQueue(final SongData songData) {
        if (this.queuedSongs == null || this.queuedSongs.size() < 1)
            this.resetQueue();
        this.queuedSongs.add(0, songData);
    }

    /**
     * Stops the current song for the player
     */
    public final void stopCurrentSong() {
        this.player.stopAllSounds();
        if (this.musicTimer == null) return;
        this.musicTimer.cancel();
    }

    /**
     * Plays a specific song to the player
     * @param songData {@link SongData}
     */
    public final void playSpecificSong(final SongData songData) {
        if (this.isSwitchingSongs.get()) {
            return;
        }

        if (this.isListening()) {
            this.stopCurrentSong();
        }

        this.isSwitchingSongs.set(true);
        this.currentlyPlayingSong.set(songData);
        this.queuedSongs.remove(0);

        if (queuedSongs.size() < 1) this.resetQueue();
        this.sendSongMessages();

        TaskRunner.runSyncTaskLater(() -> {
            if (this.isListening() && !this.isSwitchingSongs.get()) return;
            this.player.playSound(this.player, "musepluse:%s".formatted(songData.getLocalKey()), SoundCategory.VOICE, 1.0F, 1.0F);
            this.isSwitchingSongs.set(false);
            this.musicTimer = TaskRunner.runSyncTaskLater(() -> {
                if (this.isSwitchingSongs.get()) return;
                if (!this.hasAutoPlayEnabled()) return; // player likely just played a music disc in the GUI
                this.playNext();
            }, songData.getTotalTicks());
        }, 10);

    }

    protected void sendSongMessages() {
        /* Messaging */
        if (!this.moduleSettings.isSendNextSongMessage()) return;
        if (this.moduleSettings.getNextSongMessages() == null) {
            if (this.moduleSettings.getNextSongMessage() == null) throw new MusePluseConfigurationException("\"Notifications.SongStarted.ActionBar\" was null during runtime! WHAT THE FUCK!!!!??????!!!!!????!!!");
            String message = convert(this.moduleSettings.getNextSongMessage());
            message = message.replace("%song_name%", convert(this.currentlyPlayingSong.get().getSongDisplayName()));
            message = message.replace("%song_artist%", convert(this.currentlyPlayingSong.get().getArtistName()));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(message));
        } else this.moduleSettings.getNextSongMessages().forEach(message -> {
            message = message.replace("%song_name%", convert(this.currentlyPlayingSong.get().getSongDisplayName()));
            message = message.replace("%song_artist%", convert(this.currentlyPlayingSong.get().getArtistName()));
            player.sendMessage(convert(message));
        });
    }

    /**
     * Plays the next song in the auto-queue for the player
     *
     * Use this metho to also start
     */
    public final void playNext() {
        if (this.isSwitchingSongs.get()) return;
        final SongData nextSong = this.nextSong();
        this.playSpecificSong(nextSong);
    }

    /**
     * Gets the next song in line for the player
     * @return {@link SongData}
     */
    public final SongData nextSong() {
        if (this.queuedSongs.isEmpty()) {
            this.resetQueue();
            return this.nextSong();
        } else return this.queuedSongs.get(0);
    }

    /**
     * Reshuffles the player's queue, this also
     * re-fill the players queue-list
     */
    public void resetQueue() {
        this.queuedSongs = new ArrayList<>(this.permissibleSongs);
        Collections.shuffle(this.queuedSongs);
    }

    /**
     * Checks if a player is currently listening to/switching to a song
     * In the case of a player switching songs we return tre
     * @return tru if the player is currently listening to a song
     */
    public final boolean isListening() {
        return this.musicTimer != null && !this.musicTimer.isCancelled();
    }

    /**
     * Checks if a player has the auto-player enabled
     * @return true if the player has the auto player enabled
     */
    public final boolean hasAutoPlayEnabled() {
        final Integer playing = this.dataContainer.get(this.autoPlayKey, PersistentDataType.INTEGER);
        if (playing == null) return this.moduleSettings.isPlayOnJoin(); // if the player doesn't have the flag yet use the configuration default
        return playing == 1; // 1 == playing 0 == not
    }

    /**
     * Sets whether or not the player should auto-listen to music when they join the server and when the server reloads
     * @param auto true if you want the player to auto-listen to music when they join and on the server reload
     */
    public final void setAutoPlayEnabled(boolean auto) {
        if (!auto) {
            this.stopCurrentSong();
        } else this.playNext();
        int status = 0;
        if (auto) status = 1;
        this.dataContainer.set(this.autoPlayKey, PersistentDataType.INTEGER, status);
        this.musicPlayerInstance.stopFor(this.player);
    }

    public final SongData currentSong() {
        return this.currentlyPlayingSong.get();
    }

    /**
     * returns a list of songs that the given player has permissions to, if said song has a permissions set
     * @return List of songs the player has access to
     */
    public List<SongData> getPermissibleSongs() {
        final List<SongData> songs = new ArrayList<>();
        this.pluginInstance.getMusicListConfig().getSongDataList().forEach(song -> {
            if (song.getPermission() != null && player.hasPermission(song.getPermission())) {
                songs.add(song);
            } else if (song.getPermission() == null) {
                songs.add(song);
            }
        });
        return songs;
    }
}
