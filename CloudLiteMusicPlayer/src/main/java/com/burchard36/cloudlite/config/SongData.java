package com.burchard36.cloudlite.config;

import com.burchard36.cloudlite.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class SongData {

    @Getter
    protected final int minutes;
    @Getter
    protected final int seconds;
    @Getter
    protected final String localKey;
    @Getter
    protected final String songDisplayName;
    @Getter
    protected final String artistName;
    @Getter
    protected final Material guiMaterial;

    public SongData(final ConfigurationSection config, final String songLocalKey) {
        this.minutes = config.getInt("Length.Minutes");
        this.seconds = config.getInt("Length.Seconds");
        this.songDisplayName = config.getString("Details.SongName");
        this.artistName = config.getString("Details.Artist");
        this.guiMaterial = Material.valueOf(config.getString("Details.GuiMaterial"));
        this.localKey = songLocalKey;
    }

    /**
     * Gets the total amount of ticks this song is supposed to play for
     * @return total tick length of the song
     */
    public final long getTotalTicks() {
        final int tickSeconds = this.getSeconds() * 20;
        final int tickMinutes = (this.minutes * 60) * 20;
        return tickSeconds + tickMinutes;
    }

    public final ItemStack getDisplayItem() {
        return ItemUtils.createItemStack(
                this.guiMaterial,
                convert("Song: &b%s".formatted(this.songDisplayName)),
                "&f ",
                "&eSong By: &f%s".formatted(this.artistName),
                "&eSong Length: &f%sm %ss".formatted(this.minutes, this.seconds)
        );
    }
}
