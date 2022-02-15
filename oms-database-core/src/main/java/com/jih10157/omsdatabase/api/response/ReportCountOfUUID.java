package com.jih10157.omsdatabase.api.response;

import com.jih10157.omsdatabase.api.object.User;

public class ReportCountOfUUID {

    public final User user;
    public final int count;

    public ReportCountOfUUID(User user, int count) {
        this.user = user;
        this.count = count;
    }
}
