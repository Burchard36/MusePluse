package com.burchard36.cloudlite.commands;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadMusicCommand implements CommandExecutor {

    protected final CloudLiteMusicPlayer moduleInstance;

    public ReloadMusicCommand(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            return true;
        } else return false;
    }
}
