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
        boolean isEditor = Boolean.parseBoolean(parsed.get(3));
        List<String> names = new ArrayList<>();

        SimpleForm.Builder placesform = SimpleForm.builder()
                .title(parsed.get(0))
                .content(parsed.get(1))
                .validResultHandler((form, response) -> {
                    int buttonId = response.clickedButtonId();
                    if (buttonId >= placesList.size()) {
                        if (response.clickedButton().text().equals("Add Place")) {
                            FloodgateUtil.sendForm(player, addOrEditForm.sendForm(player, previousForm, null, -1, names).build());
                        } else if (response.clickedButton().text().equals("Editor mode")) {
                            FloodgateUtil.sendForm(player, PlacesForm.sendForm(player, previousForm, profile, "editor").build());
                        } else if (response.clickedButton().text().equals("Exit editor mode")) {
                            FloodgateUtil.sendForm(player, PlacesForm.sendForm(player, previousForm, profile).build());
                        } else {
                            FloodgateUtil.sendForm(player, previousForm);
                        }
                    } else {
                        int index = placesList.indexOf(placesList.get(buttonId));
                        FloodgateUtil.sendForm(player, PlaceForm.sendForm(player, form, previousForm, placesList.get(buttonId), isEditor, index, names).build());
                    }
                });

        NbtCompound playerData = PlayerDataCache.getPlayer(profile);
        NbtList places = playerData.getList("places", NbtElement.COMPOUND_TYPE);
        for (NbtElement p : places) {
            NbtCompound place = (NbtCompound) p;
            String name = place.getString("name");
            String color = place.getString("color");
            boolean show = place.getBoolean("visibility") || isOwner || isEditor;
            placesList.add(place);
            names.add(name);
            placesform.optionalButton(ColorUtil.getColor(color) + name, show);
        }
        boolean show = placesList.size() < 10 && isOwner; //make sure there are less than 10 places
        placesform.optionalButton("Add Place", show && !isEditor);
        placesform.optionalButton("Editor mode", isOwner && !isEditor);
        placesform.optionalButton("Exit editor mode", isEditor);
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
            parsed.add(String.valueOf(isOwner)); //owner mode on
            parsed.add("false"); //editor mode off
        } else {
            parsed.add("Edit or delete your places");
            parsed.add("Select a place to edit or delete it");
            parsed.add("true"); //owner mode off
            parsed.add("true"); //editor mode on
        }
        return parsed;
    }
}