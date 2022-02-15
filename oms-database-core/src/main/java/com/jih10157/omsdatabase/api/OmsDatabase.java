package com.jih10157.omsdatabase.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jih10157.omsdatabase.api.adapter.InstantAdapter;
import com.jih10157.omsdatabase.api.adapter.UUIDAdapter;
import com.jih10157.omsdatabase.api.object.Report.Reason;
import com.jih10157.omsdatabase.api.object.Report.Reason.ReasonAdapter;
import com.jih10157.omsdatabase.api.object.Report.ReporterType;
import com.jih10157.omsdatabase.api.object.Report.ReporterType.ReporterTypeAdapter;
import com.jih10157.omsdatabase.api.response.ReportAll;
import com.jih10157.omsdatabase.api.response.ReportCountOfUUID;
import com.jih10157.omsdatabase.api.response.ReportsOfNickname;
import com.jih10157.omsdatabase.api.response.ReportsOfUUID;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OmsDatabase {

    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantAdapter())
        .registerTypeAdapter(ReporterType.class, new ReporterTypeAdapter())
        .registerTypeAdapter(Reason.class, new ReasonAdapter())
        .registerTypeAdapter(UUID.class, new UUIDAdapter())
        .create();
    private final JsonParser parser = new JsonParser();

    public CompletableFuture<ReportAll> getReports() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String json = ApiConnection.getString("report/all");
                JsonObject object = parser.parse(json).getAsJsonObject();
                if (!object.get("success").getAsBoolean()) {
                    throw new OmsFailException(object.get("code").getAsInt(),
                        object.get("reason").toString());
                }
                return gson.fromJson(object.get("value"), ReportAll.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<ReportsOfNickname> getReportsByNickname(String nickname, int skip,
        int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String json = ApiConnection
                    .getString("report/search/" + nickname + "?skip=" + skip + "&limit=" + limit);
                JsonObject object = parser.parse(json).getAsJsonObject();
                if (!object.get("success").getAsBoolean()) {
                    throw new OmsFailException(object.get("code").getAsInt(),
                        object.get("reason").toString());
                }
                return gson.fromJson(object.get("value"), ReportsOfNickname.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<ReportsOfNickname> getReportsByNickname(String nickname) {
        return getReportsByNickname(nickname, 0, 100);
    }

    public CompletableFuture<ReportCountOfUUID> getReportCount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String json = ApiConnection
                    .getString("report/reportcount/" + uuid.toString().replace("-", ""));
                JsonObject object = parser.parse(json).getAsJsonObject();
                if (!object.get("success").getAsBoolean()) {
                    throw new OmsFailException(object.get("code").getAsInt(),
                        object.get("reason").toString());
                }
                return gson.fromJson(object.get("value"), ReportCountOfUUID.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<ReportsOfUUID> getReportsByUUID(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String json = ApiConnection
                    .getString("report/user/" + uuid.toString().replace("-", ""));
                JsonObject object = parser.parse(json).getAsJsonObject();
                if (!object.get("success").getAsBoolean()) {
                    throw new OmsFailException(object.get("code").getAsInt(),
                        object.get("reason").toString());
                }
                return gson.fromJson(object.get("value"), ReportsOfUUID.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
