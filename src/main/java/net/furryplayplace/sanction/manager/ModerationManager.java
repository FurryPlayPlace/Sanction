/*
---------------------------------------------------------------------------------
File Name : ModerationManager

Developer : vakea 
Email     : vakea@fluffici.eu
Real Name : Alex Guy Yann Le Roy

Date Created  : 29.12.2024
Last Modified : 29.12.2024

---------------------------------------------------------------------------------
*/

package net.furryplayplace.sanction.manager;

import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.cottonframework.api.permissions.v1.CottonPermissions;
import net.furryplayplace.cottonframework.api.permissions.v1.Permissible;
import net.furryplayplace.cottonframework.api.permissions.v1.Permission;
import net.furryplayplace.sanction.Sanction;
import net.furryplayplace.sanction.api.PerformAction;
import net.furryplayplace.sanction.api.dummy.SanctionDummy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModerationManager {
    public final String MODERATION_TAG = Formatting.DARK_PURPLE + " " + Formatting.BOLD + "~ Moderation ~" + Formatting.RESET + " ";
    public final Permission seeStaffBroadcast = Permission.of("sanction.staff.broadcast", "Sanction Permission");
    private final Sanction plugin;

    /**
     * Constructor that initializes the ModerationManager with the given plugin.
     *
     * @param plugin The {@link Sanction} plugin instance used for database access and logging.
     */
    public ModerationManager(Sanction plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a player is banned.
     *
     * @param player The {@link UUID} of the player to check.
     * @return {@code true} if the player is banned, {@code false} otherwise.
     */
    public boolean isBanned(UUID player) {
        AtomicBoolean isBanned = new AtomicBoolean(false);

        this.queryPerformer("SELECT * FROM sanctions WHERE player_uuid = ? AND type_id = 2 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> isBanned.set(resultSet.next()));

        return isBanned.get();
    }

    /**
     * Checks if a player is muted.
     *
     * @param player The {@link UUID} of the player to check.
     * @return {@code true} if the player is muted, {@code false} otherwise.
     */
    public boolean isMuted(UUID player) {
        AtomicBoolean isMuted = new AtomicBoolean(false);

        this.queryPerformer("SELECT * FROM sanctions WHERE player_uuid = ? AND type_id = 1 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> isMuted.set(resultSet.next()));

        return isMuted.get();
    }

    public SanctionDummy getBan(UUID player) {
        AtomicReference<SanctionDummy> ban = new AtomicReference<>(null);

        this.queryPerformer("SELECT * FROM sanctions WHERE player_uuid = ? AND type_id = 2 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> {
            if (resultSet.next()) {
                ban.set(new SanctionDummy(
                        resultSet.getString("reason"),
                        resultSet.getString("punisher_uuid"),
                        resultSet.getTimestamp("expiration_date")
                ));
            }
        });

        return ban.get();
    }

    public SanctionDummy getMuteInfo(UUID player) {
        AtomicReference<SanctionDummy> ban = new AtomicReference<>(null);

        this.queryPerformer("SELECT * FROM sanctions WHERE player_uuid = ? AND type_id = 1 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> {
            if (resultSet.next()) {
                ban.set(new SanctionDummy(
                        resultSet.getString("reason"),
                        resultSet.getString("punisher_uuid"),
                        resultSet.getTimestamp("expiration_date")
                ));
            }
        });

        return ban.get();
    }

    /**
     * Checks if a player has an active warning.
     *
     * @param player The {@link UUID} of the player to check.
     * @return {@code true} if the player has an active warning, {@code false} otherwise.
     */
    public boolean hasActiveWarn(UUID player) {
        AtomicBoolean hasActiveWarn = new AtomicBoolean(false);

        this.queryPerformer("SELECT * FROM sanctions WHERE player_uuid = ? AND type_id = 4 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> hasActiveWarn.set(resultSet.next()));

        return hasActiveWarn.get();
    }

    /**
     * Returns the total number of warnings issued to a player.
     *
     * @param player The {@link UUID} of the player to check.
     * @return The total number of warnings issued to the player.
     */
    public int warnCount(UUID player) {
        AtomicInteger hasActiveWarn = new AtomicInteger(0);

        this.queryPerformer("SELECT COUNT(*) as total_warns FROM sanctions WHERE player_uuid = ? AND type_id = 4 AND is_deleted = 0", Map.of(
                1, player.toString().replaceAll("-", "")
        ), resultSet -> {
            if (resultSet.next()) {
                hasActiveWarn.set(resultSet.getInt("total_warns"));
            }
        });

        return hasActiveWarn.get();
    }

    /**
     * Bans a player from the server.
     *
     * @param player     The {@link PlayerEntity} to be banned.
     * @param reason     The reason for the ban.
     * @param author     The {@link PlayerEntity} who is authorizing the ban.
     * @param expiration The expiration timestamp for the ban.
     */
    public void banPlayer(PlayerEntity player, String reason, PlayerEntity author, Timestamp expiration) {
        ((ServerPlayerEntity) player).networkHandler.disconnect(Text.literal(this.MODERATION_TAG).append("You got banned from FurryPlayPlace. Reason: ").formatted(Formatting.GRAY).append(Text.of(reason)).formatted(Formatting.GOLD));

        this.applySanction(player, 2, reason, author, expiration);
        this.broadcastSanction("ban", player, reason, author, expiration);
    }

    /**
     * Unbans a player.
     *
     * @param player The {@link UUID} of the player to unban.
     * @param author The {@link PlayerEntity} who is authorizing the unban.
     * @param reason The reason for the unban.
     */
    public void unbanPlayer(UUID player, PlayerEntity author, String reason) {
        this.waiveSanction(player, 2, reason, author);
    }

    /**
     * Mutes a player on the server.
     *
     * @param player     The {@link PlayerEntity} to mute.
     * @param reason     The reason for the mute.
     * @param author     The {@link PlayerEntity} who is authorizing the mute.
     * @param expiration The expiration timestamp for the mute.
     */
    public void mutePlayer(PlayerEntity player, String reason, PlayerEntity author, Timestamp expiration) {
        player.sendMessage(Text.literal(MODERATION_TAG).append("You got muted. Reason: ").formatted(Formatting.GRAY).append(reason).formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal(MODERATION_TAG).append("Until: ").formatted(Formatting.GRAY).append(expiration.toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))).formatted(Formatting.GOLD), false);

        this.applySanction(player, 1, reason, author, expiration);
        this.broadcastSanction("mute", player, reason, author, expiration);
    }

    /**
     * Unmutes a player.
     *
     * @param player The {@link UUID} of the player to unmute.
     * @param author The {@link PlayerEntity} who is authorizing the unmute.
     * @param reason The reason for the unmute.
     */
    public void unmutePlayer(UUID player, PlayerEntity author, String reason) {
        this.waiveSanction(player, 1, reason, author);
    }

    /**
     * Issues a warning to a player.
     *
     * @param player The {@link PlayerEntity} to warn.
     * @param reason The reason for the warning.
     * @param author The {@link PlayerEntity} who is authorizing the warning.
     */
    public void warnPlayer(PlayerEntity player, String reason, PlayerEntity author) {
        player.sendMessage(Text.literal(MODERATION_TAG).append("You received a warn. Reason: ").formatted(Formatting.GRAY).append(reason).formatted(Formatting.GOLD), false);
        player.playSound(SoundEvent.of(Identifier.of("minecraft", "note.pling")));

        this.applySanction(player, 4, reason, author, new Timestamp(System.currentTimeMillis()));
        this.broadcastSanction("warn", player, reason, author, null);
    }

    /**
     * Kicks a player from the server.
     *
     * @param player The {@link PlayerEntity} to kick.
     * @param reason The reason for the kick.
     * @param author The {@link PlayerEntity} who is authorizing the kick.
     */
    public void kickPlayer(PlayerEntity player, String reason, PlayerEntity author) {
        ((ServerPlayerEntity) player).networkHandler.disconnect(Text.literal(this.MODERATION_TAG).append("You got kicked from FurryPlayPlace. Reason: ").formatted(Formatting.GRAY).append(Text.of(reason)).formatted(Formatting.GOLD));

        this.applySanction(player, 5, reason, author, new Timestamp(System.currentTimeMillis()));
        this.broadcastSanction("kick", player, reason, author, null);
    }

    /**
     * Applies a sanction to a player in the database.
     *
     * @param player     The {@link PlayerEntity} to apply the sanction to.
     * @param typeId     The type ID of the sanction (e.g., mute, ban, warn).
     * @param reason     The reason for the sanction.
     * @param author     The {@link PlayerEntity} authorizing the sanction.
     * @param expiration The expiration timestamp of the sanction (optional for permanent sanctions).
     */
    private void applySanction(PlayerEntity player, int typeId, String reason, PlayerEntity author, Timestamp expiration) {
        this.queryPerformer("INSERT INTO sanctions (player_uuid, type_id, reason, punisher_uuid, expiration_date) VALUES ()", Map.of(
                1, player.toString().replaceAll("-", ""),
                2, typeId,
                3, reason,
                4, author.getUuid().toString().replaceAll("-", ""),
                5, expiration.toString()
        ));
    }

    /**
     * Waives a player's sanction.
     *
     * @param player The {@link UUID} of the player whose sanction is to be waived.
     * @param typeId The type ID of the sanction (e.g., mute, ban, warn).
     * @param reason The reason for waiving the sanction.
     * @param author The {@link PlayerEntity} authorizing the waiver.
     */
    private void waiveSanction(UUID player, int typeId, String reason, PlayerEntity author) {
        this.queryPerformer("UPDATE sanctions SET is_deleted = 1 WHERE player_uuid = ? AND type_id = ?", Map.of(
                1, player.toString().replaceAll("-", ""),
                2, typeId
        ));

        String type = switch (typeId) {
            case 1 -> "Mute";
            case 2 -> "Ban";
            case 4 -> "Warn";
            default -> "Unknown";
        };

        CottonAPI.get().server().getPlayerManager().getPlayerList().stream()
                .filter(this::hasPermission)
                .forEach(onlinePlayer -> onlinePlayer.sendMessage(Text.literal(MODERATION_TAG).append(player.toString()).formatted(Formatting.AQUA).append(" was waived of the sanction ").formatted(Formatting.GRAY).append(type).formatted(Formatting.AQUA).append (" reason ").formatted(Formatting.GRAY).append(reason).formatted(Formatting.AQUA).append(" by ").formatted(Formatting.GRAY).append(author.getName()).formatted(Formatting.AQUA), false));
    }

    /**
     * Broadcasts a sanction message to all authorized staff members.
     *
     * @param sanctionType The type of sanction (ban, mute, warn, etc.).
     * @param player      The {@link PlayerEntity} who is being sanctioned.
     * @param reason      The reason for the sanction.
     * @param author      The {@link PlayerEntity} authorizing the sanction.
     * @param expiration  The expiration timestamp of the sanction (optional).
     */
    private void broadcastSanction(String sanctionType, PlayerEntity player, String reason, PlayerEntity author, Timestamp expiration) {
        MutableText sanctionMessage = Text.literal(this.MODERATION_TAG)
                .append(Text.literal(sanctionType + " ").formatted(Formatting.RED, Formatting.BOLD))
                .append(Text.literal(player.getName().getString()).formatted(Formatting.GOLD))
                .append(Text.literal(" | Reason: ").formatted(Formatting.GRAY))
                .append(Text.literal(reason).formatted(Formatting.YELLOW))
                .append(Text.literal(" | By: ").formatted(Formatting.GRAY))
                .append(Text.literal(author.getName().getString()).formatted(Formatting.BLUE));

        if (expiration != null) {
            sanctionMessage
                    .append(Text.literal(" | Expires: ").formatted(Formatting.GRAY))
                    .append(Text.literal(expiration.toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))).formatted(Formatting.GREEN));
        }

        CottonAPI.get().server().getPlayerManager().getPlayerList().stream()
                .filter(this::hasPermission)
                .forEach(onlinePlayer -> onlinePlayer.sendMessage(sanctionMessage));
    }

    /**
     * Checks if a player has the required permissions.
     *
     * @param player The {@link ServerPlayerEntity} to check permissions for.
     * @return {@code true} if the player has the required permissions, {@code false} otherwise.
     */
    public boolean hasPermission(ServerPlayerEntity player) {
        Permissible permissible = CottonPermissions.getPermissible(player);

        if (permissible != null) {
            return permissible.hasPermission(this.seeStaffBroadcast) || player.hasPermissionLevel(3) || player.hasPermissionLevel(4);
        } else {
            return player.hasPermissionLevel(3) || player.hasPermissionLevel(4);
        }
    }

    // Action Performer

    /**
     * Utility method for executing database operations with a managed connection.
     *
     * @param runnable A {@code PerformAction} functional interface instance that encapsulates the database logic
     *                 to be executed using the provided {@link Connection}.
     * @throws NullPointerException if {@code runnable} is null.
     */
    private void actionPerformer(PerformAction<Connection> runnable) {
        if (runnable == null) {
            throw new NullPointerException("Runnable action cannot be null");
        }

        try (Connection connection = this.plugin.getDatabase().dataSource().getConnection()) {
            runnable.run(connection);
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Unable to open database connection: " + e.getMessage());
            this.plugin.getLogger().severe("SQL State: " + e.getSQLState());
            this.plugin.getLogger().severe("Error Code: " + e.getErrorCode());
        }
    }

    /**
     * Utility method for executing a database query using a prepared statement with parameterized values.
     *
     * @param query               The SQL query string to be executed.
     * @param parameters          A map of parameter indices (1-based) and values.
     * @param resultSetPerformAction The action to process the resulting {@link ResultSet}.
     * @throws NullPointerException if {@code query}, {@code parameters}, or {@code resultSetPerformAction} is null.
     */
    private void queryPerformer(String query, Map<Integer, Object> parameters, PerformAction<ResultSet> resultSetPerformAction) {
        if (query == null || parameters == null || resultSetPerformAction == null) {
            throw new NullPointerException("Query, parameters, and resultSetPerformAction cannot be null");
        }

        try (Connection connection = this.plugin.getDatabase().dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                statement.setObject(entry.getKey(), entry.getValue());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSetPerformAction.run(resultSet);
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Unable to execute query: " + e.getMessage());
            this.plugin.getLogger().severe("SQL State: " + e.getSQLState());
            this.plugin.getLogger().severe("Error Code: " + e.getErrorCode());
        }
    }

    /**
     * Executes a database query using a prepared statement with parameterized values.
     *
     * @param query The SQL query string to execute.
     * @param parameters A map containing parameter indices (1-based) as keys and their respective values as values.
     * @throws NullPointerException if {@code query} or {@code parameters} is {@code null}.
     */
    private void queryPerformer(String query, Map<Integer, Object> parameters) {
        if (query == null || parameters == null) {
            throw new NullPointerException("Query, parameters cannot be null");
        }

        try (Connection connection = this.plugin.getDatabase().dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                statement.setObject(entry.getKey(), entry.getValue());
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Unable to execute query: " + e.getMessage());
            this.plugin.getLogger().severe("SQL State: " + e.getSQLState());
            this.plugin.getLogger().severe("Error Code: " + e.getErrorCode());
        }
    }

    /**
     * Executes a database query using a prepared statement.
     *
     * @param query The SQL query string to execute.
     * @throws NullPointerException if {@code query} or {@code parameters} is {@code null}.
     */
    public void queryPerformer(String query) {
        if (query == null) {
            throw new NullPointerException("Query cannot be null");
        }

        try (Connection connection = this.plugin.getDatabase().dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Unable to execute query: " + e.getMessage());
            this.plugin.getLogger().severe("SQL State: " + e.getSQLState());
            this.plugin.getLogger().severe("Error Code: " + e.getErrorCode());
        }
    }
}