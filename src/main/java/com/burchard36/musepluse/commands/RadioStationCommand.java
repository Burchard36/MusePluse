package com.burchard36.musepluse.commands;

import com.burchard36.musepluse.MusePlusePlugin;
import com.burchard36.musepluse.MusicListener;
import com.burchard36.musepluse.MusicPlayer;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.burchard36.libs.utils.StringUtils.convert;

/**
 * /radio [<leave | join>] [<playerName>]
 *
 * blank command opens a gui
 * otherwise, subcommand handler
 *
 */
public class RadioStationCommand implements CommandExecutor {

    protected final MusePlusePlugin pluginInstance;
    protected final MusicPlayer musicPlayer;

    public RadioStationCommand(final MusePlusePlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.musicPlayer = this.pluginInstance.getMusicPlayer();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (sender instanceof ConsoleCommandSender) {
            // do shit later
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) { // Open a gui
            player.sendMessage(convert("&aTBA"));
            return true;
        }

        switch (args.length) {
            case 1 -> {
                final String argumentOne = args[0].toUpperCase();

                switch (argumentOne) {
                    case "LEAVE" -> this.musicPlayer.quitRadioStation(player);

                    case "JOIN" -> {

                    }

                    case "CREATE" -> {

                    }
                }

            }
        }


        return false;
    }
}
