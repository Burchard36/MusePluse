package com.burchard36.libs.ffmpeg;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.resource.SongQuality;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FFExecutor {
    public static Executor FFMPEG_THREAD_POOL = Executors.newFixedThreadPool(2);

    protected final File ffmpeg;
    protected final Runtime runtime = Runtime.getRuntime();
    protected final MusePlusePlugin pluginInstance;

    public FFExecutor(final File ffmpeg) {
        this.ffmpeg = ffmpeg;
        this.pluginInstance = MusePlusePlugin.INSTANCE;
    }

    public void doFuckingSomething() {
        Bukkit.getLogger().info("What????");
    }

    public void convertToOgg(final File from, final File to, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            try {
                // TODO If this breaks the original command we used is this line here
                final int songQuality = SongQuality.getQualityNumber(this.pluginInstance.getMusePluseSettings().getSongGenerationQuality());
                //final Process process = runtime.exec("%s -y -v error -i %s %s".formatted(ffmpeg.getPath(), from.getPath(), to.getPath()));
                //Bukkit.getLogger().info("Running FFMPEG: %s -y -v error -i %s -q:a %sk %s".formatted(ffmpeg.getPath(), from.getPath(), songQuality, to.getPath()));
                final Process process = runtime.exec("%s -y -v error -i %s -c:a libvorbis -b:a %sk %s".formatted(ffmpeg.getPath(), from.getPath(), songQuality, to.getPath()));

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(process.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(process.getErrorStream()));

                System.out.println("Here is the standard output of the command:\n");
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }

                System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }

                process.onExit().thenAccept((v) -> onComplete.run()).join();

            //TODO Debugging purposes, change back to IOException later
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, FFMPEG_THREAD_POOL);
    }

}
