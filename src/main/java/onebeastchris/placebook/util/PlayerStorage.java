package onebeastchris.placebook.util;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import onebeastchris.placebook.PlaceBook;

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
         PlaceBook.debug("Data for player: " + player.getEntityName() + " " + data);
         if (data == null){
             set(player, DEFAULT_DATA);
         }
         return data;
    }
    public static void addNewHome(ServerPlayerEntity player, BlockPos pos, String WorldName, String name, String color, String description, boolean isPublic) {
        NbtCompound data = getOrCreate(player);
        NbtCompound place = createHome(name, color, description, isPublic, pos, WorldName);
        NbtList places = data.getList("places", NbtElement.COMPOUND_TYPE);
        PlaceBook.debug("PlaceList " + places.toString());
        places.add(place);
        data.put("places", places);
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
        PlaceBook.debug("Removing home at index: " + index + " for player: " + player.getGameProfile().getName());
        NbtCompound data = getOrCreate(player);
        NbtList places = data.getList("places", NbtElement.COMPOUND_TYPE);
        PlaceBook.debug("PlaceList " + places.toString());
        places.remove(index);
        data.put("places", places);
        set(player, data);
    }

    public static void updateHome(ServerPlayerEntity player, int index, String color, String name, String description, boolean isPublic, BlockPos pos, String WorldName) {
        NbtCompound data = getOrCreate(player);
        NbtList places = data.getList("places", NbtElement.COMPOUND_TYPE);
        PlaceBook.debug("PlaceList before " + places.toString());
        NbtCompound place = places.getCompound(index);

        if (name == null || name.isEmpty() || name.isBlank()) {
            name = place.getString("name");
        }
        if (color == null || color.isEmpty() || color.isBlank()) {
            color = place.getString("color");
        }
        if (description == null || description.isEmpty() || description.isBlank()) {
            description = place.getString("description");
        }
        if (pos == null) {
            NbtList posList = place.getList("pos", NbtElement.INT_TYPE);
            pos = new BlockPos(posList.getInt(0), posList.getInt(1), posList.getInt(2));
        }
        if (WorldName == null || WorldName.isEmpty() || WorldName.isBlank()) {
            WorldName = place.getString("world");
        }

        NbtCompound newPlace = createHome(name, color, description, isPublic, pos, WorldName);
        places.set(index, newPlace);
        PlaceBook.debug("PlaceList new " + places.toString());
        data.put("places", places);
        set(player, data);
    }

    public static NbtCompound createHome(String name, String color, String description, boolean isPublic, BlockPos pos, String WorldName){
        NbtCompound place = new NbtCompound();
        place.putString("name", name);
        place.putString("color", color);
        place.putBoolean("visibility", isPublic);
        place.putString("description", description);
        if (pos == null) {
            place.put("pos", new NbtList());
        } else {
            place.put("pos", newLocation(pos));
        }
        place.putString("world", WorldName);
        return place;
    }

}