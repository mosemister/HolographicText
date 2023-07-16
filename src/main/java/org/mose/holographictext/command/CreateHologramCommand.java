package org.mose.holographictext.command;

import net.kyori.adventure.text.Component;
import org.mose.holographictext.HolographicText;
import org.mose.holographictext.HolographicTextPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;


public class CreateHologramCommand {

    public static Command.Parameterized createTextCommand() {
        return Command
                .builder()
                .permission(HolographicTextPermissions.CREATE_HOLOGRAM.permissionNode())
                .addChild(createTextCommand(Parameter.formattingCodeTextOfRemainingElements().key("display").build()), "simple")
                .addChild(createTextCommand(Parameter.jsonTextOfRemainingElements().key("display").build()), "advanced")
                .build();

    }

    private static Command.Parameterized createTextCommand(Parameter.Value<Component> textParameter) {
        return Command.builder().addParameter(textParameter).executor((context) -> {
            Optional<Vector3d> opRotation = context.cause().rotation();
            ServerLocation location = context.cause().location().map(loc -> loc.add(0, -1, 0)).orElseThrow(() -> new CommandException(Component.text("Location is required on the command sender")));
            if (opRotation.isPresent()) {
                Optional<RayTraceResult<LocatableBlock>> opResult = Optional.empty();
                if (context.cause().root() instanceof Living) {
                    Living living = (Living) context.cause().root();
                    opResult = RayTrace
                            .block()
                            .sourceEyePosition(living)
                            .direction(living)
                            .world(location.world())
                            .limit(7)
                            .continueWhileBlock(RayTrace.onlyAir())
                            .execute();
                } else {
                    opResult = RayTrace
                            .block()
                            .direction(opRotation.get())
                            .sourcePosition(location.position())
                            .world(location.world())
                            .limit(7)
                            .continueWhileBlock(RayTrace.onlyAir())
                            .execute();
                }
                Optional<ServerLocation> opLocation = opResult.flatMap(result -> result.selectedObject().location().onServer());
                if (opLocation.isPresent()) {
                    location = opLocation.get();
                }
            }


            ArmorStand stand = location.world().createEntity(EntityTypes.ARMOR_STAND, location.position());
            stand.offer(Keys.CUSTOM_NAME, context.requireOne(textParameter));
            stand.offer(Keys.IS_CUSTOM_NAME_VISIBLE, true);
            stand.offer(Keys.IS_GRAVITY_AFFECTED, false);
            stand.offer(Keys.IS_INVISIBLE, true);
            int attempts = 0;
            while (attempts < 4 && !location.world().spawnEntity(stand)) {
                attempts++;
                stand.setPosition(stand.position().add(0, 1, 0));
            }
            if (attempts >= 4) {
                return CommandResult.error(Component.text("Could not create hologram"));
            }

            return CommandResult.success();
        }).build();
    }
}
