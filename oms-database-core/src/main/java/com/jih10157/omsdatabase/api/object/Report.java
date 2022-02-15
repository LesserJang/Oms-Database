package com.jih10157.omsdatabase.api.object;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import com.jih10157.omsdatabase.api.object.Report.ReporterType;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class Report {

    public final int id;
    public final User user;
    public final String serverName;
    public final String serverAddress;
    public final String serverCommunity;
    public final ReporterType reporterType;
    public final Reason reason;
    public final String specificReason;
    public final Instant reportDate;
    public final List<String> files;

    public Report(int id, User user, String serverName, String serverAddress,
        String serverCommunity,
        ReporterType reporterType, Reason reason, String specificReason, Instant reportDate,
        List<String> files) {
        this.id = id;
        this.user = user;
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.serverCommunity = serverCommunity;
        this.reporterType = reporterType;
        this.reason = reason;
        this.specificReason = specificReason;
        this.reportDate = reportDate;
        this.files = files;
    }

    public enum ReporterType {
        User("사용자"), OPERATOR("관리자"), ADMINISTRATOR("소유자");

        public final String name;

        ReporterType(String name) {
            this.name = name;
        }
        public static class ReporterTypeAdapter extends TypeAdapter<ReporterType> {

            @Override
            public void write(JsonWriter out, ReporterType value) {
            }

            @Override
            public ReporterType read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    return null;
                }
                return ReporterType.values()[in.nextInt()];
            }
        }
    }

    public enum Reason {
        UNAUTHORIZED_PROGRAM("비인가 프로그램"), TERROR("테러"), FRAUD("사기"), HACKING("해킹"), DISPUTE("분쟁"),
        SPEECH("언행"), ETC("기타");

        public final String name;

        Reason(String name) {
            this.name = name;
        }

        public static class ReasonAdapter extends TypeAdapter<Reason> {

            @Override
            public void write(JsonWriter out, Reason value) {
            }

            @Override
            public Reason read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    return null;
                }
                return Reason.values()[in.nextInt()];
            }
        }
    }
}
