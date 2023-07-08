package com.burchard36.musepluse.commands;

import com.burchard36.libs.gui.SongListGui;
import com.burchard36.musepluse.MusePlusePlugin;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.burchard36.libs.utils.StringUtils.convert;

public class MusicGuiCommand implements CommandExecutor {


    protected final MusePlusePlugin pluginInstance;

    public MusicGuiCommand(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
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

        this.pluginInstance.getGuiManager()
                .openPaginatedTo((Player) sender, 0, new SongListGui(this.pluginInstance, player));
        return false;
    }
}
