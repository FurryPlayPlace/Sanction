/*
---------------------------------------------------------------------------------
File Name : UnbanCommand

Developer : vakea
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.commands.ban;

import com.mojang.brigadier.context.CommandContext;
import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.cottonframework.api.command.AbstractCommand;
import net.furryplayplace.cottonframework.api.permissions.v1.CottonPermissions;
import net.furryplayplace.cottonframework.api.permissions.v1.Permissible;
import net.furryplayplace.cottonframework.api.permissions.v1.Permission;
import net.furryplayplace.sanction.Sanction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.UUID;

public class UnbanCommand extends AbstractCommand {

    private final Sanction plugin;

    public UnbanCommand(Sanction plugin) {
        super("unban", true, "moderation.unban");
        this.plugin = plugin;
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Text.literal("Usage:").formatted(Formatting.RED));
            sender.sendMessage(Text.literal("/" + this.getName() + " <player-id> <reason>").formatted(Formatting.RED));
            return 0;
        }

        UUID playerId = UUID.fromString(args[0]);
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.plugin.moderationManager().unbanPlayer(playerId, sender, reason);
        return 1;
    }
}