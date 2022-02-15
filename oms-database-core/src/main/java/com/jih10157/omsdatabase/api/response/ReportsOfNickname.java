package com.jih10157.omsdatabase.api.response;

import com.jih10157.omsdatabase.api.object.Report;

import java.util.List;

public class ReportsOfNickname {

    public final int count;
    public final List<Report> list;

    public ReportsOfNickname(int count, List<Report> list) {
        this.count = count;
        this.list = list;
    }
}
