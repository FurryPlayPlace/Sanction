/*
---------------------------------------------------------------------------------
File Name : HistoryCommand

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.cottonframework.api.command.AbstractCommand;
import net.furryplayplace.sanction.Sanction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class HistoryCommand extends AbstractCommand {

    private final Sanction plugin;

    public HistoryCommand(Sanction plugin) {
        super("history", true, "moderation.history");
        this.plugin = plugin;
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Text.literal("Usage:").formatted(Formatting.RED));
            sender.sendMessage(Text.literal("/" + this.getName() + " <player> <sanction-type>").formatted(Formatting.RED));
            return 0;
        }

        PlayerEntity target = CottonAPI.get().server().getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Text.literal("The player is offline.").formatted(Formatting.RED));
            return 0;
        }

        return switch (args[1]) {
            case "bans" -> this.handleBanHistory(target);
            case "kicks" -> this.handleKickHistory(target);
            case "warns" -> this.handleWarnHistory(target);
            case "mute" -> this.handleMuteHistory(target);
            default -> this.handleDefault(sender);
        };
    }

    private int handleBanHistory(PlayerEntity player) {
        player.sendMessage(Text.literal("Not Yet Implemented").formatted(Formatting.RED));

        return 0;
    }

    private int handleKickHistory(PlayerEntity player) {
        player.sendMessage(Text.literal("Not Yet Implemented").formatted(Formatting.RED));

        return 0;
    }

    private int handleWarnHistory(PlayerEntity player) {
        player.sendMessage(Text.literal("Not Yet Implemented").formatted(Formatting.RED));

        return 0;
    }

    private int handleMuteHistory(PlayerEntity player) {
        player.sendMessage(Text.literal("Not Yet Implemented").formatted(Formatting.RED));

        return 0;
    }

    public int handleDefault(PlayerEntity player) {
        player.sendMessage(Text.literal("Invalid sanction type: (ban, kick, warn, mute)").formatted(Formatting.RED));

        return 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = commandContext.getSource().getServer().getPlayerManager().getPlayerList();

        for (ServerPlayerEntity player : players) {
            suggestionsBuilder.createOffset(0).suggest(player.getName().getString());
        }

        suggestionsBuilder.createOffset(1).suggest("ban");
        suggestionsBuilder.createOffset(1).suggest("kick");
        suggestionsBuilder.createOffset(1).suggest("warn");
        suggestionsBuilder.createOffset(1).suggest("mute");

        return suggestionsBuilder.buildFuture();
    }
}