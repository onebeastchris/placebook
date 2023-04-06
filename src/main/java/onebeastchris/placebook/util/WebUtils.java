package onebeastchris.placebook.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.skin.Heads;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import static java.net.http.HttpClient.newHttpClient;

public class WebUtils {

    private static final String MOJANG_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String GEYSER_SKIN_API = "https://api.geysermc.org/v2/skin/";
    private static final String GEYSER_API = "https://api.geysermc.org/v2/xbox/xuid/";

    private static JsonObject webRequest(String url) {
        var client = newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> resp;
        try {
            resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            PlaceBook.LOGGER.error("Error while sending request to " + url + ": " + e);
            throw new RuntimeException(e);
        }
        return new Gson().fromJson(resp.body(), JsonObject.class);
    }

    public static String getTextureID(UUID uuid) {
        if (FloodgateUtil.isFloodgatePlayer(uuid) || uuid.version() == 0) {
            if (!FloodgateUtil.isLinked(uuid)) {
                PlaceBook.debug("Player is linked to bedrock account, using java texture id");
                return getBedrockTexture(uuid);
            }
        }
        return getJavaTexture(uuid);
    }

    private static String getBedrockTexture(UUID uuid) {
        String xuid = FloodgateUtil.getXuid(uuid);
        if (xuid == null) {
            //needed when player is offline
            xuid = getXuid(uuid);
            assert xuid != null;
        }
        JsonObject json = webRequest(GEYSER_SKIN_API + xuid);
        PlaceBook.debug("json from BEDROCK texture id web request: " + json);
        if (json.get("message") != null) {
            PlaceBook.LOGGER.error("Error while getting skin for " + uuid + ": " + json.get("message").getAsString());
            return null;
        }
        //this one is not base64 encoded...
        String texture_id = json.get("texture_id").getAsString();
        return Heads.getEncodedTexture(texture_id);
    }

    public static String getXuid(UUID uuid) {
        try {
            return webRequest(GEYSER_API + uuid.toString().replace("-", "")).get("xuid").getAsString();
        } catch (Exception e){
            return null;
        }
    }

    private static String getJavaTexture(UUID uuid) {
        JsonObject json = webRequest(MOJANG_PROFILE + uuid.toString().replace("-", ""));
        String texture_id = json.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        PlaceBook.debug("json from JAVA texture id web request: " + json);
        if (texture_id == null) {
            PlaceBook.LOGGER.error("Error while getting skin for " + uuid + ": " + json.get("message").getAsString());
            return null;
        }
        //this one is base64 encoded... yeeeeeeeeeeeeeeeeeeeeeeah
        return texture_id;
    }

    public static String handleAvatarCache(UUID uuid, String textureId, String mcTexture) {
        //check if cached skin exists; if it does, see for how long - upon player join, new skin check
        final String TydiumAPIPath = "https://api.tydiumcraft.net/v1/players/skin?uuid=";
        final String apiType = "&type=avatar";
        final Path cachePath = PlaceBook.paths.getCacheDir();

        int texturehash = mcTexture.hashCode();

        String fileName = uuid.toString() + "+" + texturehash + ".png";

        File imageFile = cachePath.resolve(fileName).toFile();
        PlaceBook.debug("checking for skin file for " + uuid.toString() + " at " + imageFile.getAbsolutePath());

        if (imageFile.exists()) {
            PlaceBook.debug("skin file for " + uuid.toString() + " exists");
            imageFile.setLastModified(System.currentTimeMillis());
            //no check necessary;
        } else {
            PlaceBook.debug("skin file for " + uuid.toString() + " does not exist");
            //find and remove old skin file, if present
            for (File file : Objects.requireNonNull(cachePath.toFile().listFiles())) {
                if (file.getName().startsWith(uuid.toString())) {
                    try {
                        PlaceBook.debug("deleting old skin file for " + uuid.toString() + " at " +file.getAbsolutePath());
                        FileUtils.forceDelete(file);
                    } catch (IOException e) {
                        PlaceBook.LOGGER.error("failed to delete old skin file for " + uuid.toString() + e);
                    }
                }
            }

            //download skin
            String url = TydiumAPIPath + uuid.toString() + apiType;
            PlaceBook.debug("downloading skin for " + uuid.toString() + " from " + url);
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) new URL(url).openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            con.setRequestProperty("User-Agent", "placebook-fabric");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            BufferedImage image;
            try {
                image = ImageIO.read(con.getInputStream());
                imageFile.createNewFile();
                ImageIO.write(image, "png", imageFile);
            } catch (IOException e) {
                PlaceBook.LOGGER.error("skin download for " + uuid.toString() + " failed " + e);
            }
        }

        return imageFile.getAbsolutePath();
    }

    public static String getTexture(String encoded) {
        var a= Base64.getDecoder().decode(encoded);
        JsonObject json = new Gson().fromJson(new String(a), JsonObject.class);
        return json.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
    }

    public static String getAvatarUrl(UUID uuid) {
        return "https://api.tydiumcraft.net/v1/players/skin?uuid=" + uuid + "&{type=avatar}";
    }
}