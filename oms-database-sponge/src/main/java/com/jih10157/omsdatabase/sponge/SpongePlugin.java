package com.jih10157.omsdatabase.sponge;

import com.google.inject.Inject;
import com.jih10157.omsdatabase.core.Core;
import com.jih10157.omsdatabase.core.interpaces.Config;
import com.jih10157.omsdatabase.core.interpaces.Player;
import com.jih10157.omsdatabase.core.interpaces.Server;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.bstats.sponge.Metrics;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.serializer.TextSerializers;

@Plugin(id = "oms-database-sponge", name = "OmsDatabase", description = "우마공 DB")
public class SpongePlugin {

    private final Logger logger;
    private final Path defaultConfig;

    @Inject
    public SpongePlugin(Logger logger, @DefaultConfig(sharedRoot = true) Path defaultConfig, Metrics.Factory metrics) {
        this.logger = logger;
        this.defaultConfig = defaultConfig;
        metrics.make(14327);
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        Core core = new Core(new ImplServer(), new ImplConfig(this));
        Sponge.getEventManager().registerListeners(this, new EventListener(core.getEventListener()));
        Sponge.getCommandManager().register(this, new CommandListener(core.getCommandListener()), "omsdatabase", "omsdb");
    }

    private static final class ImplServer implements Server {

        @Override
        public void sendMessage(String message) {
            Sponge.getServer().getBroadcastChannel().send(
                TextSerializers.FORMATTING_CODE.deserialize(message));
        }

        @Override
        public List<Player> getAllPlayers() {
            return Sponge.getServer().getOnlinePlayers().stream()
                .map(ImplPlayer::new).collect(Collectors.toList());
        }

        @Override
        public Player getPlayer(String name) {
            return Sponge.getServer().getPlayer(name).map(ImplPlayer::new).orElse(null);
        }
    }

    private static final class ImplConfig implements Config {

        private final SpongePlugin instance;
        private final HoconConfigurationLoader configLoader;
        private CommentedConfigurationNode configNode;

        private ImplConfig(SpongePlugin instance) {
            this.instance = instance;
            this.configLoader = HoconConfigurationLoader.builder().setPath(instance.defaultConfig).build();
            reloadConfig();
        }

        @Override
        public String getString(String path) {
            return getNode(path).getString();
        }

        @Override
        public void setString(String path, String str) {
            getNode(path).setValue(str);
        }

        @Override
        public int getInt(String path) {
            return getNode(path).getInt();
        }

        @Override
        public void setInt(String path, int integer) {
            getNode(path).setValue(integer);
        }

        @Override
        public boolean getBoolean(String path) {
            return getNode(path).getBoolean();
        }

        @Override
        public void setBoolean(String path, boolean bool) {
            getNode(path).setValue(bool);
        }

        @Override
        public void reloadConfig() {
            CommentedConfigurationNode defaultConfig = null;
            Optional<Asset> asset = Sponge.getAssetManager().getAsset(instance, this.instance.defaultConfig.getFileName().toString());
            if (asset.isPresent()) {
                try {
                    asset.get().copyToFile(this.instance.defaultConfig, false, true);
                    defaultConfig = HoconConfigurationLoader.builder().setURL(asset.get().getUrl()).build().load();
                } catch (IOException e) {
                    this.instance.logger.error("기본 설정 파일을 불러오는데 실패하였습니다.", e);
                }
            }
            try {
                this.configNode = configLoader.load();
            } catch (IOException e) {
                this.instance.logger.error("설정을 불러오는데 실패하였습니다.", e);
                this.configNode = CommentedConfigurationNode.root();
            }
            if (defaultConfig != null) {
                this.configNode.mergeValuesFrom(defaultConfig);
            }
        }

        private CommentedConfigurationNode getNode(String path) {
            return this.configNode.getNode(Arrays.stream(path.split("\\.")).iterator());
        }
    }

    static class ImplPlayer implements Player {

        private final org.spongepowered.api.entity.living.player.Player player;
        private final User user;

        public ImplPlayer(org.spongepowered.api.entity.living.player.Player player) {
            this.player = player;
            this.user = null;
        }

        public ImplPlayer(User user) {
            this.player = null;
            this.user = user;
        }

        @Override
        public UUID getUUID() {
            if (this.player != null) {
                return this.player.getUniqueId();
            } else if (this.user != null) {
                return this.user.getUniqueId();
            } else {
                return null;
            }
        }

        @Override
        public String getName() {
            if (this.player != null) {
                return this.player.getName();
            } else if (this.user != null) {
                return this.user.getName();
            } else {
                return null;
            }
        }

        @Override
        public boolean hasPermission(String perm) {
            if (this.player != null) {
                return this.player.hasPermission(perm);
            } else if (this.user != null) {
                return this.user.hasPermission(perm);
            } else {
                return false;
            }
        }

        @Override
        public void sendMessage(String message) {
            if (this.player != null) {
                this.player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));
            }
        }
    }
}
