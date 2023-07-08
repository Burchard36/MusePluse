package com.burchard36.musepluse.resource.writers;

import com.burchard36.libs.config.MusePluseConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.FileWriter;
import java.io.IOException;

import static com.burchard36.libs.utils.StringUtils.convert;

public abstract class SoundsJsonWriter extends McMetaWriter {
    public SoundsJsonWriter(final Gson gson) {
        super(gson);
    }

    public void writeSoundsJson(final MusePluseConfig songConfig) {
        if (this.getSoundsJsonFile().exists()) {
            if (this.getSoundsJsonFile().delete()) Bukkit.getConsoleSender().sendMessage(convert("&fOld sounds.json was found, &cdeleting&f..."));
            try {
                if (this.getSoundsJsonFile().createNewFile()) Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f created new &bsounds.json"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        final JsonObject soundsJsonData = new JsonObject();
        songConfig.getSongDataList().forEach((song) -> {
            final JsonObject soundsArray = new JsonObject();
            final JsonArray arrayOfSongs = new JsonArray();
            arrayOfSongs.add("music/" + song.getLocalKey());
            soundsArray.add("sounds", arrayOfSongs);
            soundsJsonData.add(song.getLocalKey(), soundsArray);
        });

        try (final FileWriter writer = new FileWriter(this.getSoundsJsonFile())) {
            this.gson.toJson(soundsJsonData, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
