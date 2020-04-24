package quimufu.simple_creator;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "simple_creator")
public class SimpleCreatorConfig  implements ConfigData {
    public boolean enableTestThings = false;
    public boolean extendedLogging = false;
}
