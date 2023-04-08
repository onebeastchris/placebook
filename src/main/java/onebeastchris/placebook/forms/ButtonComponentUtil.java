package onebeastchris.placebook.forms;

import com.mojang.authlib.GameProfile;
import onebeastchris.placebook.util.PlayerDataCache;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

import java.util.HashMap;
import java.util.List;

public class ButtonComponentUtil {
    public static HashMap<ButtonComponent, GameProfile> getPlayers(Integer filter, String search) {
        HashMap<ButtonComponent, GameProfile> buttonMap = new HashMap<>();
        switch (filter) {
            case 0 -> {
                //all players
                if (search.isEmpty()) {
                    return search(search, PlayerDataCache.getAllPlayers());
                }
                for (GameProfile name : PlayerDataCache.getAllPlayers()) {
                    ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, PlayerDataCache.TEXTURES.get(name.getId()).getAvatarUrl());
                    buttonMap.put(button, name);
                }
            }
            case 1 -> {
                //online players
                if (search.isEmpty()) {
                    return search(search, PlayerDataCache.getOnlinePlayers());
                }
                for (GameProfile name : PlayerDataCache.getOnlinePlayers()) {
                    ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, PlayerDataCache.TEXTURES.get(name.getId()).getAvatarUrl());
                    buttonMap.put(button, name);
                }
            }
            case 2 -> {
                //offline players
                if (search.isEmpty()) {
                    return search(search, PlayerDataCache.getOfflinePlayers());
                }
                for (GameProfile name : PlayerDataCache.getOfflinePlayers()) {
                    ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, PlayerDataCache.TEXTURES.get(name.getId()).getAvatarUrl());
                    buttonMap.put(button, name);
                }
            }
        }
        return buttonMap;
    }

    private static HashMap<ButtonComponent, GameProfile> search(String search, List<GameProfile> players) {
        HashMap<ButtonComponent, GameProfile> buttons = new HashMap<>();
        for (GameProfile name : players) {
            if (name.getName().contains(search)) {
                ButtonComponent button = ButtonComponent.of(name.getName(), FormImage.Type.URL, PlayerDataCache.TEXTURES.get(name.getId()).getAvatarUrl());
                buttons.put(button, name);
            }
        }
        return buttons;
    }

}