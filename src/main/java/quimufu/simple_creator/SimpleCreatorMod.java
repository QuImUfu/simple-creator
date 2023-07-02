package quimufu.simple_creator;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleCreatorMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();
    public static ItemResourceLoader irl = new ItemResourceLoader();
    public static BlockResourceLoader brl = new BlockResourceLoader();

    public static final String MOD_ID = "simple_creator";
    public static final String MOD_NAME = "Simple Item/Block Creator";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        irl.load();
        brl.load();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}