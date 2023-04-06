package onebeastchris.placebook.skin;

import com.mojang.authlib.GameProfile;
import lombok.Data;
import net.minecraft.item.ItemStack;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.util.WebUtils;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class SkinUtil {
    private ItemStack head;
    //private final Path cachePath = PlaceBook.paths.getCacheDir();
    //private String cachedHeadPath;
    private UUID uuid;
    private String name;
    private String textureId;

    private String avatarUrl;

    public SkinUtil() {

    }

    public CompletableFuture<Void> fill(GameProfile gameProfile) {
        return CompletableFuture.runAsync(() -> {
            this.uuid = gameProfile.getId();
            this.name = gameProfile.getName();
            textureId = WebUtils.getTextureID(this.uuid);
            head = Heads.getHead(gameProfile.getName(), textureId);
            this.avatarUrl = WebUtils.getAvatarUrl(this.uuid);
            //mcTexture = WebUtils.getTexture(textureId);
            //cachedHeadPath = WebUtils.handleAvatarCache(this.uuid, textureId, mcTexture);
        });
    }
}