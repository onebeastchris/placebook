package onebeastchris.placebook.forms;


import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.ColorUtil;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class PlacesForm implements FormInterface {
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, GameProfile profile, String... args) {
        List<NbtCompound> placesList = new ArrayList<>();
        List<String> parsed = parseArgs(player, profile, args);
        boolean isOwner = Boolean.parseBoolean(parsed.get(2));

        SimpleForm.Builder placesform = SimpleForm.builder()
                .title(parsed.get(0))
                .content(parsed.get(1))
                .validResultHandler((form, response) -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId >= placesList.size()) {
                        if (response.clickedButton().text().equals("Add Place")) {
                            FloodgateUtil.sendForm(player, addOrEditForm.sendForm(player, form, null).build());
                        } else {
                            FloodgateUtil.sendForm(player, previousForm);
                        }
                    } else {
                        int index = placesList.indexOf(placesList.get(buttonId));
                        FloodgateUtil.sendForm(player, PlaceForm.sendForm(player, form, placesList.get(buttonId), isOwner, index).build());
                    }
                });

        NbtCompound playerData = PlayerDataCache.getPlayer(profile);
        if (playerData != null) {
            NbtList places = playerData.getList("places", 10);
            for (NbtElement p : places) {
                NbtCompound place = (NbtCompound) p;
                String name = place.getString("name");
                String color = ColorUtil.colorMap.get(place.getString("color"));
                boolean show = place.getBoolean("visibility") || isOwner;
                placesList.add(place);
                placesform.optionalButton(color + name, show);
            }
        }
        placesform.optionalButton("Add Place", isOwner);
        placesform.button("Back");
        return placesform;
    }

    private static List<String> parseArgs(ServerPlayerEntity player, GameProfile profile, String... args) {
        List<String> parsed = new ArrayList<>();
        boolean isOwner = player.getUuid().equals(profile.getId());
        String title = isOwner ? "Your Places" : "Places of " + profile.getName();

        if (args.length == 0) {
            parsed.add(title);
            parsed.add("Click a place to see more");
            parsed.add(String.valueOf(isOwner));
            return parsed;
        } else {
            parsed.add(title);
            parsed.add("Select a place to see more");
            parsed.add("true");
            return parsed;
        }
    }
}