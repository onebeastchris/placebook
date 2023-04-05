package onebeastchris.placebook.forms;

import com.mojang.authlib.GameProfile;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.List;

public class ButtonComponentUtil {
    public static List<ButtonComponent> getButtonComponent(int what, String search) {
        List<ButtonComponent> buttons = new ArrayList<>();
        switch (what) {
            case 0 -> {
                //all players
                for (GameProfile name : PlayerDataCache.getAllPlayers(null)) {
                    ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, "https://api.tydiumcraft.net/v1/players/skin?uuid=" + name.getId() + "&type=avatar");
                    buttons.add(button);
                }
                return buttons;
            }
            case 1 -> {
                //online players
                for (GameProfile name : PlayerDataCache.getOnlinePlayers()) {
                    ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, "https://api.tydiumcraft.net/v1/players/skin?uuid=" + name.getId() + "&type=avatar");
                    buttons.add(button);
                }
                return buttons;
            }
            case 2 -> {
                //search
                return search(search, PlayerDataCache.getAllPlayers(search));
            }
        }
        return buttons;
    }

    private static List<ButtonComponent> search(String search, List<GameProfile> players) {
        List<ButtonComponent> buttons = new ArrayList<>();
        for (GameProfile name : players) {
            if (name.getName().contains(search)) {
                ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, "https://api.tydiumcraft.net/v1/players/skin?uuid=" + name.getId() + "&type=avatar");
                buttons.add(button);
            }
        }
        return buttons;
    }
}