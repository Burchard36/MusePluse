package com.burchard36.musepluse.commands;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.config.MusePluseSettings;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class SkipSongCommand implements CommandExecutor {

    protected final MusePluseMusicPlayer moduleInstance;
    protected final MusePluseSettings musePluseSettings;

    public SkipSongCommand(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musePluseSettings = this.moduleInstance.getMusePluseSettings();
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String @NonNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Dude fuck off you cant listen to music loser join the game ffs");
            return false;
        }

        if (this.musePluseSettings.isNeedsSkipPermission() && !sender.hasPermission("musepluse.queue.skip")){
            sender.sendMessage(convert("&cYou do not have permission to use this command!"));
            return false;
        }

        this.moduleInstance.getMusicPlayer().playNextSong((Player) sender);
        return true;
    }
}
