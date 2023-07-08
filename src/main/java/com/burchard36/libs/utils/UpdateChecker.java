package com.burchard36.libs.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private final static String VERSION = "2.0.5";

    public static void checkVersion(final Player player, final Runnable callback) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://raw.githubusercontent.com/Burchard36/MusePluse/main/VERSION.txt"))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String versionString = response.body();
                if (!versionString.equals(VERSION)) {
                    TaskRunner.runSyncTaskLater(() -> {
                        player.sendMessage(StringUtils.convert("&a&lHey!&a this is the author of &a&lMusePluse!"));
                        player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                        TaskRunner.runSyncTaskLater(() -> {
                            player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                            player.sendMessage(StringUtils.convert("&a&lA new update is out (%s)! &ehttps://www.spigotmc.org/resources/musepluse-youtube-music-player-massive-update.110536/".formatted(versionString)));
                            player.sendMessage(StringUtils.convert("&cOnly server operators and those with musepluse.update will see this message!"));
                        }, 35);
                    }, 60);
                }

            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
