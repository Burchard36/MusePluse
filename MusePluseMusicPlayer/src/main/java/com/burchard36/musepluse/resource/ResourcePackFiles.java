package com.burchard36.musepluse.resource;

import com.burchard36.musepluse.MusePlusePlugin;
import lombok.Getter;

import java.io.File;

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

    public void mkdirs() {
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
