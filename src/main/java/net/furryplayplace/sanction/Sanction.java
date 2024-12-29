package net.furryplayplace.sanction;

import net.furryplayplace.cottonframework.api.plugin.CottonPlugin;
import net.furryplayplace.sanction.commands.CommandManager;
import net.furryplayplace.sanction.database.MinecraftDatabase;
import net.furryplayplace.sanction.listeners.ListenerManager;
import net.furryplayplace.sanction.manager.ModerationManager;
import net.furryplayplace.sanction.tasks.UpdateSanction;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sanction extends CottonPlugin {
    private MinecraftDatabase database;
    private ModerationManager moderationManager;
    private final CommandManager commandManager = new CommandManager();
    private final ListenerManager listenerManager = new ListenerManager();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(16);;

    public Sanction() {
        super("Sanction", "1.0.0", List.of(
                "Vakea"
        ));
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Starting Sanction Plugin...");
        this.getLogger().info("Version: v" + this.version());
        this.getLogger().info("Authors: " + String.join(", ", this.authors()));

        this.database = new MinecraftDatabase(this);
        this.moderationManager = new ModerationManager(this);

        this.scheduledExecutorService.scheduleAtFixedRate(new UpdateSanction(this),
                this.getConfig().getInt("sanctionUpdateTask.initialDelay", 1),
                this.getConfig().getInt("sanctionUpdateTask.initialDelay", 1),
                TimeUnit.valueOf(this.getConfig().getString("sanctionUpdateTask.unit", TimeUnit.MINUTES.name()))
        );

        this.commandManager.register(this);
        this.listenerManager.register(this);
    }

    @Override
    public void onDisable() {
        try {
            this.database.dataSource().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.getLogger().severe("Unable to close database connection.");
        }
    }

    @Override
    public void onLoad() {}

    public MinecraftDatabase getDatabase() {
        return database;
    }

    public ModerationManager moderationManager() {
        return moderationManager;
    }
}