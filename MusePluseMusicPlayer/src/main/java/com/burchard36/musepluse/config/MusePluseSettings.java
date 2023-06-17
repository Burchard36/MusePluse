package com.burchard36.musepluse.config;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.exception.MusePluseConfigurationException;
import com.burchard36.musepluse.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class MusePluseSettings implements Config {

    @Getter
    protected String resourcePack;
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
    protected boolean autoUpdateResourcePack;

    @Override
    public @NonNull String getFileName() {
        return "settings.yml";
    }

    @SneakyThrows
    @Override
    public void deserialize(FileConfiguration configuration) {
        this.autoUpdateResourcePack = configuration.getBoolean("AutoUpdateResourcePack", true);
        if (!configuration.isSet("AutoUpdateResourcePack")) {
            configuration.set("AutoUpdateResourcePack", true);
            configuration.setComments("AutoUpdateResourcePack", List.of("Should the resource pack url be force updated every update?"));
        }

        if (this.autoUpdateResourcePack) {
            this.resourcePack = "https://github.com/Burchard36/MusePluse/raw/main/MusePluseResources1.0.2-SNAPSHOT.zip";
            configuration.set("ResourcePack", this.resourcePack);
        } else {
            this.resourcePack = configuration.getString("ResourcePack", "https://github.com/Burchard36/MusePluse/raw/main/MusePluseResources1.0.2-SNAPSHOT.zip");
        }
        this.playOnJoin = configuration.getBoolean("JoinSettings.PlayOnJoin", true);
        this.sendNextSongMessage = configuration.getBoolean("Notifications.SongStarted.Send");
        this.needsPermissionToPlayOnJoin = configuration.getBoolean("JoinSettings.NeedsPermission", true);
        this.needsSkipPermission = configuration.getBoolean("QueueSettings.NeedsSkipPermission", false);
        this.needsForcePlayPermission = configuration.getBoolean("QueueSettings.NeedsForcePlayPermission", false);
        this.loadNextSongMessage(configuration);

        configuration.save(new File(MusePlusePlugin.INSTANCE.getDataFolder(), this.getFileName()));
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
}
