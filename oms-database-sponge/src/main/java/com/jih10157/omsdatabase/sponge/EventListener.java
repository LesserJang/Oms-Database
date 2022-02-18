package com.jih10157.omsdatabase.sponge;

import com.jih10157.omsdatabase.sponge.SpongePlugin.ImplPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.event.network.ClientConnectionEvent.Login;
import org.spongepowered.api.text.serializer.TextSerializers;

public class EventListener {

    private final com.jih10157.omsdatabase.core.interpaces.EventListener listener;

    public EventListener(com.jih10157.omsdatabase.core.interpaces.EventListener eventListener) {
        this.listener = eventListener;
    }

    @Listener
    public void onJoin(Join event) {
        listener.onJoin(new ImplPlayer(event.getTargetEntity()));
    }

    @Listener
    public void onConnect(Login event) {
        listener.onConnect(new ImplPlayer(event.getTargetUser()),
            msg -> event.getTargetUser().getPlayer()
                .ifPresent(p -> p.kick(TextSerializers.FORMATTING_CODE.deserialize(msg))),
            msg -> {
                event.setCancelled(true);
                event.setMessage(TextSerializers.FORMATTING_CODE.deserialize(msg));
            });
    }
}
