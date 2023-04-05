package onebeastchris.placebook.skin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import onebeastchris.placebook.PlaceBook;

import java.util.Base64;

public class Heads {
    public static ItemStack getHead(String name, String encodedTexture) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound headNbt = new NbtCompound();

        NbtCompound displayNbt = new NbtCompound();
        displayNbt.putString("Name", getStyledText(name));

        NbtCompound skullOwnerNbt = new NbtCompound();
        skullOwnerNbt.putIntArray("Id", new int[]{67, 54, 25, 9});
        NbtCompound propertiesNbt = new NbtCompound();
        NbtList texturesNbt = new NbtList();
        NbtCompound texture = new NbtCompound();

        texture.putString("Value", encodedTexture);

        texturesNbt.add(texture);
        propertiesNbt.put("textures", texturesNbt);
        skullOwnerNbt.put("Properties", propertiesNbt);

        headNbt.put("display", displayNbt);
        headNbt.put("SkullOwner", skullOwnerNbt);

        head.setNbt(headNbt);
        return head;
    }

    private static String getStyledText(String text) {
        return Text.Serializer.toJson(Text.literal(text).styled(style -> style.withColor(Formatting.BLACK)));
    }

    public static String getEncodedTexture(String textureID) {
        try {
            String toBeEncoded = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/" + textureID + "\"}}}";
            return Base64.getEncoder().encodeToString(toBeEncoded.getBytes());
        } catch (Exception e) {
            PlaceBook.LOGGER.error("Error while encoding texture ID: " + e);
            return null;
        }
    }
}