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
import java.util.List;

import static com.burchard36.musepluse.utils.StringUtils.convert;

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
    protected String resourcePack;
    @Getter
    protected boolean autoGenerateResourcePack;


    @Override
    public @NonNull String getFileName() {
        return "settings.yml";
    }

    @SneakyThrows
    @Override
    public void deserialize(FileConfiguration configuration) {
        this.playOnJoin = configuration.getBoolean("JoinSettings.PlayOnJoin", true);
        this.sendNextSongMessage = configuration.getBoolean("Notifications.SongStarted.Send");
        this.needsPermissionToPlayOnJoin = configuration.getBoolean("JoinSettings.NeedsPermission", true);
        this.needsSkipPermission = configuration.getBoolean("QueueSettings.NeedsSkipPermission", false);
        this.needsForcePlayPermission = configuration.getBoolean("QueueSettings.NeedsForcePlayPermission", false);
        this.resourcePackServerPort = configuration.getInt("ResourcePackServer.Port", 67699);
        this.resourcePackServerEnabled = configuration.getBoolean("ResourcePackServer.Enabled", true);
        this.resourcePack = configuration.getString("ResourcePackServer.Host", "localhost");
        this.autoGenerateResourcePack = configuration.getBoolean("AutoGenerateResourcePack", true);
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

    public String getResourcePack(final File resourcePackFile) {
        final String fileUUID = resourcePackFile.getName().split("\\.")[0];
        return "http://%s:%s/resource_pack.zip".formatted(this.resourcePack, this.resourcePackServerPort);
    }
}
