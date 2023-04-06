package onebeastchris.placebook.forms;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListForm implements FormInterface {
    public static SimpleForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, String... args) {
        List<String> parsed = parseArgs(args);
        int filter = Integer.parseInt(parsed.get(0));
        String searchParam = parsed.get(1);

        SimpleForm.Builder playerlistform = SimpleForm.builder()
                .title(parsed.get(2))
                .content(parsed.get(3))
                .button("Filter/Search")
                .validResultHandler((form, response) -> {
                    if (response.clickedButtonId() == 0) {
                        FloodgateUtil.sendForm(player, FilterForm.sendForm(player, previousForm, searchParam).build()); //handle filter call
                    } else if (response.clickedButton().text().equals("Back")) {
                        FloodgateUtil.sendForm(player, previousForm); //handle back call
                    } else {
                        //call places form
                    }
                });

        HashMap<ButtonComponent, GameProfile> buttons = ButtonComponentUtil.getButtonComponent(filter, searchParam);
        for (ButtonComponent button : buttons.keySet()) {
            playerlistform.button(button);
        }
        playerlistform.button("Back");
        return playerlistform;
    }

    private static List<String> parseArgs(String... args) {
        List<String> parsed = new ArrayList<>();
        if (args.length == 0) {
            parsed.add("0"); //default: load all players
            parsed.add(""); //default: no search param
            parsed.add("Showing all players");
            parsed.add("You can search or filter for players.");
        } else {
            parsed.add(args[0]); //which toggle?
            parsed.add(args[1]); //search param, if present
            parsed.add("Showing " + parseInt(args[0])); //title
            if (args[1].equals("")) {
                parsed.add("You can search for a player name!"); //content
            } else {
                parsed.add("Searching for \"" + args[1] + "\""); //content
            }
        }
        return parsed;
    }

    private static String parseInt(String filter) {
        return switch (filter) {
            case "1" -> "online players";
            case "2" -> "offline players";
            default -> "all players";
        };
    }

}