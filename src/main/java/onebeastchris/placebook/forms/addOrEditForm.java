package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.ColorUtil;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import onebeastchris.placebook.util.PlayerStorage;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class addOrEditForm implements FormInterface{

    private static final String[] colors = {"§0black", "§1dark_blue", "§2dark_green", "§3dark_aqua", "§4dark_red", "§5dark_purple", "§6gold", "§8dark_gray", "§9blue", "§agreen", "§baqua", "§cred", "§dlight_purple", "§eyellow"};

    public static CustomForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, NbtCompound nbtCompound, int index, List<String> names, String... args) {
        PlaceBook.debug("addOrEditForm.sendForm() called " + nbtCompound + " " + index + " " + Arrays.toString(args));
        List<String> argsList = parseArgs(nbtCompound, args);
        boolean isPublic = argsList.get(2).equals("visible");
        boolean isEditor = nbtCompound != null;
        String currentColor = argsList.get(5);

        return CustomForm.builder()
                .title(argsList.get(0))
                .input("Name", argsList.get(1))
                .dropdown("Color", colors)
                .toggle("Visible to everyone?", isPublic)
                .input("Description", argsList.get(3))
                .input("Position", argsList.get(4))
                .dropdown("Dimension", "current", "overworld", "nether", "end")
                .optionalToggle("Keep old description?", true, isEditor)
                .optionalToggle("Keep " + currentColor + " as the name color?", true, isEditor)
                .invalidResultHandler((response) -> {
                    FloodgateUtil.sendForm(player, sendForm(player, previousForm, nbtCompound, index, names,"§4Invalid entries!").build());
                })
                .validResultHandler((response) -> {
                    String name = response.next();
                    int color = response.asDropdown();
                    boolean isVis = Boolean.TRUE.equals(response.next());
                    String description = response.next();
                    BlockPos position = parsePosition(Objects.requireNonNull(response.next()), player, nbtCompound);
                    int dimension = response.asDropdown();
                    boolean keepOldDescription = Boolean.TRUE.equals(response.next());
                    boolean keepColor = Boolean.TRUE.equals(response.next());

                    if (nbtCompound != null) {
                        NbtCompound edit = PlayerStorage.createHome(name, ColorUtil.colornames[color], description, isVis, position, nbtCompound.getString("dimension"));
                        if (name == null || name.isEmpty() || name.isBlank()) {
                            name = nbtCompound.getString("name");
                            if (name.isEmpty() || name.isBlank()) {
                                FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Invalid name!").build());
                                return;
                            }
                        } else if (names.contains(name) && !names.get(index).equals(name)) {
                            FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Place with that name already exists!").build());
                            return;
                        } else if (position == null || position.getY() < -64 || position.getY() > 320) {
                            FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Invalid position!").build());
                            return;
                        }
                        if (keepOldDescription) {
                            description = nbtCompound.getString("description");
                        }
                        if (keepColor) {
                            color = Arrays.asList(ColorUtil.colornames).indexOf(nbtCompound.getString("color"));
                        }

                        if (index == -1) {
                            PlayerStorage.addNewHome(player, position, getDimension(player, dimension), name, ColorUtil.colornames[color], description, isVis);
                        } else {
                            PlayerStorage.updateHome(player, index, ColorUtil.colornames[color], name, description, isVis, position, getDimension(player, dimension));
                        }
                    } else {
                        NbtCompound edit = PlayerStorage.createHome(name, ColorUtil.colornames[color], description, isVis, position, getDimension(player, dimension));
                        if (position == null || position.getY() < -64 || position.getY() > 320) {
                            FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Invalid position!").build());
                            return;
                        }
                        if (name == null || name.isEmpty()) {
                            FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Invalid name!").build());
                            return;
                        } else if (names.contains(name)) {
                            FloodgateUtil.sendForm(player, sendForm(player, previousForm, edit, index, names, "§4Place with that name already exists!").build());
                            return;
                        }
                        PlayerStorage.addNewHome(player, position, getDimension(player, dimension), name, ColorUtil.colornames[color], description, isVis);
                    }

                    PlayerDataCache.updateCache(player);
                    //send players back to previous form
                    FloodgateUtil.sendForm(player, previousForm); //TODO: admin mode to edit other players?
                });
    }

    private static List<String> parseArgs(NbtCompound nbt, String... args) {
        String[] def = {"Add new place", //0
                "Put a name here", //1
                "visible", //2
                "Put an optional description here", //3
                "Empty: Your pos. Or, e.g.: §3-3 §369 §33",//4
                "chris says hi"}; //5
        if (args.length == 0) {
            return Arrays.asList(def);
        } else {
            List<String> custom = new ArrayList<>();
            if (nbt != null) {
                custom.add(args[0]);
                custom.add(nbt.getString("name"));
                custom.add(nbt.getBoolean("visibility") ? "visible" : "private");
                custom.add(nbt.getString("description"));
                NbtList pos = nbt.getList("pos", NbtElement.INT_TYPE);
                if (pos.size() != 3) {
                    custom.add("");
                } else {
                    custom.add(pos.getInt(0) + " " + pos.getInt(1) + " " + pos.getInt(2));
                }
                custom.add(nbt.getString("color"));

                for (int i = 0; i < custom.size(); i++) {
                    if (custom.get(i).isEmpty() || custom.get(i).isBlank()) {
                        custom.set(i, def[i]);
                    }
                }
            } else {
                custom.add(args[0]);
                custom.add(def[1]);
                custom.add(def[2]);
                custom.add(def[3]);
                custom.add(def[4]);
            }
            return custom;
        }
    }

    private static BlockPos parsePosition(String position, ServerPlayerEntity player, NbtCompound nbt) {
        String[] pos = position.split(" ");
        if (pos.length == 3){
            return new BlockPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
        } else {
            if (nbt == null) {
                if (position.isEmpty()) {
                    return player.getBlockPos();
                } else {
                    return null;
                }
            } else {
                NbtList posList = nbt.getList("pos", NbtElement.INT_TYPE);
                return new BlockPos(posList.getInt(0), posList.getInt(1), posList.getInt(2));
            }
        }
    }

    public static String getDimension(ServerPlayerEntity player, int dim) {
        var dimension = player.getEntityWorld().getRegistryKey();

        if (dim == 0) {
            if (dimension.equals(World.OVERWORLD)) {
                return "overworld";
            } else if (dimension.equals(World.NETHER)) {
                return "nether";
            } else if (dimension.equals(World.END)) {
                return "end";
            } else {
                return "unknown";
            }
        } else {
            if (dim == 1) {
                return "overworld";
            } else if (dim == 2) {
                return "nether";
            } else if (dim == 3) {
                return "end";
            }
        }
        return "unknown";
    }
}