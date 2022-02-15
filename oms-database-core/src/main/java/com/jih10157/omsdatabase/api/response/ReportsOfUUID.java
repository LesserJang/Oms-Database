package com.jih10157.omsdatabase.api.response;

import com.jih10157.omsdatabase.api.object.Report;
import com.jih10157.omsdatabase.api.object.User;

import java.util.List;

public class ReportsOfUUID {
    public final User user;
    public final int count;
    public final List<Report> list;

    public ReportsOfUUID(User user, int count, List<Report> list) {
        this.user = user;
        this.count = count;
        this.list = list;
    }
}
