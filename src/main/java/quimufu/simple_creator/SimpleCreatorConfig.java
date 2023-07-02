package quimufu.simple_creator;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed4.annotated.AConfigEntry;
import de.siphalor.tweed4.annotated.ATweedConfig;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;


@ATweedConfig(serializer = "tweed4:hjson", scope = ConfigScope.GAME, environment = ConfigEnvironment.UNIVERSAL, tailors = {"tweed4:lang_json_descriptions", "tweed4:coat", "tweed4:json_schema"}, casing = CaseFormat.LOWER_HYPHEN)
//@ClothData(modid = "tweed4_testmod")
public class SimpleCreatorConfig {

	@AConfigEntry(name = "enableTestThings", comment = "Enables included test Blocks and Items")
    public static boolean enableTestThings = false;


    public static boolean extendedLogging = false;
}
