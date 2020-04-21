package quimufu.simple_creator;

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;

public class FlexibleMaterialBuilder extends Material.Builder {

    public FlexibleMaterialBuilder(MaterialColor color) {
        super(color);
    }

    @Override
    public Material.Builder blocksPistons() {
        return super.blocksPistons();
    }

    @Override
    public Material.Builder burnable() {
        return super.burnable();
    }

    @Override
    public Material.Builder destroyedByPiston() {
        return super.destroyedByPiston();
    }

    @Override
    public Material.Builder requiresTool() {
        return super.requiresTool();
    }

}
