package com.burchard36.musepluse.radio;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RadioStation {

    @Getter
    protected final UUID ownerUID;

    public RadioStation(final Player player) {
        this.ownerUID = player.getUniqueId();

    }





}
