package org.mose.holographictext.command;

import org.mose.holographictext.HolographicText;
import org.mose.holographictext.HolographicTextPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;

public class RemoveHologramCommand {

    public static Command.Parameterized createRemoveCommand() {
        return Command.builder().permission(HolographicTextPermissions.CREATE_HOLOGRAM.permissionNode())
                .executor((context) -> {
                    HolographicText.getHologramInLineOfSight(context).remove();
                    return CommandResult.success();
                }).build();
    }
}
