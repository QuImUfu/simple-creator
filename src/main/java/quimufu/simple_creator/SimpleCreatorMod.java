package quimufu.simple_creator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SimpleCreatorMod implements ModInitializer {

    public static List<Pair<Block, String>> BLOCKS_RENDER_LAYER = new ArrayList<>();
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