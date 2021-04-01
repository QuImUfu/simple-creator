package quimufu.simple_creator;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class SimpleCreatorModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::getScreen;
    }

    private Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(SimpleCreatorConfig.class, parent).get();
    }


}
