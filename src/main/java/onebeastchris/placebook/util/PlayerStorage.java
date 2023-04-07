package onebeastchris.placebook.util;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.forms.PlaceForm;
import onebeastchris.placebook.skin.ColorUtil;

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
        PlaceBook.debug("Setting nbt: " + data.toString() + " for player: " + player.getGameProfile().getName() + "");
        PlayerDataApi.setCustomDataFor(player, PLACEBOOK_STORAGE, data);
    }

    public static NbtCompound getOrCreate(ServerPlayerEntity player) {
         var data = getOnlinePlayer(player).orElse(null);
         if (data == null){
             set(player, DEFAULT_DATA);
         }
         return data;
    }
    public static void addNewHome(ServerPlayerEntity player, BlockPos pos, String WorldName, String name, String color, String description, boolean isPublic) {
        NbtCompound data = getOrCreate(player);
        NbtList places = data.getList("places", NbtElement.LIST_TYPE);

        NbtCompound place = new NbtCompound();
        place.putString("name", name);
        place.putString("color", color);
        place.putBoolean("visibility", isPublic);
        place.putString("description", description);
        place.put("pos", newLocation(pos));
        place.putString("world", WorldName);

        places.add(place);
        set(player, data);
    }

    private static NbtCompound makeNbtCompound(){
        NbtCompound nbt = new NbtCompound();
        NbtList places = new NbtList();
        nbt.put("places", places);
        return nbt;
    }

    private static NbtList newLocation(BlockPos pos){
        NbtList posList = new NbtList();
        posList.add(NbtInt.of(pos.getX()));
        posList.add(NbtInt.of(pos.getY()));
        posList.add(NbtInt.of(pos.getZ()));
        return posList;
    }

    public static void removeHome(ServerPlayerEntity player, int index) {
        NbtCompound data = getOrCreate(player);
        NbtList places = data.getList("places", NbtElement.LIST_TYPE);
        places.remove(index);
        set(player, data);
    }

    public static void updateHome(ServerPlayerEntity player, int index, String name, String description, boolean isPublic, BlockPos pos, String WorldName) {
        NbtCompound data = getOrCreate(player);
        NbtList places = data.getList("places", NbtElement.LIST_TYPE);
        NbtCompound place = places.getCompound(index);

        place.putString("name", name);
        place.putBoolean("visibility", isPublic);
        place.putString("description", description);

        place.put("pos", newLocation(pos));
        place.putString("world", WorldName);

        places.set(index, place);
        set(player, data);
    }


}