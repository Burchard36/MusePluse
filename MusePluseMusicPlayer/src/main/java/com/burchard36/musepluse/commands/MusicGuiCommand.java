package com.burchard36.musepluse.commands;

import com.burchard36.musepluse.MusePluseMusicPlayer;
import com.burchard36.musepluse.gui.SongListGui;
import com.burchard36.musepluse.utils.TaskRunner;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

import java.util.UUID;

import static com.burchard36.musepluse.utils.StringUtils.convert;

public class MusicGuiCommand implements CommandExecutor {


    protected final MusePluseMusicPlayer moduleInstance;

    public MusicGuiCommand(final MusePluseMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String @NonNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Console cannot open Gui's!");
            return false;
        }

        final Player player = (Player) sender;
        if (!player.hasPermission("musepluse.gui")) {
            player.sendMessage(convert("&cYou do not have permission to use this command!"));
            return false;
        }
        Bukkit.getLogger().info(convert("&fOpening a &bSongListGui&f for player &e%s".formatted(player.getName())));
        this.moduleInstance.getPluginInstance().getGuiManager()
                .openPaginatedTo((Player) sender, 0, new SongListGui(this.moduleInstance, player));
        return false;
    }
}
