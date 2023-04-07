package onebeastchris.placebook.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import onebeastchris.placebook.util.PlayerDataCache;

public class PlayerEvents {

    public static void registerJoinEvent() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerDataCache.updateStatus(handler.player, true);
        });
    }

    public static void registerLeaveEvent() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerDataCache.updateStatus(handler.player, false);
        });
    }


}