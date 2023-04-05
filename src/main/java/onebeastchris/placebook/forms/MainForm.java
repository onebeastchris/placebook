package onebeastchris.placebook.forms;

import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class MainForm {

    public static SimpleForm.Builder mainForm(UUID uuid, FloodgatePlayer player) {
        String path = PlayerDataCache.TEXTURES.get(uuid).getCachedHeadPath();
        PlaceBook.debug("Path: " + path);
        return SimpleForm.builder()
                .title("PlaceBook")
                .button("Your Places", FormImage.Type.PATH, path)
                .button("Players")
                .button("Places")
                .validResultHandler((form, response) -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            //player.sendForm(PlayersForm.playersForm(uuid, player));
                            break;
                        case 2:
                            //player.sendForm(PlacesForm.placesForm(uuid, player));
                            break;
                        case 3:
                            //player.sendForm(YourPlacesForm.yourPlacesForm(uuid, player));
                            break;
                    }
                });
    }
}