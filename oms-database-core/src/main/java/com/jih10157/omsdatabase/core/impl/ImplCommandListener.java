package com.jih10157.omsdatabase.core.impl;

import com.jih10157.omsdatabase.api.OmsDatabase;
import com.jih10157.omsdatabase.api.OmsFailException;
import com.jih10157.omsdatabase.api.object.Report;
import com.jih10157.omsdatabase.api.response.ReportsOfNickname;
import com.jih10157.omsdatabase.api.response.ReportsOfUUID;
import com.jih10157.omsdatabase.core.Core;
import com.jih10157.omsdatabase.core.interpaces.CommandListener;
import com.jih10157.omsdatabase.core.interpaces.Player;
import com.jih10157.omsdatabase.core.interpaces.Sendable;
import com.jih10157.omsdatabase.core.interpaces.Server;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class ImplCommandListener implements CommandListener {

    Pattern pattern = Pattern.compile("[\\da-fA-F]{8}-([\\da-fA-F]{4}-){3}[\\da-fA-F]{12}");

    private final Core core;
    private final OmsDatabase database;
    private final Server server;

    public ImplCommandListener(Core core) {
        this.core = core;
        this.database = core.getOmsDatabase();
        this.server = core.getServer();
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        execute(player, args);
    }

    @Override
    public void onCommandByConsole(String[] args) {
        execute(server, args);
    }

    private void execute(Sendable sendable, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            core.getConfig().reloadConfig();
            sendable.sendMessage("&e설정 파일을 다시 불러왔습니다.");
            return;
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("get")) {
                String arg2 = args[1];
                int page;
                if (args.length == 3) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sendable.sendMessage("&c'" + args[2] + "'는 숫자가 아닙니다.");
                        return;
                    }
                } else {
                    page = 1;
                }
                UUID uuid;
                if (pattern.matcher(arg2).matches()) {
                    uuid = UUID.fromString(arg2);
                } else if (server.getPlayer(arg2) != null) {
                    uuid = server.getPlayer(arg2).getUUID();
                } else {
                    sendable.sendMessage("&a해당 닉네임의 제보를 가져오고 있습니다.");
                    get(arg2, page * 5 - 5, 5).thenAccept(reports -> {
                        if (reports.count == 0) {
                            sendable.sendMessage("&a해당 닉네임의 제보가 없습니다.");
                            return;
                        } else if (reports.list.size() == 0) {
                            sendable.sendMessage("&c해당 페이지가 없습니다.");
                            return;
                        }
                        sendable.sendMessage(
                            "&a" + arg2 + "님의 제보 갯수: " + reports.count + "개 (페이지 " + page + "/" + (
                                reports.count / 5 + 1) + ")");
                        for (int i = 1; i <= reports.list.size(); i++) {
                            Report report = reports.list.get(i - 1);
                            sendable.sendMessage(
                                "&a" + i + ". 날짜: " + formatInstant(report.reportDate));
                            sendable.sendMessage("&a-  제보 종류: " + report.reason.name);
                            sendable.sendMessage(
                                "&a-  제보 서버: " + report.serverName + " (" + report.serverAddress
                                    + ")");
                            sendable.sendMessage("&a-  제보자 직위: " + report.reporterType.name);
                            sendable.sendMessage(
                                "&a-  자세한 내용: https://userdb.ourmc.space/user/" + report.user.uuid
                                    .toString().replace("-", "")
                                    + "#" + report.id);
                        }
                    }).exceptionally((throwable -> {
                        if (throwable.getCause() instanceof OmsFailException) {
                            OmsFailException ex = (OmsFailException) throwable.getCause();
                            if (ex.code == 404) {
                                sendable.sendMessage("&a해당 닉네임의 제보가 없습니다.");
                            } else {
                                sendable.sendMessage(
                                    "&c" + arg2 + "님의 제보를 불러오는 중 예외가 발생하였습니다. 코드: " + ex.code);
                            }
                        } else {
                            throwable.printStackTrace();
                            server.sendMessage("&c" + arg2 + "님의 제보를 불러오는 중 에외가 발생하였습니다.");
                            sendable.sendMessage("&c" + arg2 + "님의 제보를 불러오는 중 에외가 발생하였습니다.");
                        }
                        return null;
                    }));
                    return;
                }
                sendable.sendMessage("&a해당 UUID의 제보를 가져오고 있습니다.");
                get(uuid).thenAccept(reports -> {
                    if (reports.count == 0) {
                        sendable.sendMessage("&a해당 UUID의 제보가 없습니다.");
                        return;
                    }
                    if (page > reports.count / 5 + 1) {
                        sendable.sendMessage("&c해당 페이지가 없습니다.");
                        return;
                    }
                    sendable.sendMessage(
                        "&a해당 UUID의 제보 갯수: " + reports.count + "개 (페이지 " + page + "/" + (
                            reports.count / 5 + 1) + ")");
                    for (int i = page * 5 - 4; i <= Math.min(page * 5, reports.list.size()); i++) {
                        Report report = reports.list.get(i - 1);
                        sendable.sendMessage(
                            "&a" + i + ". 날짜: " + formatInstant(report.reportDate));
                        sendable.sendMessage("&a-  제보 당시 닉네임: " + report.user.userName);
                        sendable.sendMessage("&a-  제보 종류: " + report.reason.name);
                        sendable.sendMessage(
                            "&a-  제보 서버: " + report.serverName + " (" + report.serverAddress
                                + ")");
                        sendable.sendMessage("&a-  제보자 직위: " + report.reporterType.name);
                        sendable.sendMessage(
                            "&a-  자세한 내용: https://userdb.ourmc.space/user/" + report.user.uuid
                                .toString().replace("-", "") + "#" + report.id);
                    }
                }).exceptionally((throwable -> {
                    if (throwable.getCause() instanceof OmsFailException) {
                        OmsFailException ex = (OmsFailException) throwable.getCause();
                        if (ex.code == 404) {
                            sendable.sendMessage("&a해당 UUID의 제보가 없습니다.");
                        } else {
                            sendable.sendMessage(
                                "&c" + arg2 + "님의 제보를 불러오는 중 예외가 발생하였습니다. 코드: " + ex.code);
                        }
                    } else {
                        throwable.printStackTrace();
                        server.sendMessage("&c" + arg2 + "님의 제보를 불러오는 중 에외가 발생하였습니다.");
                        sendable.sendMessage("&c" + arg2 + "님의 제보를 불러오는 중 에외가 발생하였습니다.");
                    }
                    return null;
                }));
            }
        } else {
            sendable.sendMessage("/omsdb get <username/uuid> [페이지]");
            sendable.sendMessage("- 서버에 접속해있는 플레이어의 닉네임은 해당 플레이어의");
            sendable.sendMessage("- UUID로 검색하게 되며, 접속해 있지 않은 경우 닉네임으로");
            sendable.sendMessage("- 검색합니다.");
            sendable.sendMessage("/omsdb reload");
            sendable.sendMessage("- 설정 파일을 다시 불러옵니다.");
        }
    }

    private CompletableFuture<ReportsOfUUID> get(UUID uuid) {
        return database.getReportsByUUID(uuid);
    }

    private CompletableFuture<ReportsOfNickname> get(String name, int skip, int limit) {
        return database.getReportsByNickname(name, skip, limit);
    }

    private String formatInstant(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.KOREA));
    }
}
