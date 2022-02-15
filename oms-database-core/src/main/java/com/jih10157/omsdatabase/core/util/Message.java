package com.jih10157.omsdatabase.core.util;

public class Message {

    public static String formatMessage(String string, String uuid, String name, int count) {
        return string.replace('&', '§').replace("{uuid}", uuid)
            .replace("{player}", name).replace("{count}", String.valueOf(count))
            .replace("{newline}", "\n");
    }
}
