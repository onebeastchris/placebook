package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.List;

public class EditOrDelete implements FormInterface {
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, Form deleteFallBack, NbtCompound nbtCompound, List<String> names, int index) {
        String placeName = names.get(index);
        return SimpleForm.builder()
                .title("Edit or Delete")
                .content("What would you like to do with " + placeName + "?")
                .button("Edit")
                .button("Delete")
                .button("Back")
                .validResultHandler((form, response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> {
                            //edit place
                            FloodgateUtil.sendForm(player, addOrEditForm.sendForm(player, deleteFallBack, nbtCompound, index, names, "Edit:").build());
                        }
                        case 1 -> {
                            //delete place
                            FloodgateUtil.sendForm(player, DeleteForm.sendForm(player, deleteFallBack, placeName, index).build());
                        }
                        case 2 -> {
                            //go back
                            FloodgateUtil.sendForm(player, previousForm);
                        }
                    }
                });
    }
}