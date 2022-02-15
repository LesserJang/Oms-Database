package com.jih10157.omsdatabase.core;

import com.jih10157.omsdatabase.api.OmsDatabase;
import com.jih10157.omsdatabase.core.impl.ImplCommandListener;
import com.jih10157.omsdatabase.core.impl.ImplEventListener;
import com.jih10157.omsdatabase.core.interpaces.CommandListener;
import com.jih10157.omsdatabase.core.interpaces.Config;
import com.jih10157.omsdatabase.core.interpaces.EventListener;
import com.jih10157.omsdatabase.core.interpaces.Server;

public class Core {

    private final Server server;
    private final Config config;
    private final EventListener eventListener;
    private final CommandListener commandListener;
    private final OmsDatabase omsDatabase = new OmsDatabase();

    public Core(Server server, Config config) {
        this.server = server;
        this.config = config;
        this.eventListener = new ImplEventListener(this);
        this.commandListener = new ImplCommandListener(this);
    }

    public Server getServer() {
        return server;
    }

    public Config getConfig() {
        return config;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    public OmsDatabase getOmsDatabase() {
        return omsDatabase;
    }
}
