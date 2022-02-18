package com.jih10157.omsdatabase.core.impl;

import com.jih10157.omsdatabase.api.OmsDatabase;
import com.jih10157.omsdatabase.api.OmsFailException;
import com.jih10157.omsdatabase.api.response.ReportCountOfUUID;
import com.jih10157.omsdatabase.core.Core;
import com.jih10157.omsdatabase.core.interpaces.Config;
import com.jih10157.omsdatabase.core.interpaces.EventListener;
import com.jih10157.omsdatabase.core.interpaces.Player;
import com.jih10157.omsdatabase.core.util.Message;
import java.util.concurrent.ExecutionException;

public class ImplEventListener implements EventListener {

    private final Core core;
    private final OmsDatabase database;
    private final Config config;

    public ImplEventListener(Core core) {
        this.core = core;
        this.database = core.getOmsDatabase();
        this.config = core.getConfig();
    }

    @Override
    public void onJoin(Player player) {
        if (!config.getBoolean("join-check.enable") ||
            player.hasPermission("omsdatabase.bypass.join-check")) {
            return;
        }
        database.getReportCount(player.getUUID()).thenAccept((count) -> {
            if (count.count < config.getInt("join-check.condition")) {
                return;
            }
            String message = Message.formatMessage(
                config.getString("join-check.message"),
                player.getUUID().toString().replace("-", ""),
                player.getName(),
                count.count
            );
            core.getServer().getAllPlayers().forEach((all -> {
                if (all.hasPermission("omsdatabase.notification")) {
                    all.sendMessage(message);
                }
            }));
        }).exceptionally(throwable -> {
            if (throwable.getCause() instanceof OmsFailException
                && ((OmsFailException) throwable.getCause()).code == 404) {
                if (0 >= config.getInt("join-check.condition")) {
                    String message = Message.formatMessage(
                        config.getString("join-check.message"),
                        player.getUUID().toString().replace("-", ""),
                        player.getName(),
                        0
                    );
                    core.getServer().getAllPlayers().forEach((all -> {
                        if (all.hasPermission("omsdatabase.notification")) {
                            all.sendMessage(message);
                        }
                    }));
                }
            } else {
                core.getServer()
                    .error(player.getName() + "님의 제보 개수를 불러오는 중 예외가 발생하였습니다.", throwable);
            }
            return null;
        });
    }

    @Override
    public String onConnect(Player player) {
        if (!config.getBoolean("ban.enable") ||
            player.hasPermission("omsdatabase.bypass.ban")) {
            return null;
        }
        try {
            ReportCountOfUUID reportCount = database.getReportCount(player.getUUID()).get();
            if (reportCount.count < config.getInt("ban.condition")) {
                return null;
            }
            return Message.formatMessage(
                config.getString("ban.ban-message"),
                player.getUUID().toString().replace("-", ""),
                player.getName(),
                reportCount.count
            );
        } catch (ExecutionException | InterruptedException throwable) {
            if (!(throwable.getCause() instanceof OmsFailException)
                || ((OmsFailException) throwable.getCause()).code != 404) {
                core.getServer()
                    .error(player.getName() + "님의 제보 개수를 불러오는 중 예외가 발생하였습니다.", throwable);
            }
            return null;
        }
    }
}
