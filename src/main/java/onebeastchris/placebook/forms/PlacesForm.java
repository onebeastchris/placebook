package onebeastchris.placebook.forms;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesForm implements FormInterface {
    private static HashMap<Integer, Integer> remap = new HashMap<>();
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, NbtCompound nbt,  String... args) {
        List<String> parsed = parseArgs(nbt, player, args);
        SimpleForm.Builder placesform = SimpleForm.builder()
                .title(parsed.get(0))
                .validResultHandler((form, response) -> {
                    PlaceBook.debug("Response to PlacesForm: " + response.clickedButtonId());
                    if (response.clickedButtonId() == parsed.size() + 1) {
                        FloodgateUtil.sendForm(player, previousForm);
                    } else {
                        NbtCompound place = nbt.getList("places", NbtCompound.LIST_TYPE).getCompound(remap.get(response.clickedButtonId() - 1));
                        FloodgateUtil.sendForm(player, PlaceForm.sendForm(player, form, place).build());
                    }
                });

        for (int i = 1; i <= parsed.size(); i++) {
            placesform.button(parsed.get(i));
        }
        return placesform;
    }

    private static List<String> parseArgs(NbtCompound places, ServerPlayerEntity player, String... args) {
        NbtList potentials = places.getList("places", NbtCompound.LIST_TYPE);
        List<String> parsed = new ArrayList<>();

        parsed.add(0, "Places of " + args[0]);
        for (int i = 0; i < potentials.size(); i++) {
            NbtCompound place = potentials.getCompound(i);
            if (place.getBoolean("visibility")) {
                parsed.add(place.getString("name"));
                remap.put(parsed.size(), i);
            }
        }
        parsed.add("Back");
        return parsed;
    }
}