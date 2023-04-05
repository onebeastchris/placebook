package onebeastchris.placebook.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import onebeastchris.placebook.util.PlayerDataCache;
public class ServerLifeCycleEvent {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(PlayerDataCache::loadAll);
    }

}