package com.burchard36.musepluse.resource;

import com.burchard36.musepluse.MusePlusePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;

import static com.burchard36.libs.utils.StringUtils.convert;

public class ResourcePackFiles {

    @Getter
    private final File resourcePackDirectory;
    @Getter
    private final File mediaFilesDirectory;
    @Getter
    private final File resourcePackTempFilesDirectory;
    @Getter
    private final File oggDirectory;
    @Getter
    private final File m4aDirectory;
    @Getter
    private final File soundsJsonFile;
    @Getter
    private final File mcMetaFile;

    public ResourcePackFiles() {
        File dataFolder = MusePlusePlugin.INSTANCE.getDataFolder();
        this.resourcePackDirectory = new File(dataFolder, "/resource-pack");
        this.mediaFilesDirectory = new File(dataFolder, "/media");

        this.resourcePackTempFilesDirectory = new File(this.resourcePackDirectory, "/temp");
        this.oggDirectory = new File(this.mediaFilesDirectory, "/ogg");
        this.m4aDirectory = new File(this.mediaFilesDirectory, "/m4a");
        this.soundsJsonFile = new File(this.resourcePackTempFilesDirectory, "/assets/assets/musepluse/sounds.json");
        this.mcMetaFile = new File(this.resourcePackTempFilesDirectory, "/assets/pack.mcmeta");
    }

    /**
     * Creates new directory's recursively as well as clearing any existing ones!
     */
    public void mkdirs() {
        if (this.mediaFilesDirectory.exists()) {
            Bukkit.getConsoleSender().sendMessage(convert("&cDetected an old /media directory, cleaning..."));
            this.deleteDir(this.mediaFilesDirectory);
        }

        if (this.resourcePackTempFilesDirectory.exists()) {
            Bukkit.getConsoleSender().sendMessage(convert("&cDetected an old temporary resource pack directory, deleteing..."));
            this.deleteDir(this.resourcePackTempFilesDirectory);
        }


        this.oggDirectory.mkdirs();
        this.m4aDirectory.mkdirs();
        this.soundsJsonFile.mkdirs();
    }

    public void cleanUp() {
        this.deleteDir(this.getResourcePackTempFilesDirectory());
        this.deleteDir(this.getMediaFilesDirectory());
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
