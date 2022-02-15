package com.jih10157.omsdatabase.core.interpaces;

import java.util.List;

public interface Server extends Sendable {
    List<Player> getAllPlayers();
    Player getPlayer(String name);
}
