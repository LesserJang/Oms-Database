package com.jih10157.omsdatabase.core.interpaces;

public interface CommandListener {
    void onCommandByPlayer(Player player, String[] args);
    void onCommandByConsole(String[] args);
}
