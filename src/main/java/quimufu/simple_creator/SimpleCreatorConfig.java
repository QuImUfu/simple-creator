package quimufu.simple_creator;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "simple_creator")
public class SimpleCreatorConfig  implements ConfigData {
    public boolean enableTestThings = false;
    public boolean extendedLogging = false;
}
