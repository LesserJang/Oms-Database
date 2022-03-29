package com.jih10157.omsdatabase.sponge;

import com.jih10157.omsdatabase.sponge.SpongePlugin.ImplPlayer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandListener implements CommandCallable {

    private final com.jih10157.omsdatabase.core.interpaces.CommandListener comListener;

    public CommandListener(
        com.jih10157.omsdatabase.core.interpaces.CommandListener commandListener) {
        this.comListener = commandListener;
    }

    @Override
    @NonNull
    public CommandResult process(@NonNull CommandSource source, @NonNull String arguments) {
        if (!source.hasPermission("omsdatabase.usage")) {
            source.sendMessage(Text.of("§c당신은 권한이 없습니다!"));
            return CommandResult.success();
        }
        if (source instanceof Player) {
            comListener.onCommandByPlayer(new ImplPlayer((Player) source), arguments.split(" "));
        } else {
            comListener.onCommandByConsole(arguments.split(" "));
        }
        return CommandResult.success();
    }

    @Override
    @NonNull
    public List<String> getSuggestions(@NonNull CommandSource source, @NonNull String arguments,
        @Nullable Location<World> targetPosition) {
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(@NonNull CommandSource source) {
        return source.hasPermission("omsdatabase.usage");
    }

    @Override
    @NonNull
    public Optional<Text> getShortDescription(@NonNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    @NonNull
    public Optional<Text> getHelp(@NonNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    @NonNull
    public Text getUsage(@NonNull CommandSource source) {
        return Text.of();
    }
}
