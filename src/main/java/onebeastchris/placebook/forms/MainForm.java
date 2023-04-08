package onebeastchris.placebook.forms;

import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.UUID;

public class MainForm implements FormInterface {

    public static SimpleForm.Builder sendForm(ServerPlayerEntity player) {
        String url = PlayerDataCache.TEXTURES.get(player.getUuid()).getAvatarUrl();
        boolean showUnpin = false;
        String unpin = "";
        if (PlayerDataCache.messageCache.get(player) != null) {
            showUnpin = true;
            unpin = "Unpin actionbar message";
        }

        return SimpleForm.builder()
                .title("PlaceBook")
                .button("Your Places", FormImage.Type.URL, url)
                .button("Players")
                .optionalButton("Places", false) //TODO: add places
                .optionalButton(unpin, showUnpin)
                .validResultHandler((form, response) -> {
                    switch (response.clickedButtonId()) {
                        case 0:
                            //YourPlacesForm
                            FloodgateUtil.sendForm(player, PlacesForm.sendForm(player, form, player.getGameProfile()).build());
                            break;
                        case 1:
                            //PlayerListForm
                            FloodgateUtil.sendForm(player, PlayerListForm.sendForm(player, form).build());
                            break;
                        case 2:
                            //player.sendForm(YourPlacesForm.yourPlacesForm(uuid, player));
                            break;
                        case 3:
                            //unpin
                            PlayerDataCache.messageCache.remove(player);
                            FloodgateUtil.sendForm(player, sendForm(player).build());
                            break;
                    }
                });
    }
}