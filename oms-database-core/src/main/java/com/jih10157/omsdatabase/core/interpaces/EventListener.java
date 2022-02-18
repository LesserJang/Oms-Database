package com.jih10157.omsdatabase.core.interpaces;

import java.util.function.Consumer;

public interface EventListener {
    void onJoin(Player player);
    void onConnect(Player player, Consumer<String> kick, Consumer<String> disallow);
}
