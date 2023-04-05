package onebeastchris.placebook;

import net.fabricmc.api.ModInitializer;

import onebeastchris.placebook.util.FileUtil;
import onebeastchris.placebook.util.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceBook implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("placebook");

	public static FileUtil paths;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loading PlaceBook");
		Register.registerAll();

		paths = new FileUtil();
	}

	public static void debug(String message) {
		LOGGER.info(message);
	}
}