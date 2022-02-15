package com.jih10157.omsdatabase.api.object;

import java.util.UUID;

public class User {

    public final UUID uuid;
    public final String userName;

    public User(String uuid, String userName) {
        this.uuid = UUID.fromString(uuid);
        this.userName = userName;
    }
}
