package com.example.commands;

import com.example.TestPlugin;
import com.mojang.brigadier.context.CommandContext;
import net.furryplayplace.cottonframework.api.command.AbstractCommand;
import net.furryplayplace.cottonframework.api.permissions.v1.CottonPermissions;
import net.furryplayplace.cottonframework.api.permissions.v1.Permissible;
import net.furryplayplace.cottonframework.api.permissions.v1.Permission;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ExampleCommand extends AbstractCommand {

    private final TestPlugin plugin;

    public ExampleCommand(TestPlugin plugin) {
        super("spawn", true, "player.spawn");
        this.plugin = plugin;
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity sender, String[] args) {
        double x = this.plugin.getConfig().getDouble("spawn.x");
        double y = this.plugin.getConfig().getDouble("spawn.y");
        double z = this.plugin.getConfig().getDouble("spawn.z");

        return sender.teleport(x, y, z, true) ? 1 : 0;
    }

    @Override
    public boolean test(ServerCommandSource serverCommandSource) {
        Permissible permissible = CottonPermissions.getPermissible(serverCommandSource.getPlayer());
        if (permissible == null)
            return false;

        // You can use this system or use the classic
        // serverCommandSource.hasPermissionLevel(4) / serverCommandSource.hasPermissionLevel(3)

        return permissible.hasPermission(Permission.of(this.getPermission(), "A Example Permission"));
    }
}