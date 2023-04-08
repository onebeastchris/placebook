package onebeastchris.placebook.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import onebeastchris.placebook.command.PlacesCommand;
import onebeastchris.placebook.event.PlayerEvents;
import onebeastchris.placebook.event.ServerLifeCycleEvent;
import onebeastchris.placebook.event.Timer;

public class Register {

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PlacesCommand.register(dispatcher);
        });
    }

    private static void registerStorage() {
        PlayerStorage.register();
    }

    private static void registerEvents() {
        ServerLifeCycleEvent.register();
        PlayerEvents.registerJoinEvent();
        PlayerEvents.registerLeaveEvent();
        Timer.register();
    }

    public static void registerAll() {
        registerCommands();
        registerStorage();
        registerEvents();
    }

}