package onebeastchris.placebook.util;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerStorage {
    private static PlayerDataStorage<NbtCompound> PLACEBOOK_STORAGE = new NbtDataStorage("placebook");
    private static final NbtCompound DEFAULT_DATA = makeNbtCompound();
    public static void register() {
        PlayerDataApi.register(PLACEBOOK_STORAGE);
    }

    private static Optional<NbtCompound> getOnlinePlayer(ServerPlayerEntity player) {
        return Optional.ofNullable(PlayerDataApi.getCustomDataFor(player, PLACEBOOK_STORAGE));
    }

    public static Optional<NbtCompound> getOfflinePlayer(UUID uuid, MinecraftServer server) {
        return Optional.ofNullable(PlayerDataApi.getCustomDataFor(server, uuid, PLACEBOOK_STORAGE));
    }

    public static void set(ServerPlayerEntity player, NbtCompound data) {
        PlayerDataApi.setCustomDataFor(player, PLACEBOOK_STORAGE, data);
    }

    public static NbtCompound getOrCreate(ServerPlayerEntity player) {
         var data = getOnlinePlayer(player).orElse(null);
         if (data == null){
             set(player, DEFAULT_DATA);
         }
         return data;
    }
    public static void addNewHome(ServerPlayerEntity player, BlockPos pos, String WorldName, String name, String description, boolean isPublic, int index) {
        NbtCompound data = getOrCreate(player);
        NbtList homes = data.getList("homes", NbtElement.LIST_TYPE);

        NbtCompound home = new NbtCompound();
        home.putString("name", name);
        home.putString("description", description);
        home.putBoolean("visibility", isPublic);

        home.put("pos", newLocation(pos, WorldName));
        homes.add(index, home);
        set(player, data);
    }

    private static NbtCompound makeNbtCompound(){
        NbtCompound nbt = new NbtCompound();
        NbtList homes = new NbtList();
        nbt.put("homes", homes);
        return nbt;
    }

    private static NbtList newLocation(BlockPos pos, String WorldName){
        NbtList posList = new NbtList();
        posList.add(NbtInt.of(pos.getX()));
        posList.add(NbtInt.of(pos.getY()));
        posList.add(NbtInt.of(pos.getZ()));
        posList.add(NbtString.of(WorldName));
        return posList;
    }

    public static void removeHome(ServerPlayerEntity player, int index) {
        NbtCompound data = getOrCreate(player);
        NbtList homes = data.getList("homes", NbtElement.LIST_TYPE);
        homes.remove(index);
        set(player, data);
    }

    public static void updateHome(ServerPlayerEntity player, int index, String name, String description, boolean isPublic, BlockPos pos, String WorldName) {
        NbtCompound data = getOrCreate(player);
        NbtList homes = data.getList("homes", NbtElement.LIST_TYPE);
        NbtCompound home = homes.getCompound(index);

        home.putString("name", name);
        home.putString("description", description);
        home.putBoolean("visibility", isPublic);

        home.put("pos", newLocation(pos, WorldName));

        homes.set(index, home);
        set(player, data);
    }


}