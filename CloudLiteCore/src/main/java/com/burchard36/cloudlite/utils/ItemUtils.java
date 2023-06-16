package com.burchard36.cloudlite.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class ItemUtils {

    private static final String TEXTURE_URL = "http://textures.minecraft.net/texture/";

    public static ItemStack createSkull(final String texture, final @Nullable String displayName, final @Nullable String... lore) {
        final ItemStack itemStack = createItemStack(Material.PLAYER_HEAD, displayName, lore);
        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        setSkullTextures(texture, skullMeta);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    protected static void setSkullTextures(String texture, SkullMeta meta) {
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.nameUUIDFromBytes(texture.getBytes()));
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(TEXTURE_URL + texture));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
    }

    public static ItemStack modify(final ItemStack itemStack, final String displayName) {
        final ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(convert(displayName));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack modify(final ItemStack itemStack, final String... lore) {
        final ItemMeta meta = itemStack.getItemMeta();
        assert  meta != null;
        final List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(convert(line));
        }
        meta.setLore(newLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(
            final String materialName,
            @Nullable final String displayName,
            final @Nullable String... lore) {
        return createItemStack(Material.valueOf(materialName), displayName, lore);
    }

    public static ItemStack createItemStack(
            final String materialName,
            final int amount,
            @Nullable final String displayName,
            @Nullable final String... lore) {
        final ItemStack stack = createItemStack(materialName, displayName, lore);
        stack.setAmount(amount);
        return stack;
    }

    public static ItemStack createItemStack(
            final Material material,
            @Nullable final String displayName,
            @Nullable final String... lore) {
        final ItemStack itemStack = new ItemStack(material);
        if (displayName == null && lore == null) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        if (displayName != null) itemMeta.setDisplayName(convert(displayName));
        if (lore != null) {
            final List<String> itemLore = new ArrayList<>();
            for (String loreLine : lore) {
                if (loreLine == null) continue;
                itemLore.add(convert(loreLine));
            }
            itemMeta.setLore(itemLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
