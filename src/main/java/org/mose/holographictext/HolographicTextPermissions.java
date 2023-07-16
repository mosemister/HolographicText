package org.mose.holographictext;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ServiceRegistration;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

public enum HolographicTextPermissions {

    CREATE_HOLOGRAM(Component.text("Allows the ability to create and remove holograms"), "holographictext.create"),
    EDIT_HOLOGRAM(Component.text("Allows the ability to edit the holograms"), "holographictext.edit");

    private final String permissionNode;
    private final Tristate defaultValue;
    private final Component description;
    private PermissionDescription registered;

    HolographicTextPermissions(Component description, String permissionNode) {
        this(Tristate.UNDEFINED, description, permissionNode);
    }

    HolographicTextPermissions(Tristate defaultIf, Component description, String permissionNode) {
        this.permissionNode = permissionNode;
        this.defaultValue = defaultIf;
        this.description = description;
    }

    public Optional<PermissionDescription> register() {
        Optional<ServiceRegistration<PermissionService>> opService = Sponge.serviceProvider().registration(PermissionService.class);
        if (!opService.isPresent()) {
            return Optional.empty();
        }
        PermissionService service = opService.get().service();
        this.registered = service.newDescriptionBuilder(HolographicText.plugin().container())
                .id(permissionNode)
                .defaultValue(this.defaultValue)
                .description(this.description)
                .register();
        return Optional.of(this.registered);
    }

    public Optional<PermissionDescription> permissionDescription() {
        return Optional.ofNullable(this.registered);
    }

    public Component description() {
        return this.description;
    }

    public String permissionNode() {
        return this.permissionNode;
    }

    public Tristate onByDefault() {
        return this.defaultValue;
    }
}
