package com.jih10157.omsdatabase.bukkit;

import com.jih10157.omsdatabase.bukkit.BukkitPlugin.ImplPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class EventListener implements Listener {

    private final com.jih10157.omsdatabase.core.interpaces.EventListener listener;

    public EventListener(com.jih10157.omsdatabase.core.interpaces.EventListener eventListener) {
        this.listener = eventListener;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        listener.onJoin(new ImplPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent event) {
        String string = listener.onConnect(new ImplPlayer(event.getPlayer()));
        if (string != null) {
            event.disallow(Result.KICK_BANNED, string);
        }
    }
}
