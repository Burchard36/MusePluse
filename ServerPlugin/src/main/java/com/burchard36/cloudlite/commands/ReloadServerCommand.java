package com.burchard36.cloudlite.commands;

import com.burchard36.cloudlite.ServerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class ReloadServerCommand implements CommandExecutor {
    protected final ServerPlugin pluginInstance;

    public ReloadServerCommand(final ServerPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return false;

        sender.sendMessage(convert("&aReloading modules...."));
        this.pluginInstance.getModuleLoader().reloadModules();
        sender.sendMessage(convert("&aReloaded all modules!"));
        return false;
    }
}
