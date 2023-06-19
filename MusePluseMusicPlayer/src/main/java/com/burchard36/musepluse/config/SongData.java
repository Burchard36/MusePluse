package com.burchard36.musepluse.config;

import com.burchard36.musepluse.exception.MusePluseConfigurationException;
import com.burchard36.musepluse.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class SongData {

    @Getter
    @Setter
    protected int seconds;
    @Getter
    protected final String localKey;
    @Getter
    protected final String songDisplayName;
    @Getter
    protected final String artistName;
    @Getter
    protected final Material guiMaterial;
    @Getter
    @Nullable
    protected final String permission;
    @Getter
    protected final String youTubeLink;

    public SongData(final ConfigurationSection config, final String songLocalKey) {
        this.songDisplayName = config.getString("Details.SongName");
        this.artistName = config.getString("Details.Artist");
        this.guiMaterial = Material.valueOf(config.getString("Details.GuiMaterial"));
        this.permission = config.getString("Details.Permission");
        this.youTubeLink = config.getString("YouTubeLink");
        if (this.youTubeLink == null) throw new MusePluseConfigurationException("All songs need a YouTube link now! %s doesnt!".formatted(this.songDisplayName));
        this.localKey = songLocalKey;
    }

    /**
     * Gets the total amount of ticks this song is supposed to play for
     * @return total tick length of the song
     */
    public final long getTotalTicks() {
        return this.getSeconds() * 20L;
    }

    public final int getMinutes() {
        return this.seconds / 60;
    }

    /**
     * Gets the display ItemStack for this item to be used
     * in the songs gui
     * @return {@link ItemStack}
     */
    public final ItemStack getDisplayItem() {
        final ItemStack itemStack = ItemUtils.createItemStack(
                this.guiMaterial,
                convert("&fSong: &b%s".formatted(this.songDisplayName)),
                "&f ",
                "&eSong By: &f%s".formatted(this.artistName),
                "&eSong Length: &f%sm %ss".formatted(this.getMinutes(), this.seconds % 60),
                "&f "
        );
        final ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        if (this.guiMaterial.name().startsWith("MUSIC"))
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
