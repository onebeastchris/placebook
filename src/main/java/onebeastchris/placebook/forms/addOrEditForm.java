package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import onebeastchris.placebook.util.PlayerStorage;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class addOrEditForm implements FormInterface{

    private static final String[] colors = {"§0black", "§1dark_blue", "§2dark_green", "§3dark_aqua", "§4dark_red", "§5dark_purple", "§6gold", "§8dark_gray", "§9blue", "§agreen", "§baqua", "§cred", "§dlight_purple", "§eyellow"};
    public static CustomForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, NbtCompound nbtCompound, String... args) {
        List<String> argsList = parseArgs(nbtCompound, args);
        boolean isPrivate = argsList.get(2).equals("private");

        return CustomForm.builder()
                .title(argsList.get(0))
                .input("Name", argsList.get(1))
                .dropdown("Color", colors)
                .toggle("Only visible to you?", isPrivate)
                .input("Description", argsList.get(3))
                .input("Position", argsList.get(4))
                .invalidResultHandler((response) -> {
                    FloodgateUtil.sendForm(player, sendForm(player, previousForm, nbtCompound, "Invalid entries!").build());
                })
                .validResultHandler((response) -> {
                    String name = response.next();
                    int color = response.next();
                    boolean priv = Boolean.FALSE.equals(response.next());
                    String description = response.next();
                    BlockPos position = parsePosition(Objects.requireNonNull(response.next()), player);
                    if (position == null) {
                        FloodgateUtil.sendForm(player, sendForm(player, previousForm, nbtCompound, "Invalid position!").build());
                        return;
                    }
                    PlayerStorage.addNewHome(player, position, player.getWorld().getDimensionKey().toString(), name, colors[color], description, !priv);
                    PlayerDataCache.updateCache(player);
                    //send players back to previous form
                    FloodgateUtil.sendForm(player, previousForm); //TODO: send to the correct form?
                });
    }

    private static List<String> parseArgs(NbtCompound nbt, String... args) {
        String[] def = {"Add new place", //0
                "Put a name here", //1
                "visible", //2
                "Put an optional description here", //3
                "Leave empty to use your position. Or: §3100 §360 §3100"}; //4
        if (args.length == 0) {
            return Arrays.asList(def);
        } else {
            List<String> custom = Arrays.asList(args);
            if (nbt != null) {
                custom.set(0, args[0]);
                custom.set(1, nbt.getString("name"));
                custom.set(2, nbt.getBoolean("isPublic") ? "visible" : "private");
                custom.set(3, nbt.getString("description"));
                NbtList pos = nbt.getList("position", NbtElement.INT_TYPE);
                custom.set(4, pos.getInt(0) + " " + pos.getInt(1) + " " + pos.getInt(2));
            } else {
                custom.addAll(Arrays.asList(args));
                if (custom.size() < 5) {
                    custom.addAll(Arrays.asList(def).subList(custom.size(), 5));
                }
            }
            return custom;
        }
    }

    private static BlockPos parsePosition(String position, ServerPlayerEntity player){
        String[] pos = position.split(" ");
        if (pos.length == 3){
            return new BlockPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
        } else {
            if (position.isEmpty()) {
                return player.getBlockPos();
            } else {
                return null;
            }
        }
    }

    public static CustomForm.Builder editHome(ServerPlayerEntity player, Form previousForm, NbtCompound nbt, String... args) {
        return sendForm(player, previousForm, nbt, "Edit:", nbt.getString("name"), nbt.getBoolean("isPublic") ? "visible" : "private", nbt.getString("description"));
    }
}