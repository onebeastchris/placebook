package onebeastchris.placebook.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import onebeastchris.placebook.util.PlayerDataCache;

public class Timer {
    private static int ticks = 0;
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticks++;
            if (ticks ==(20)) {
                ticks = 0;
                sendMessages();
            }
        });
    }
    public static void sendMessages(){
        for (ServerPlayerEntity player : PlayerDataCache.messageCache.keySet()){
            player.sendMessage(Text.of(PlayerDataCache.messageCache.get(player)), true);
        }
    }
}