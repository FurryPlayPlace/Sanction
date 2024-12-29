/*
---------------------------------------------------------------------------------
File Name : PlayerListener

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.listeners.player;

import com.google.common.eventbus.Subscribe;
import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.cottonframework.api.events.player.PlayerChatMessageEvent;
import net.furryplayplace.cottonframework.api.events.player.PlayerJoinEvent;
import net.furryplayplace.sanction.Sanction;
import net.furryplayplace.sanction.api.dummy.SanctionDummy;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.format.DateTimeFormatter;

public class PlayerListener {

    private final Sanction plugin;

    public PlayerListener(Sanction plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onJoinCheck(PlayerJoinEvent event) {
        if (this.plugin.getConfig().getBoolean("listeners.onJoinCheck.enabled", false)) {
            if (this.plugin.moderationManager().isBanned(event.getPlayer().getUuid())) {
                SanctionDummy banInfo = this.plugin.moderationManager().getBan(event.getPlayer().getUuid());

                String formattedMessage = this.plugin.getConfig().getString("listeners.onJoinCheck.bannedMessage", "You are banned from %server% by %author% for %reason% until: %expiration%")
                        .replace("%server%", Formatting.GOLD + this.plugin.getConfig().getString("server-name", "FurryPlayPlace"))
                        .replace("%author%", Formatting.GOLD + banInfo.author())
                        .replace("%reason%", Formatting.GOLD + banInfo.reason())
                        .replace("%expiration%", Formatting.GOLD + banInfo.expiration().toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));

                ((ServerPlayerEntity) event.getPlayer()).networkHandler.disconnect(Text.literal(formattedMessage).formatted(Formatting.RED));
                this.plugin.getLogger().info(event.getPlayer().getDisplayName() + " joined the server but got disconnected due to active ban.");
            }
        }
    }

    @Subscribe
    public void onChatCheck(PlayerChatMessageEvent event) {
        if (this.plugin.getConfig().getBoolean("listeners.onChatCheck.enabled", false)) {
            if (this.plugin.moderationManager().isMuted(event.getPlayer().getUuid())) {
                SanctionDummy muteInfo = this.plugin.moderationManager().getMuteInfo(event.getPlayer().getUuid());

                event.getPlayer().sendMessage(Text.literal("You are currently muted:").formatted(Formatting.GRAY));
                event.getPlayer().sendMessage(Text.literal("Reason: ").formatted(Formatting.GRAY).append(muteInfo.reason()).formatted(Formatting.GOLD));
                event.getPlayer().sendMessage(Text.literal("Until: ").formatted(Formatting.GRAY).append(muteInfo.expiration().toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))).formatted(Formatting.GOLD));

                if (this.plugin.getConfig().getBoolean("listeners.onChatCheck.seeGhost", false)) {
                    CottonAPI.get().server().getPlayerManager().getPlayerList().stream()
                            .filter(this.plugin.moderationManager()::hasPermission)
                            .forEach(staff -> staff.sendMessage(Text.literal("[Ghost] ")
                                    .formatted(Formatting.GRAY).append(event.getPlayer().getName())
                                    .formatted(Formatting.GRAY).append(" ").append(event.getTextMessage())
                                    .formatted(Formatting.GRAY))
                            );
                }

                event.setCancelled(true);
            }
        }
    }
}