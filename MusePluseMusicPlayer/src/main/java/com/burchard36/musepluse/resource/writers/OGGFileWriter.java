package com.burchard36.musepluse.resource.writers;

import com.google.gson.Gson;
import org.bukkit.Bukkit;

import java.io.File;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public abstract class OGGFileWriter extends SoundsJsonWriter{
    public OGGFileWriter(Gson gson) {
        super(gson);
    }

    /**
     * Flashes the OGG Files from the /media/ogg directory into the /assets/assets/minecraft/sounds/music directory
     */
    public void flashOGGFilesToTempDirectory() {
        File[] files = this.getOggDirectory().listFiles();
        if (files == null) throw new RuntimeException("There was no OGG files found in /media/ogg, if this is intended the plugin will disable.");
        final File musicDirectory = new File(this.getResourcePackTempFilesDirectory(), "/assets/assets/minecraft/sounds/music");
        if (!musicDirectory.exists()) if (musicDirectory.mkdirs()) Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f created new &b/assets/assets/minecraft/sounds/music&f directory!"));
        for (File file : files) {
            if (file.renameTo(new File(musicDirectory, "/%s".formatted(file.getName()))))
                Bukkit.getConsoleSender().sendMessage(convert("&fFlashing OGG File &b%s").formatted(file.getPath()));
        }
    }
}
