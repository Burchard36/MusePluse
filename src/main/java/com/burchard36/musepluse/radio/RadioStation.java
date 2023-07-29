package com.burchard36.musepluse.radio;

import com.burchard36.musepluse.MusePlusePlugin;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RadioStation {

    @Getter
    protected final UUID ownerUID;
    protected final List<UUID> listeningPlayers;
    protected final MusePlusePlugin pluginInstance;

    public RadioStation(final Player player) {
        this.ownerUID = player.getUniqueId();
        this.listeningPlayers = new ArrayList<>();
        this.pluginInstance = MusePlusePlugin.INSTANCE;
        Chunk
    }


    /**
     * Forces all players who are currently tuned into this radio station to force listen to the next song of the song leader
     * Do note, that this will stop all songs currently playing to all RadioListeners, this is a minecraft limitation in being unable to
     * Start songs at x time stamp
     *
     * @since 2.0.8
     */
    public void forceSyncPlayers() {

    }


}
