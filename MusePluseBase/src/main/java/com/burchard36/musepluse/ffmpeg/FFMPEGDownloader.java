package com.burchard36.musepluse.ffmpeg;

import com.burchard36.musepluse.ffmpeg.events.FFMPEGInitializedEvent;
import com.burchard36.musepluse.utils.TaskRunner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.burchard36.musepluse.MusePlusePlugin.IS_WINDOWS;
import static com.burchard36.musepluse.MusePlusePlugin.THREAD_POOL;
import static com.burchard36.musepluse.utils.StringUtils.convert;

public class FFMPEGDownloader {

    protected final URL linuxDownloadLink;
    protected final URL windowsDownloadLink;
    protected final File ffmpegInstallationDirectory;
    @Getter
    protected final AtomicBoolean downloading = new AtomicBoolean(false);

    public FFMPEGDownloader(final JavaPlugin plugin) {
        try {
            this.windowsDownloadLink = new URL("https://github.com/CloudLiteMC/ffmpeg-as-zip/raw/main/ffmpeg-windows.zip");
            this.linuxDownloadLink = new URL("https://github.com/CloudLiteMC/ffmpeg-as-zip/raw/main/ffmpeg-linux.zip");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        this.ffmpegInstallationDirectory = new File(plugin.getDataFolder(), "/ffmpeg");
        if (!this.ffmpegIsInstalled()) {
            this.ffmpegInstallationDirectory.mkdirs();
            Bukkit.getConsoleSender().sendMessage(convert("&cFFMPEG Was detected as not installed on this server"));
            Bukkit.getConsoleSender().sendMessage(convert("&cAsynchronus installation will now begin & most plugin features"));
            Bukkit.getConsoleSender().sendMessage(convert("&cWill be halted until this installation is completed"));
            Bukkit.getConsoleSender().sendMessage(convert("&cThis will be the only time your server will need to do this!"));
            Bukkit.getConsoleSender().sendMessage(convert("&fCreated directory &b/ffmpeg"));
            this.downloading.set(true);
        }
    }

    /**
     * Asynchronously installs FFMPEG into the plugins/ffmpeg folder
     */
    public final void installFFMPEG() {
        if (this.ffmpegIsInstalled()) {
            this.downloading.set(false);
            Bukkit.getConsoleSender().sendMessage(convert("&fFFMPEG was detected as installed on this server!"));
            return;
        }
        this.downloading.set(true);
        Bukkit.getConsoleSender().sendMessage(convert("&fAttempting to download ffmpeg. . ."));
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) this.getURL().openConnection();
                connection.setRequestMethod("GET");
                final BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry zipEntry = zipInputStream.getNextEntry();

                Bukkit.getConsoleSender().sendMessage(convert("&fFFMPEG Has successfully downloaded! Extracting files. . ."));
                while (zipEntry != null) {
                    final File file = new File(this.ffmpegInstallationDirectory, zipEntry.getName());

                    if (!zipEntry.isDirectory()) {
                        final BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        int i;
                        while ((i = zipInputStream.read()) != -1) {
                            fileOutputStream.write(i);
                        }
                        fileOutputStream.close();
                    } else {
                        if (file.mkdirs())
                            Bukkit.getConsoleSender().sendMessage(convert("[FFMPEG] Successfully created directory &b%s&f".formatted(zipEntry.getName())));
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
                Bukkit.getConsoleSender().sendMessage(convert("FFMPEG Has been successfully installed into &b/ffmpeg"));
                downloading.set(false);
                TaskRunner.runSyncTask(() -> Bukkit.getPluginManager().callEvent(new FFMPEGInitializedEvent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, THREAD_POOL);
    }

    public boolean isDownloading() {
        return this.downloading.get();
    }

    public URL getURL() {
        if (IS_WINDOWS) return this.windowsDownloadLink;
        return this.linuxDownloadLink;
    }


    /**
     * Checks if FFMPEG is installed on the server
     * @return true if ffmpeg files are detected
     */
    public final boolean ffmpegIsInstalled() {
        if (!this.ffmpegInstallationDirectory.exists()) return false;
        String[] files = this.ffmpegInstallationDirectory.list();
        if (files == null) return false;
        return  files.length > 0;
    }

}
