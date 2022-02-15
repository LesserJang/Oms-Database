package com.jih10157.omsdatabase.bukkit;

import com.jih10157.omsdatabase.bukkit.BukkitPlugin.ImplPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    private final com.jih10157.omsdatabase.core.interpaces.CommandListener comListener;

    public CommandListener(
        com.jih10157.omsdatabase.core.interpaces.CommandListener commandListener) {
        this.comListener = commandListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            comListener.onCommandByPlayer(new ImplPlayer((Player) sender), args);
        } else {
            comListener.onCommandByConsole(args);
        }
        return true;
    }
}
