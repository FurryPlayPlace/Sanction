/*
---------------------------------------------------------------------------------
File Name : UnmuteCommand

Developer : vakea
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.commands.mute;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UnmuteCommand extends AbstractCommand {

    private final Sanction plugin;

    public UnmuteCommand(Sanction plugin) {
        super("unmute", true, "moderation.unmute");
        this.plugin = plugin;
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Text.literal("Usage:").formatted(Formatting.RED));
            sender.sendMessage(Text.literal("/" + this.getName() + " <player-id> <reason>").formatted(Formatting.RED));
            return 0;
        }

        PlayerEntity target = CottonAPI.get().server().getPlayerManager().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Text.literal("The player is offline.").formatted(Formatting.RED));
            return 0;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.plugin.moderationManager().unmutePlayer(target.getUuid(), sender, reason);
        return 1;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = commandContext.getSource().getServer().getPlayerManager().getPlayerList();

        for (ServerPlayerEntity player : players) {
            suggestionsBuilder.suggest(player.getName().getString());
        }

        return suggestionsBuilder.buildFuture();
    }
}