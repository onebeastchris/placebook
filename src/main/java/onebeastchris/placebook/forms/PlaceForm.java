package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceForm implements FormInterface{
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, NbtCompound place, boolean canEdit) {
        List<String> args = parseArgs(place, player);
        return SimpleForm.builder()
                .title(args.get(0))
                .content(args.get(1))
                .optionalButton("Edit", canEdit)
                .button("Back")
                .validResultHandler((response) -> {
                    FloodgateUtil.sendForm(player, previousForm);
                });
    }

    private static List<String> parseArgs(NbtCompound place, ServerPlayerEntity player) {
        List<String> homeData = new ArrayList<>();
        homeData.add(place.getString("name"));

        String description = place.getString("description");

        NbtList posList = place.getList("pos", NbtElement.LIST_TYPE);
        String x = posList.getString(0);
        String y = posList.getString(1);
        String z = posList.getString(2);
        String world = place.getString("world");

        String location = x + " " + y + " " + z + " in world named" + world;

        String result = description + "\n" + location + "\n";

        if (Objects.equals(player.getWorld().getDimensionKey().toString(), world)) {
             result = result + "You are " + player.getPos().distanceTo(new Vec3d(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z))) + " blocks away";
        }
        homeData.add(result);
        return homeData;
    }
}