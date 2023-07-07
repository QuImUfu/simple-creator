package quimufu.simple_creator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;

@Environment(EnvType.CLIENT)
public class SimpleCreatorModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SimpleCreatorMod.log(Level.INFO, "Client init");
        for (Pair<Block, String> blockRenderLayerPair : SimpleCreatorMod.BLOCKS_RENDER_LAYER) {
            BlockRenderLayerMap.INSTANCE.putBlock(blockRenderLayerPair.getLeft(), getRenderLayer(blockRenderLayerPair.getRight()));
        }
    }

    private net.minecraft.client.render.RenderLayer getRenderLayer(String renderLayer) {
        switch (renderLayer.toUpperCase()){
            case "SOLID":
                return RenderLayer.getSolid();
            case "CUTOUT_MIPPED":
                return RenderLayer.getCutoutMipped();
            case "CUTOUT":
                return RenderLayer.getCutout();
            case "TRANSLUCENT":
                return RenderLayer.getTranslucent();
            case "TRANSLUCENT_MOVING_BLOCK":
                return RenderLayer.getTranslucentMovingBlock();
            case "TRANSLUCENT_NO_CRUMBLING":
                return RenderLayer.getTranslucentNoCrumbling();
            case "LEASH":
                return RenderLayer.getLeash();
            case "WATER_MASK":
                return RenderLayer.getWaterMask();
            case "ARMOR_GLINT":
                return RenderLayer.getArmorGlint();
            case "ARMOR_ENTITY_GLINT":
                return RenderLayer.getArmorEntityGlint();
            case "GLINT_TRANSLUCENT":
                return RenderLayer.getGlintTranslucent();
            case "GLINT":
                return RenderLayer.getGlint();
            case "DIRECT_GLINT":
                return RenderLayer.getDirectGlint();
            case "ENTITY_GLINT":
                return RenderLayer.getEntityGlint();
            case "DIRECT_ENTITY_GLINT":
                return RenderLayer.getDirectEntityGlint();
            case "TEXT_BACKGROUND":
                return RenderLayer.getTextBackground();
            case "TEXT_BACKGROUND_SEE_THROUGH":
                return RenderLayer.getTextBackgroundSeeThrough();
            case "LIGHTNING":
                return RenderLayer.getLightning();
            case "TRIPWIRE":
                return RenderLayer.getTripwire();
            case "END_PORTAL":
                return RenderLayer.getEndPortal();
            case "END_GATEWAY":
                return RenderLayer.getEndGateway();
            case "LINES":
                return RenderLayer.getLines();
            case "LINE_STRIP":
                return RenderLayer.getLineStrip();
            case "DEBUG_FILLED_BOX":
                return RenderLayer.getDebugFilledBox();
            case "DEBUG_QUADS":
                return RenderLayer.getDebugQuads();
            case "DEBUG_SECTION_QUADS":
                return RenderLayer.getDebugSectionQuads();
            case "GUI":
                return RenderLayer.getGui();
            case "GUI_OVERLAY":
                return RenderLayer.getGuiOverlay();
            case "GUI_TEXT_HIGHLIGHT":
                return RenderLayer.getGuiTextHighlight();
            case "GUI_GHOST_RECIPE_OVERLAY":
                return RenderLayer.getGuiGhostRecipeOverlay();
            default:
                SimpleCreatorMod.log(Level.INFO, "Could not find renderLayer " + renderLayer + " using solid");
                return RenderLayer.getSolid();
        }
    }
}
