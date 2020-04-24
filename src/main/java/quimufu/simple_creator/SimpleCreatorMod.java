package quimufu.simple_creator;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import net.minecraft.block.Material;

//import java.lang.reflect.Field;

public class SimpleCreatorMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();
    public static ItemResourceLoader irl = new ItemResourceLoader();
    public static BlockResourceLoader brl = new BlockResourceLoader();

    public static final String MOD_ID = "simple_creator";
    public static final String MOD_NAME = "Simple Item/Block Creator";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(SimpleCreatorConfig.class, GsonConfigSerializer::new);
//        for(Material m : Material.class.getEnumConstants()){
//            log(Level.INFO,  String.valueOf(m.getColor().color));
//        }
//        for (Field f : Material.class.getDeclaredFields()) {
//            log(Level.INFO, f.getName());
//            try {
//                Material m = ((Material) f.get(Material.class));
//                log(Level.INFO, "pistonBehavior: " + m.getPistonBehavior().name());
//                log(Level.INFO, "blocksMovement: " + m.blocksMovement());
//                log(Level.INFO, "burnable: " + m.isBurnable());
//                log(Level.INFO, "breakByHand: " + m.canBreakByHand());
//                log(Level.INFO, "liquid: " + m.isLiquid());
//                log(Level.INFO, "replaceable: " + m.isReplaceable());
//                log(Level.INFO, "solid: " + m.isSolid());
//                log(Level.INFO, "blocksLight: " + m.blocksLight());
//                log(Level.INFO, "");
//                log(Level.INFO, "");
//
//            } catch (IllegalAccessException ignored) {
//            }
//        }
        irl.load();
        brl.load();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}