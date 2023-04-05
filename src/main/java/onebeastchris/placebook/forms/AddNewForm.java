package onebeastchris.placebook.forms;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerStorage;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Arrays;
import java.util.List;

public class AddNewForm {
    public static CustomForm.Builder getAddNewForm(ServerPlayerEntity player, Form previousForm, FloodgatePlayer fplayer, String... args) {
        List<String> argsList = getArgs(args);
        boolean isPrivate = argsList.get(2).equals("private");
        return CustomForm.builder()
                .title(argsList.get(0))
                .input("Name", argsList.get(1))
                .toggle("Only visible to you?", isPrivate)
                .optionalInput("description", argsList.get(3), false)
                .closedResultHandler((response) -> {
                    // do nothing
                })
                .invalidResultHandler((response) -> {
                    FloodgateUtil.sendForm(player, getAddNewForm(player, previousForm, fplayer, "Invalid entries!").build());
                })
                .validResultHandler((response) -> {
                    String name = response.asInput(0);
                    boolean isPublic = response.asToggle(1);
                    String description = response.asInput(2);
                    PlayerStorage.addNewHome(player, player.getBlockPos(), player.getWorld().getRegistryKey().toString(), name, description, isPublic, 0);
                    //send players back to previous form
                    fplayer.sendForm(previousForm);
                });
    }

    private static List<String> getArgs(String... args) {
        String[] def = {"Add New Place", "Put a name here", "visible", "Put a description here"};
        if (args.length == 0) {
            return Arrays.asList(def);
        } else {
            List<String> custom = Arrays.asList(args);
            for (int i = 0; i < custom.size(); i++) {
                if (custom.get(i) == null) {
                    custom.set(i, def[i]);
                }
            }
            return custom;
        }
    }

    public static CustomForm.Builder editHome(ServerPlayerEntity player, Form previousForm, FloodgatePlayer fplayer, NbtCompound nbt, String... args) {
        return getAddNewForm(player, previousForm, fplayer, "Edit:", nbt.getString("name"), nbt.getBoolean("isPublic") ? "visible" : "private", nbt.getString("description"));
    }
}