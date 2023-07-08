package com.burchard36.musepluse.resource;

import com.burchard36.libs.config.MusePluseSettings;
import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.resource.events.MusePluseResourcePackLoadedEvent;
import com.burchard36.libs.utils.TaskRunner;
import org.bukkit.Bukkit;
import spark.Spark;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static com.burchard36.libs.utils.StringUtils.convert;

/**
 * Generic static spark server for server the resource pack in
 * /resource-pack/resource_pack.zip
 *
 * This will only ever be enabled if its enabled in the configuration files
 */
public class ResourcePackServer {

    protected static MusePlusePlugin pluginInstance;
    protected static MusePluseSettings moduleSettings;
    protected static boolean isRunning = false;


    public static void startServer(final MusePlusePlugin pluginInstance) {
        if (isRunning) return;
        ResourcePackServer.pluginInstance = pluginInstance;
        moduleSettings = pluginInstance.getMusePluseSettings();
        CompletableFuture.runAsync(() -> {
            isRunning = true;
            Bukkit.getConsoleSender().sendMessage(convert("&aLaunching ResourcePackServer!"));
            Spark.port(moduleSettings.getResourcePackServerPort());

            Spark.staticFiles.externalLocation(new File(pluginInstance.getPluginInstance().getDataFolder(), "/resource-pack").getPath());
            Spark.init();
            Spark.awaitInitialization();
            TaskRunner.runSyncTask(() -> Bukkit.getPluginManager().callEvent(new MusePluseResourcePackLoadedEvent()));
        }, Executors.newSingleThreadExecutor());
    }

}
