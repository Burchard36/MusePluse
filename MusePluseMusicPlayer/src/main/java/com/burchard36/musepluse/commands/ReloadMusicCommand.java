package com.burchard36.musepluse.commands;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class ReloadMusicCommand implements CommandExecutor {

    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings moduleSettings;

    public ReloadMusicCommand(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.moduleSettings = this.moduleInstance.getMusePluseSettings();

    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!sender.hasPermission("musepluse.reload")) return false;

        sender.sendMessage(convert("&aMusePluse will now attempt to rebuild the songs.yml into a new texture pack!"));
        sender.sendMessage(convert("&aYour current listening experience will not be interrupted until this process is complete!"));
        return false;
    }
}
