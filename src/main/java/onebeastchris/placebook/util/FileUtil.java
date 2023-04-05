package onebeastchris.placebook.util;

import net.fabricmc.loader.api.FabricLoader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtil {

    private final Path configDir;
    private final Path cacheDir;

    public FileUtil() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("placebook");
        cacheDir = configDir.resolve("cache");

        if (!configDir.toFile().exists()) {
            var w =configDir.toFile().mkdirs();
            if (!w) {
                throw new RuntimeException("Could not create config directory" + configDir.toString());
            }
        }

        if (!cacheDir.toFile().exists()) {
            var w = cacheDir.toFile().mkdirs();
            if (!w) {
                throw new RuntimeException("Could not create cache directory" + cacheDir.toString());
            }
        }
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getCacheDir() {
        return cacheDir;
    }

    public Path doesPathExist(String name) {
        Path path = cacheDir.resolve(name + ".png");
        if (path.toFile().exists()) {
            return path;
        }
        return null;
    }
}