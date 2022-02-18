package com.jih10157.omsdatabase.bukkit;

import com.jih10157.omsdatabase.core.Core;
import com.jih10157.omsdatabase.core.interpaces.Config;
import com.jih10157.omsdatabase.core.interpaces.Player;
import com.jih10157.omsdatabase.core.interpaces.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Core core = new Core(new ImplServer(this), new ImplConfig(this));
        Objects.requireNonNull(getCommand("omsdatabase"))
            .setExecutor(new CommandListener(core.getCommandListener()));
        Bukkit.getPluginManager().registerEvents(new EventListener(core.getEventListener()), this);
        new Metrics(this, 14326);
    }

    public static class ImplServer implements Server {

        private final BukkitPlugin instance;
        private final Logger logger;

        private ImplServer(BukkitPlugin instance) {
            this.instance = instance;
            this.logger = instance.getLogger();
        }

        @Override
        public List<Player> getAllPlayers() {
            List<Player> list = new ArrayList<>();
            for (org.bukkit.entity.Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                list.add(new ImplPlayer(bukkitPlayer));
            }
            return list;
        }

        @Override
        @SuppressWarnings("deprecation")
        public Player getPlayer(String name) {
            return Bukkit.getPlayer(name) != null ? new ImplPlayer(Bukkit.getPlayer(name)) : null;
        }

        @Override
        public void error(String message, Throwable throwable) {
            this.logger.log(Level.SEVERE, message, throwable);
        }

        @Override
        public void doSync(Runnable runnable) {
            Bukkit.getScheduler().runTask(this.instance, runnable);
        }

        @Override
        public void sendMessage(String message) {
            Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static class ImplConfig implements Config {

        private final BukkitPlugin instance;

        private ImplConfig(BukkitPlugin instance) {
            this.instance = instance;
        }

        @Override
        public String getString(String path) {
            return instance.getConfig().getString(path);
        }

        @Override
        public void setString(String path, String str) {
            instance.getConfig().set(path, str);
        }

        @Override
        public int getInt(String path) {
            return instance.getConfig().getInt(path);
        }

        @Override
        public void setInt(String path, int integer) {
            instance.getConfig().set(path, integer);
        }

        @Override
        public boolean getBoolean(String path) {
            return instance.getConfig().getBoolean(path);
        }

        @Override
        public void setBoolean(String path, boolean bool) {
            instance.getConfig().set(path, bool);
        }

        @Override
        public void reloadConfig() {
            instance.reloadConfig();
        }
    }

    public static class ImplPlayer implements Player {

        private final org.bukkit.entity.Player bukkitPlayer;

        public ImplPlayer(org.bukkit.entity.Player bukkitPlayer) {
            this.bukkitPlayer = bukkitPlayer;
        }

        @Override
        public UUID getUUID() {
            return bukkitPlayer.getUniqueId();
        }

        @Override
        public String getName() {
            return bukkitPlayer.getName();
        }

        @Override
        public boolean hasPermission(String perm) {
            return bukkitPlayer.hasPermission(perm);
        }

        @Override
        public void sendMessage(String message) {
            bukkitPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
