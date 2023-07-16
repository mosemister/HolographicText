package org.mose.holographictext.command;

import net.kyori.adventure.text.Component;
import org.mose.holographictext.HolographicText;
import org.mose.holographictext.HolographicTextPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;

public class EditTextCommand {

    public static Command.Parameterized createEditTextCommand() {
        return Command
                .builder()
                .permission(HolographicTextPermissions.EDIT_HOLOGRAM.permissionNode())
                .addChild(createEditTextCommand(Parameter.jsonTextOfRemainingElements().key("text").build()), "advanced")
                .addChild(createEditTextCommand(Parameter.formattingCodeTextOfRemainingElements().key("text").build()), "simple")
                .build();
    }

    private static Command.Parameterized createEditTextCommand(Parameter.Value<Component> parameter) {
        return Command
                .builder()
                .addParameter(parameter)
                .executor(context -> {
                    Entity armorstand = HolographicText.getHologramInLineOfSight(context);
                    armorstand.offer(Keys.CUSTOM_NAME, context.requireOne(parameter));
                    return CommandResult.success();
                })
                .build();
    }
}
