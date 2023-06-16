package com.burchard36.cloudlite.commands;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SkipSongCommand implements CommandExecutor {

    protected final CloudLiteMusicPlayer moduleInstance;

    public SkipSongCommand(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
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

        this.moduleInstance.getMusicPlayer().playFor((Player) sender);
        return true;
    }
}
