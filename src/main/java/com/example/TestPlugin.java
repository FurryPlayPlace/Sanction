package com.example;

import com.example.commands.ExampleCommand;
import com.google.common.eventbus.Subscribe;
import net.furryplayplace.cottonframework.api.CottonAPI;
import net.furryplayplace.cottonframework.api.events.player.PlayerTeleportEvent;
import net.furryplayplace.cottonframework.api.plugin.CottonPlugin;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TestPlugin extends CottonPlugin {


    public TestPlugin() {
        super("TestPlugin", "1.0.0", List.of(
                "Vakea"
        ));
    }

    @Override
    public void onEnable() {
        System.out.println("Hello World!");

        // To register a command
        CottonAPI.get().pluginManager().registerCommand(this, new ExampleCommand(this));

        // To register a event listener
        CottonAPI.get().pluginManager().getEventBus().register(this);
    }

    @Override
    public void onDisable() {
        System.out.println("Goodbye World!");
    }

    @Override
    public void onLoad() {}

    @Subscribe
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.hasChangedPosition()) {
            event.getPlayer().sendMessage(Text.literal("Yippie! You got teleported to another position.").formatted(Formatting.GRAY));
        }
    }
}