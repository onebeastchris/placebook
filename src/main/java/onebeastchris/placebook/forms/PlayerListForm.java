package onebeastchris.placebook.forms;

import com.mojang.authlib.GameProfile;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerListForm {
    public static SimpleForm.Builder getPlayerListForm(boolean onlyOnline, @Nullable String search, Form previousForm, FloodgatePlayer player) {
        return SimpleForm.builder()
                .title("Player List")
                .content("This is a list of all players on the server.")
                .button("Online Players")
                .button("Offline Players")
                .button("Search")
                .button("Back")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> {
                            //online players
                        }
                        case 1 -> {
                            //offline players
                        }
                        case 2 -> {
                            //search
                        }
                        case 3 -> {
                            //back
                            player.sendForm(previousForm);
                        }
                    }
                })
                .closedOrInvalidResultHandler((response) -> {

                });
    }

    private static SimpleForm.Builder getAllPlayersForm() {
        return SimpleForm.builder()
                .title("Online Players")
                .content("This is a list of all online players on the server.")
                .button("Back")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> {
                            //back
                        }
                    }
                });
    }

    private static SimpleForm.Builder getOnlinePlayers() {
        SimpleForm.Builder simpleform = SimpleForm.builder()
                .title("Offline Players")
                .content("This is a list of all offline players on the server.")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> {
                            //back
                        }
                    }
                });
        var a = ButtonComponentUtil.getButtonComponent(1, null);
        for (ButtonComponent button : a) {
            simpleform.button(button);
        }
        //add buttons
        return simpleform;
    }



}