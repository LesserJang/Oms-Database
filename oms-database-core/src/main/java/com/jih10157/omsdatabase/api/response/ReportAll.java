package com.jih10157.omsdatabase.api.response;

import com.jih10157.omsdatabase.api.object.Report;

import java.util.Collections;
import java.util.List;

public class ReportAll {

    public final int count;
    public final List<Report> list;

    public ReportAll(int count, List<Report> list) {
        this.count = count;
        this.list = Collections.unmodifiableList(list);
    }
}
