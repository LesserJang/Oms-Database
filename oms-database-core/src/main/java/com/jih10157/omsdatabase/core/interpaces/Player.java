package com.jih10157.omsdatabase.core.interpaces;

import java.util.UUID;

public interface Player extends Sendable {
    UUID getUUID();
    String getName();
    boolean hasPermission(String perm);
}
