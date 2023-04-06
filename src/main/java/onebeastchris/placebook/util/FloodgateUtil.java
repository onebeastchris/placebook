package onebeastchris.placebook.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.PlaceBook;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class FloodgateUtil {

    private static boolean isFloodgateAPIavailable = isIsFloodgateAPIavailable();

    public static boolean isIsFloodgateAPIavailable() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return true;
        } catch (ClassNotFoundException e) {
            PlaceBook.LOGGER.warn("Floodgate API not found. Bedrock players will not be able to use PlaceBook - either install floodgate, or disable the floodgate option in the config.");
            return false;
        }
    }

    public static boolean isFloodgatePlayer(PlayerEntity player) {
        if (isFloodgateAPIavailable) {
            return FloodgateApi.getInstance().isFloodgatePlayer(player.getUuid());
        }
        return false;
    }

    public static boolean isFloodgatePlayer(UUID uuid){
        if (isFloodgateAPIavailable) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }
        return false;
    }

    public static void sendForm (ServerPlayerEntity player, Form form) {
        if (isFloodgatePlayer(player)) {
            FloodgateApi.getInstance().sendForm(player.getUuid(), form);
        }
    }

    public static String getXuid(UUID uuid) {
        if (isFloodgatePlayer(uuid)) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(uuid);
            return floodgatePlayer.getXuid();
        }
        return null;
    }

    public static boolean isLinked(UUID uuid) {
        if (isFloodgatePlayer(uuid)) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(uuid);
            return floodgatePlayer.isLinked();
        }
        return false;
    }
}