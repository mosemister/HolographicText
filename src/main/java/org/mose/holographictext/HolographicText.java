package org.mose.holographictext;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.mose.holographictext.command.CreateHologramCommand;
import org.mose.holographictext.command.EditTextCommand;
import org.mose.holographictext.command.MoveHologramCommand;
import org.mose.holographictext.command.RemoveHologramCommand;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.Arrays;
import java.util.Optional;

@Plugin("holographic_text")
public class HolographicText {

    private static HolographicText plugin;
    private final PluginContainer container;
    private final Logger logger;

    @Inject
    HolographicText(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
        plugin = this;
    }

    public static Entity getHologramInLineOfSight(CommandContext context) throws CommandException {
        Object root = context.cause().root();
        if (!(root instanceof Player)) {
            throw new CommandException(Component.text("Player only command"));
        }
        Player sender = (Player) root;
        return getHologramInLineOfSight(sender);
    }

    public static Entity getHologramInLineOfSight(Living living) throws CommandException {
        Optional<RayTraceResult<Entity>> opArmorstand = RayTrace
                .entity()
                .limit(7)
                .sourceEyePosition(living)
                .direction(living)
                .continueWhileEntity(HolographicText::isHologram)
                .execute();
        if (!opArmorstand.isPresent()) {
            throw new CommandException(Component.text("No holograms in line of sight. Make sure you look under the text"));
        }
        return opArmorstand.get().selectedObject();
    }

    public static boolean isHologram(Entity entity) {
        if (!entity.type().equals(EntityTypes.ARMOR_STAND.get())) {
            return false;
        }
        if (!entity.getValue(Keys.CUSTOM_NAME).isPresent()) {
            return false;
        }
        return entity.getValue(Keys.IS_CUSTOM_NAME_VISIBLE).isPresent();
    }

    public static HolographicText plugin() {
        return plugin;
    }

    public PluginContainer container() {
        return this.container;
    }

    @Listener
    public void onCommand(RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized joinedCommand = Command
                .builder()
                .addChild(CreateHologramCommand.createTextCommand(), "create")
                .addChild(RemoveHologramCommand.createRemoveCommand(), "remove")
                .addChild(MoveHologramCommand.createMoveCommand(), "move")
                .addChild(EditTextCommand.createEditTextCommand(), "edit")
                .build();


        event.register(container, joinedCommand, "holographictext", "ht");
    }

    @Listener
    public void onServerStarted(final StartedEngineEvent<Server> event) {
        Arrays.stream(HolographicTextPermissions.values()).forEach(HolographicTextPermissions::register);
    }
}
