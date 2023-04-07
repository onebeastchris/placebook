package onebeastchris.placebook.util;

import java.util.HashMap;

public class ColorUtil {
    public static HashMap<String, String> colorMap = new HashMap<>();
    public static final String[] colorcodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
    public static final String[] colornames = {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow"};

    public ColorUtil() {
        colorMap.put("black", "§0");
        colorMap.put("dark_blue", "§1");
        colorMap.put("dark_green", "§2");
        colorMap.put("dark_aqua", "§3");
        colorMap.put("dark_red", "§4");
        colorMap.put("dark_purple", "§5");
        colorMap.put("gold", "§6");
        colorMap.put("gray", "§7");
        colorMap.put("dark_gray", "§8");
        colorMap.put("blue", "§9");
        colorMap.put("green", "§a");
        colorMap.put("aqua", "§b");
        colorMap.put("red", "§c");
        colorMap.put("light_purple", "§d");
        colorMap.put("yellow", "§e");
        colorMap.put("white", "§f");
    }
}