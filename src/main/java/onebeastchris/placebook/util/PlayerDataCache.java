package onebeastchris.placebook.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.skin.Heads;
import onebeastchris.placebook.skin.SkinUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerDataCache {
    private static final HashMap<GameProfile, NbtCompound> onlineCache = new HashMap<>();
    private static final HashMap<GameProfile, NbtCompound> offlineCache = new HashMap<>();

    public static final HashMap<UUID, SkinUtil> TEXTURES = new HashMap<>();

    public static void update(ServerPlayerEntity player, boolean isOnline) {
        UUID uuid = player.getUuid();
        GameProfile gameProfile = player.getGameProfile();
        if (isOnline) {
            onlineCache.put(gameProfile, PlayerStorage.getOrCreate(player));
            addToTextureCache(gameProfile);
            offlineCache.remove(gameProfile);
        } else {
            offlineCache.put(gameProfile, onlineCache.get(gameProfile));
            onlineCache.remove(gameProfile);
        }
    }

    public static void loadAll(MinecraftServer server) {
        // Load all players from the user cache
        var a = server.getUserCache().load();
        for (var entry : a) {
            GameProfile gameProfile = entry.getProfile();
            if (gameProfile != null) {
                PlaceBook.LOGGER.debug("Loading player: " + gameProfile.getName());
                Optional<NbtCompound> optional = PlayerStorage.getOfflinePlayer(gameProfile.getId(), server);
                optional.ifPresent(nbtCompound -> offlineCache.put(gameProfile, nbtCompound));
                addToTextureCache(gameProfile);
            }
        }
    }

    public static List<GameProfile> getOnlinePlayers() {
        return new ArrayList<>(onlineCache.keySet());
    }

    public static List<GameProfile> getOfflinePlayers() {
        return new ArrayList<>(offlineCache.keySet());
    }

    public static Optional<NbtCompound> getOnlinePlayer(GameProfile gameProfile) {
        return Optional.ofNullable(onlineCache.get(gameProfile));
    }

    public static List<GameProfile> getAllPlayers() {
        List<GameProfile> allPlayers = new ArrayList<>(onlineCache.keySet());
        allPlayers.addAll(offlineCache.keySet());
        return allPlayers;
    }

    public static void addToTextureCache(GameProfile gameProfile) {
        var skinUtil = new SkinUtil();
        CompletableFuture<Void> a = skinUtil.fill(gameProfile);
        a.thenRun(() -> TEXTURES.put(gameProfile.getId(), skinUtil));
    }
}