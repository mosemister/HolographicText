package org.mose.holographictext.command;

import org.mose.holographictext.HolographicText;
import org.mose.holographictext.HolographicTextPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.math.vector.Vector3d;

import java.util.function.Function;

public class MoveHologramCommand {

    public static Command.Parameterized createMoveCommand() {
        Parameter.Value<Vector3d> positionParameter = Parameter.vector3d().key("position").build();

        Command.Parameterized exact = Command
                .builder()
                .executor((context) -> execute(context, entity -> context.requireOne(positionParameter)))
                .addParameter(positionParameter)
                .build();

        Command.Parameterized relativeTo = Command
                .builder()
                .executor((context) -> execute(context, entity -> entity.position().add(context.requireOne(positionParameter))))
                .addParameter(positionParameter)
                .build();

        return Command
                .builder()
                .permission(HolographicTextPermissions.EDIT_HOLOGRAM.permissionNode())
                .addChild(exact, "exact")
                .addChild(relativeTo, "relative", "add")
                .build();
    }

    private static CommandResult execute(CommandContext context, Function<Entity, Vector3d> toPosition) throws CommandException {
        Entity moving = HolographicText.getHologramInLineOfSight(context);
        moving.setPosition(toPosition.apply(moving));
        return CommandResult.success();
    }
}
