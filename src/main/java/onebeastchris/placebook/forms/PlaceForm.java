package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import onebeastchris.placebook.util.ColorUtil;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceForm implements FormInterface {
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, NbtCompound place, boolean canEdit, int index) {
        List<String> args = parseArgs(place, player);
        return SimpleForm.builder()
                .title(args.get(0))
                .content(args.get(1))
                .optionalButton("Edit", canEdit)
                .optionalButton("Delete", canEdit)
                .button("Back")
                .validResultHandler((form, response) -> {
                            switch (response.clickedButtonId()) {
                                case 0 -> FloodgateUtil.sendForm(player, addOrEditForm.sendForm(player, form, place).build());
                                case 1 -> FloodgateUtil.sendForm(player, DeleteForm.sendForm(player, previousForm, args.get(0), index).build());
                                case 2 -> FloodgateUtil.sendForm(player, previousForm);
                            }
                    }
                );
    }

    private static List<String> parseArgs(NbtCompound place, ServerPlayerEntity player) {
        List<String> homeData = new ArrayList<>();
        String color = ColorUtil.colorMap.get(place.getString("color"));
        homeData.add(color + place.getString("name"));

        String description = place.getString("description");

        NbtList posList = place.getList("pos", NbtElement.LIST_TYPE);
        String x = posList.getString(0);
        String y = posList.getString(1);
        String z = posList.getString(2);
        String world = place.getString("world");

        String location = x + " " + y + " " + z + " in world named" + world;

        String result = description + "\n" + location + "\n";

        if (Objects.equals(player.getWorld().getDimensionKey().toString(), world)) {
            //result = result + "You are " + player.getPos().distanceTo(new Vec3d(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z))) + " blocks away";#
            //deal with this later
        }
        homeData.add(result);
        return homeData;
    }
}