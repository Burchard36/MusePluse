package com.burchard36.musepluse.resource.writers;

import com.burchard36.musepluse.resource.ResourcePackFiles;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.FileWriter;
import java.io.IOException;

import static com.burchard36.libs.utils.StringUtils.convert;

public abstract class McMetaWriter extends ResourcePackFiles {
    public final Gson gson;

    public McMetaWriter(final Gson gson) {
        this.gson = gson;
    }

    /**
     * Writes the mc meta file to the temp file location
     */
    public void writeMcMeta() {
        if (this.getMcMetaFile().exists()) {
            if (this.getMcMetaFile().delete()) Bukkit.getConsoleSender().sendMessage(convert("&fOld pack.mcmeta was found, &cdeleting&f..."));
            try {
                if (this.getMcMetaFile().createNewFile()) Bukkit.getConsoleSender().sendMessage(convert("&aSuccessfully&f created new &bpack.mcmeta"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonObject object = new JsonObject();
        JsonObject subObject = new JsonObject();
        object.add("pack", subObject);
        subObject.addProperty("pack_format", 13);
        subObject.addProperty("description", "Muse Pluse Resource Pack!");
        try (final FileWriter writer = new FileWriter(this.getMcMetaFile())) {
            this.gson.toJson(object, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
