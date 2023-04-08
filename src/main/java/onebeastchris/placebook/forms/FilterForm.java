package onebeastchris.placebook.forms;

import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;

import java.util.ArrayList;
import java.util.List;

public class FilterForm implements FormInterface{
    public static CustomForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, String... args) {
        List<String> parsed = parseArgs(args);
        return CustomForm.builder()
                .title(parsed.get(0))
                .dropdown(parsed.get(1), "all players", "online players", "offline players")
                .input(parsed.get(2), parsed.get(3))
                .validResultHandler((response) -> {

                    Form form = PlayerListForm.sendForm(
                            player,
                            previousForm,
                            String.valueOf(response.asDropdown()),
                            response.next()

                    ).build();
                    FloodgateUtil.sendForm(player, form);
                });
    }

    private static List<String> parseArgs(String... args) {
        List<String> parsed = new ArrayList<>();
        if (!args[0].equals("")) {
            parsed.add("Filtering Options");
            parsed.add("Select which players to show");
            parsed.add("Search for a player");
            parsed.add("optional: player name");
            return parsed;
        } else {
            parsed.add("Filtering Options");
            parsed.add("Select which players to show");
            parsed.add("Search for a player");
            parsed.add("searching for: " + args[0]);
            return parsed;
        }
    }
}