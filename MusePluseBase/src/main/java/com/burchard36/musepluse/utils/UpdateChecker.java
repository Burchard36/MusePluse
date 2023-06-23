package com.burchard36.musepluse.utils;

import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateChecker {

    public static void checkVersion(final Player player, final Consumer<Void> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                final URL url = new URL("");



            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
