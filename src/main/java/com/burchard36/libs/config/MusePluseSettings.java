package com.burchard36.libs.config;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.exception.MusePluseConfigurationException;
import com.burchard36.libs.utils.StringUtils;
import com.burchard36.libs.utils.TaskRunner;
import com.burchard36.musepluse.resource.SongQuality;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.burchard36.musepluse.MusePlusePlugin.MAIN_THREAD_POOL;
import static com.burchard36.libs.utils.StringUtils.convert;

public class MusePluseSettings implements Config {

    @Getter
    protected List<String> nextSongMessages = null;
    @Getter
    protected String nextSongMessage = null;
    @Getter
    protected boolean playOnJoin = true;
    @Getter
    protected boolean needsPermissionToPlayOnJoin = true;
    @Getter
    protected boolean disconnectOnReject = true;
    @Getter
    protected boolean sendNextSongMessage;
    @Getter
    protected boolean needsSkipPermission;
    @Getter
    protected boolean needsForcePlayPermission;
    @Getter
    protected int resourcePackServerPort;
    @Getter
    protected boolean resourcePackServerEnabled;
    protected String resourcePackHostAddress;
    protected String selfHostedResourcePackAddress;
    @Getter
    protected boolean autoGenerateResourcePack;
    @Getter
    protected boolean doItYourselfMode;
    @Getter
    protected boolean usingUpdateChecker;
    @Getter
    protected int downloadTimeoutSeconds;
    @Getter
    protected SongQuality songGenerationQuality;

    @Override
    public @NonNull String getFileName() {
        return "settings.yml";
    }


    @Override
    public void deserialize(FileConfiguration configuration) {
        this.playOnJoin = configuration.getBoolean("JoinSettings.PlayOnJoin", true);
        this.sendNextSongMessage = configuration.getBoolean("Notifications.SongStarted.Send");
        this.needsPermissionToPlayOnJoin = configuration.getBoolean("JoinSettings.NeedsPermission", true);
        this.needsSkipPermission = configuration.getBoolean("QueueSettings.NeedsSkipPermission", false);
        this.needsForcePlayPermission = configuration.getBoolean("QueueSettings.NeedsForcePlayPermission", false);
        this.resourcePackServerPort = configuration.getInt("ResourcePackServer.Port", 67699);
        this.resourcePackServerEnabled = configuration.getBoolean("ResourcePackServer.Enabled", true);
        this.resourcePackHostAddress = configuration.getString("ResourcePackServer.Host", "localhost");
        this.autoGenerateResourcePack = configuration.getBoolean("AutoGenerateResourcePack", true);
        this.selfHostedResourcePackAddress = configuration.getString("ResourcePack");
        if (!configuration.isSet("DoItYourselfMode")) {
            configuration.set("DoItYourselfMode", false);
            configuration.setComments("DoItYourselfMode", List.of("If you use this mode, packs still stop being sent ENTIRELY, meaning you want your users to manually install & ",
                    "download the pack themselves. If this mode is enabled, songs will play for the users as normal, they just wont hear them until they load the pack themself!",
                    "This will also override the ResourcePackServer if its set to true! Meaning it wont be enabled."));
        }

        if (!configuration.isSet("UpdateChecker")) {
            configuration.set("UpdateChecker", true);
            configuration.setComments("UpdateChecker", List.of("Set this to false if you find the auto-update message annoying!"));
        }

        if (!configuration.isSet("DownloadTimeoutSeconds")) {
            configuration.set("DownloadTimeoutSeconds", 25);
            configuration.setComments("DownloadTimeoutSeconds", List.of("Sets the max time a song may spend downloading, the song/resource pack will attempt to rebuild if this happens."));
        }

        if (!configuration.isSet("GenerationQuality")) {
            configuration.set("GenerationQuality", "MEDIUM");
            configuration.setComments("GenerationQuality", List.of("Set the quality for the audio, set to lower quality if you want a lower size resource pack",
                    "Do note that vanilla minecraft on 1.18+ Can only automatically load 150MB Resource packs at the most",
                    "Valid Qualities: HORRIBLE, LOW, MEDIUM, HIGH, ULTRA"));

        }

        this.doItYourselfMode = configuration.getBoolean("DoItYourselfMode");
        this.usingUpdateChecker = configuration.getBoolean("UpdateChecker");
        this.downloadTimeoutSeconds = configuration.getInt("DownloadTimeoutSeconds");
        this.songGenerationQuality = SongQuality.valueOf(Objects.requireNonNull(configuration.getString("GenerationQuality")).toUpperCase());
        this.loadNextSongMessage(configuration);

        try {
            configuration.save(new File(MusePlusePlugin.INSTANCE.getDataFolder(), this.getFileName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadNextSongMessage(final FileConfiguration configuration) {
        if (configuration.isSet("Notifications.SongStarted.Text")
                && configuration.isSet("Notifications.SongStarted.ActionBar"))
            throw new MusePluseConfigurationException("Uh Ohh! Seems you have \"Notifications.SongStarted.Text\" & \"Notifications.SongStarted.ActionBar\" set in the config! Did you not read the config?!");

        if (configuration.isSet("Notifications.SongStarted.Text")) {
            final List<String> textMessageRaw = configuration.getStringList("Notifications.SongStarted.Text");
            if (textMessageRaw.isEmpty()) throw new MusePluseConfigurationException("Uh ohh! What are you doing at \"\"Notifications.SongStarted.Text\"? It must be a list silly!");
            this.nextSongMessages = new ArrayList<>();
            textMessageRaw.forEach(message -> {
                if (message.startsWith("<center>")) {
                    message = message.replace("<center>", "");
                    nextSongMessages.add(convert(StringUtils.getCenteredString(message)));
                } else nextSongMessages.add(convert(message));
            });
            return;
        }

        if (configuration.isSet("Notifications.SongStarted.ActionBar")) {
            String textMessageRaw = configuration.getString("Notifications.SongStarted.ActionBar");
            if (textMessageRaw == null) throw new MusePluseConfigurationException("Uh Ohh! Why is \"Notifications.SongStarted.ActionBar\" not a string? Silly! Only one string!");
            this.nextSongMessage = convert(textMessageRaw);
        }

    }

    /**
     * Safely gets the URL for the resource pack, can automatically determine if
     * it needs to return the internal or external URL
     * @param resourcePackFile A File to the resource pack
     */
    public final void getResourcePack(final File resourcePackFile, Consumer<String> ipAddr) {
        if (!this.isResourcePackServerEnabled()) {
            TaskRunner.runSyncTask(() -> ipAddr.accept(this.selfHostedResourcePackAddress));
            return;
        }
        CompletableFuture.runAsync(() -> {
            final String fileUUID = resourcePackFile.getName().split("\\.")[0];
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://ifconfig.me/ip"))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String externalAddress = response.body();
                TaskRunner.runSyncTask(() ->
                        ipAddr.accept("http://%s:%s/%s.zip".formatted(externalAddress, this.resourcePackServerPort, fileUUID)));
            } catch (IOException | URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MAIN_THREAD_POOL);
    }
}
