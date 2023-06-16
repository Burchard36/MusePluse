package com.burchard36.cloudlite.commands;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import com.burchard36.cloudlite.gui.SongListGui;
import com.burchard36.cloudlite.utils.TaskRunner;
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

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class MusicGuiCommand implements CommandExecutor {

    public HashMap<UUID, BukkitTask> commandCoolDowns;

    protected final CloudLiteMusicPlayer moduleInstance;

    public MusicGuiCommand(final CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.commandCoolDowns = new HashMap<>();
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String @NonNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("FUCK OFF CONSOLE YOU CANT OPEN GUIS");
            return false;
        }

        final Player player = (Player) sender;
        if (this.commandCoolDowns.get(player.getUniqueId()) != null && !player.isOp()) {
            player.sendMessage(convert("&cIn order to prevent abuse, GUI opening speed is limited on this server."));
            return false;
        }

        Bukkit.getLogger().info(convert("&fOpening a &bSongListGui&f for player &e%s".formatted(player.getName())));
        this.moduleInstance.getPluginInstance().getGuiManager()
                .openPaginatedTo((Player) sender, 0, new SongListGui(this.moduleInstance));
        this.commandCoolDowns.put(player.getUniqueId(), TaskRunner.runSyncTaskLater(() -> {
            commandCoolDowns.remove(player.getUniqueId());
        }, 160));
        return false;
    }
}
