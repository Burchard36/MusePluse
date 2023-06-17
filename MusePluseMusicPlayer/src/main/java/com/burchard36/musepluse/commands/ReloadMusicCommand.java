package com.burchard36.musepluse.commands;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadMusicCommand implements CommandExecutor {

    protected final MusePluseMusicPlayer moduleInstance;

    public ReloadMusicCommand(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            return true;
        } else return false;
    }
}