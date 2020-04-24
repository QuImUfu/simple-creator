package quimufu.simple_creator;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class SimpleCreatorModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return SimpleCreatorMod.MOD_ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::getScreen;
    }

    private Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(SimpleCreatorConfig.class, parent).get();
    }


}
