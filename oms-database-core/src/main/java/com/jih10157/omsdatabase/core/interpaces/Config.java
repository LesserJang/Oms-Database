package com.jih10157.omsdatabase.core.interpaces;

public interface Config {

    String getString(String path);

    void setString(String path, String str);

    int getInt(String path);

    void setInt(String path, int integer);

    boolean getBoolean(String path);

    void setBoolean(String path, boolean bool);

    void reloadConfig();
}
