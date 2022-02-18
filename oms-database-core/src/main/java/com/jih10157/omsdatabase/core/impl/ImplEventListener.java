package com.jih10157.omsdatabase.core.impl;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jih10157.omsdatabase.api.OmsDatabase;
import com.jih10157.omsdatabase.api.OmsFailException;
import com.jih10157.omsdatabase.api.response.ReportCountOfUUID;
import com.jih10157.omsdatabase.core.Core;
import com.jih10157.omsdatabase.core.interpaces.Config;
import com.jih10157.omsdatabase.core.interpaces.EventListener;
import com.jih10157.omsdatabase.core.interpaces.Player;
import com.jih10157.omsdatabase.core.util.Message;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ImplEventListener implements EventListener {

    private final Core core;
    private final OmsDatabase database;
    private final Config config;
    private final AsyncLoadingCache<UUID, Integer> cache;

    public ImplEventListener(Core core) {
        this.core = core;
        this.database = core.getOmsDatabase();
        this.config = core.getConfig();
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .buildAsync(key -> {
                Future<ReportCountOfUUID> countFuture = database.getReportCount(key);
                try {
                    return countFuture.get().count;
                } catch (InterruptedException | ExecutionException e) {
                    if (e.getCause() instanceof OmsFailException) {
                        OmsFailException fail = (OmsFailException) e.getCause();
                        if (fail.code == 404) {
                            return 0;
                        }
                    }
                    throw e;
                }
            });
    }

    @Override
    public void onJoin(Player player) {
        if (!config.getBoolean("join-check.enable") ||
            player.hasPermission("omsdatabase.bypass.join-check")) {
            return;
        }
        this.cache.get(player.getUUID()).thenAccept((count) -> {
            if (count < config.getInt("join-check.condition")) {
                return;
            }
            String message = Message.formatMessage(
                config.getString("join-check.message"),
                player.getUUID().toString().replace("-", ""),
                player.getName(), count
            );
            core.getServer().getAllPlayers().forEach((all -> {
                if (all.hasPermission("omsdatabase.notification")) {
                    all.sendMessage(message);
                }
            }));
        }).exceptionally(e -> {
            core.getServer().error(player.getName() + "님의 제보 개수를 불러오는 중 예외가 발생하였습니다.", e);
            return null;
        });
    }

    @Override
    public void onConnect(Player player, Consumer<String> kick, Consumer<String> disallow) {
        if (!config.getBoolean("ban.enable") ||
            player.hasPermission("omsdatabase.bypass.ban")) {
            return;
        }
        CompletableFuture<Integer> future = this.cache.get(player.getUUID());
        if (config.getBoolean("ban.do-sync") || future.isDone()) {
            try {
                int count = future.get();
                if (count >= config.getInt("ban.condition")) {
                    disallow.accept(Message.formatMessage(
                        config.getString("ban.ban-message"),
                        player.getUUID().toString().replace("-", ""),
                        player.getName(),
                        count
                    ));
                }
            } catch (InterruptedException | ExecutionException e) {
                core.getServer().error(player.getName() + "님의 제보 개수를 불러오는 중 예외가 발생하였습니다.", e);
            }
        } else {
            future.thenAccept(count -> {
                if (count >= config.getInt("ban.condition")) {
                    core.getServer().doSync(() -> kick.accept(Message.formatMessage(
                        config.getString("ban.ban-message"),
                        player.getUUID().toString().replace("-", ""),
                        player.getName(),
                        count
                    )));
                }
            }).exceptionally(e -> {
                core.getServer().error(player.getName() + "님의 제보 개수를 불러오는 중 예외가 발생하였습니다.", e);
                return null;
            });
        }
    }
}
