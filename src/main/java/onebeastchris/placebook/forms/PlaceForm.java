package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import onebeastchris.placebook.util.ColorUtil;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceForm implements FormInterface {
    public static ModalForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, Form deleteFallBack, NbtCompound place, boolean canEdit, int index, List<String> names) {
        List<String> args = parseArgs(place, player);

        if (canEdit) {
            return ModalForm.builder()
                    .title("You can edit: " + args.get(0))
                    .content(args.get(1) + args.get(2) + args.get(3) + args.get(4))
                    .button1("Edit")
                    .button2("Back")
                    .validResultHandler((form, response) -> {
                                switch (response.clickedButtonId()) {
                                    case 0 -> FloodgateUtil.sendForm(player, EditOrDelete.sendForm(player, form, deleteFallBack, place, names, index).build());
                                    case 1 -> FloodgateUtil.sendForm(player, previousForm);
                                }
                            }
                    );
        } else {
            return ModalForm.builder()
                    .title("Looking at: " + args.get(0))
                    .content(args.get(1) + args.get(2) + args.get(3) + args.get(4))
                    .button1("Pin to actionbar")
                    .button2("Back")
                    .validResultHandler((form, response) -> {
                        switch (response.clickedButtonId()) {
                            case 0 -> {
                                NbtList posList = place.getList("pos", NbtElement.INT_TYPE);
                                int x = posList.getInt(0);
                                int y = posList.getInt(1);
                                int z = posList.getInt(2);
                                String dimension = place.getString("world");
                                //show position on the
                                String message = args.get(0) + "§r is at §l" + x + " " + y + " " + z + "§r in the " + parseWorld(dimension) + ".";
                                PlayerDataCache.messageCache.put(player, message);
                            }
                            case 1 -> FloodgateUtil.sendForm(player, previousForm);
                        }
                    }
                    );
        }
    }

    private static List<String> parseArgs(NbtCompound place, ServerPlayerEntity player) {
        List<String> homeData = new ArrayList<>();
        String color = ColorUtil.colorMap.get(place.getString("color"));
        homeData.add(color + place.getString("name")); //0

        String description = place.getString("description");
        if (description.equals("")) {
            homeData.add("No description set." + "\n" + " " + "\n"); //1
        } else {
            homeData.add("§lDescription: §r " + "\n" + description + "\n" + " " + "\n"); //1
        }

        NbtList posList = place.getList("pos", NbtElement.INT_TYPE);
        int x = posList.getInt(0);
        int y = posList.getInt(1);
        int z = posList.getInt(2);
        String world = place.getString("world");

        String location = "\"" + homeData.get(0) + "§r\" is at §l" + x + " " + y + " " + z + "§r in the " + parseWorld(world) + "." + "\n" + " " + "\n";
        homeData.add(location); //2

        boolean isVisible = place.getBoolean("visibility");
        if (isVisible) {
            homeData.add("" + "\n"); //3
        } else {
            homeData.add("""
                    Only you can see this place.
                    \s
                    """); //3
        }

        if (Objects.equals(addOrEditForm.getDimension(player, 0), world)) {
            homeData.add("You are §l" + player.getPos().distanceTo(new Vec3d(x, y, z)) + "§r blocks away" + "\n"); //4
        } else {
            homeData.add("Go to the " + world + " to see how far away you are." + "\n"); //4
        }
        return homeData;
    }

    private static String parseWorld(String world) {
        switch (world) {
            case "overworld" -> {
                return ColorUtil.getColor("green") + "overworld§r";
            }
            case "nether" -> {
                return ColorUtil.getColor("red") + "nether§r";
            }
            case "end" -> {
                return ColorUtil.getColor("light_purple") + "end§r";
            }
            default -> {
                return ColorUtil.getColor("white") + world;
            }
        }
    }
}