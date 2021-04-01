package quimufu.simple_creator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

import java.util.Map;

import static quimufu.simple_creator.SimpleCreatorMod.log;


public class BlockResourceLoader extends GenericManualResourceLoader<Pair<Block, Item>> {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final String dataType = "blocks";
    public static Map<Identifier, Block> blocks = Maps.newHashMap();
    public static Map<Identifier, Item> blockItems = Maps.newHashMap();

    BlockResourceLoader() {
        super(GSON, dataType);
    }

    @Override
    protected void register(Identifier id, Pair<Block, Item> thing) {
        Registry.register(Registry.BLOCK, id, thing.getLeft());
        Registry.register(Registry.ITEM, id, thing.getRight());
    }

    @Override
    protected void save(Identifier id, Pair<Block, Item> thing) {
        blocks.put(id, thing.getLeft());
        blockItems.put(id, thing.getRight());
    }

    @Override
    protected Pair<Block, Item> deserialize(Pair<Identifier, JsonObject> e) {
        JsonObject jo = e.getRight();
        Material material;
        if(JsonHelper.hasString(jo, "material")){
            String materialString = JsonHelper.getString(jo, "material");
            material = getMaterial(materialString);
        } else if (JsonHelper.getObject(jo,"material", null)!=null){
            // get material information
            JsonObject jmo = JsonHelper.getObject(jo, "material");
            MaterialSettingsPojo mspj = GSON.fromJson(jmo, MaterialSettingsPojo.class);
            //build material
            material = getSettings(mspj);
        } else {
            material = Material.SOIL;
        }

        // get block information
        BlockSettingsPojo bspj = GSON.fromJson(jo, BlockSettingsPojo.class);

        // move block information in Block.Settings (!!hacky!!)
        Block.Settings bs = getSettings(material, bspj);

        // parse item group
        String group = JsonHelper.getString(jo, "itemGroup", "misc");
        ItemGroup g = ItemResourceLoader.findGroup(group);
        //create block and corresponding item
        Block resB = new Block(bs);
        Item resI = new BlockItem(resB, new Item.Settings().group(g));

        int burnChance = JsonHelper.getInt(jo,"burnChance", -1);
        int spreadChance = JsonHelper.getInt(jo,"spreadChance", -1);
        if(burnChance!=-1 && spreadChance!=-1){
            FlammableBlockRegistry.getDefaultInstance().add(resB, spreadChance, burnChance);
        }
        return new Pair<>(resB, resI);
    }

    private Material getSettings(MaterialSettingsPojo mspj) {
        return new Material(
                getMaterialColor(mspj.materialColor),
                mspj.liquid,
                mspj.solid,
                mspj.blocksMovement,
                mspj.blocksLight,
                mspj.breakByHand,
                mspj.burnable,
                getPistonBehavior(mspj.pistonBehavior));

    }

    private PistonBehavior getPistonBehavior(String pistonBehavior) {
        switch (pistonBehavior.toUpperCase()) {
            case "NORMAL":
                return PistonBehavior.NORMAL;
            case "DESTROY":
                return PistonBehavior.DESTROY;
            case "BLOCK":
                return PistonBehavior.BLOCK;
            case "IGNORE":
                return PistonBehavior.IGNORE;
            case "PUSH_ONLY":
                return PistonBehavior.PUSH_ONLY;
            default:
                log(Level.WARN, "Piston Behavior " + pistonBehavior + " not found, using normal");
                return PistonBehavior.NORMAL;

        }
    }

    private MaterialColor getMaterialColor(String color) {
        switch (color.toUpperCase()) {
            case "AIR":
            case "CLEAR":
                return MaterialColor.CLEAR;
            case "GRASS":
                return MaterialColor.GRASS;
            case "SAND":
                return MaterialColor.SAND;
            case "WEB":
                return MaterialColor.WEB;
            case "LAVA":
                return MaterialColor.LAVA;
            case "ICE":
                return MaterialColor.ICE;
            case "IRON":
                return MaterialColor.IRON;
            case "FOLIAGE":
                return MaterialColor.FOLIAGE;
            case "WHITE":
                return MaterialColor.WHITE;
            case "CLAY":
                return MaterialColor.CLAY;
            case "DIRT":
                return MaterialColor.DIRT;
            case "STONE":
                return MaterialColor.STONE;
            case "WATER":
                return MaterialColor.WATER;
            case "WOOD":
                return MaterialColor.WOOD;
            case "QUARTZ":
                return MaterialColor.QUARTZ;
            case "ORANGE":
                return MaterialColor.ORANGE;
            case "MAGENTA":
                return MaterialColor.MAGENTA;
            case "LIGHT_BLUE":
                return MaterialColor.LIGHT_BLUE;
            case "YELLOW":
                return MaterialColor.YELLOW;
            case "LIME":
                return MaterialColor.LIME;
            case "PINK":
                return MaterialColor.PINK;
            case "GRAY":
                return MaterialColor.GRAY;
            case "LIGHT_GRAY":
                return MaterialColor.LIGHT_GRAY;
            case "CYAN":
                return MaterialColor.CYAN;
            case "PURPLE":
                return MaterialColor.PURPLE;
            case "BLUE":
                return MaterialColor.BLUE;
            case "BROWN":
                return MaterialColor.BROWN;
            case "GREEN":
                return MaterialColor.GREEN;
            case "RED":
                return MaterialColor.RED;
            case "BLACK":
                return MaterialColor.BLACK;
            case "GOLD":
                return MaterialColor.GOLD;
            case "DIAMOND":
                return MaterialColor.DIAMOND;
            case "LAPIS":
                return MaterialColor.LAPIS;
            case "EMERALD":
                return MaterialColor.EMERALD;
            case "SPRUCE":
                return MaterialColor.SPRUCE;
            case "NETHER":
                return MaterialColor.NETHER;
            case "WHITE_TERRACOTTA":
                return MaterialColor.WHITE_TERRACOTTA;
            case "ORANGE_TERRACOTTA":
                return MaterialColor.ORANGE_TERRACOTTA;
            case "MAGENTA_TERRACOTTA":
                return MaterialColor.MAGENTA_TERRACOTTA;
            case "LIGHT_BLUE_TERRACOTTA":
                return MaterialColor.LIGHT_BLUE_TERRACOTTA;
            case "YELLOW_TERRACOTTA":
                return MaterialColor.YELLOW_TERRACOTTA;
            case "LIME_TERRACOTTA":
                return MaterialColor.LIME_TERRACOTTA;
            case "PINK_TERRACOTTA":
                return MaterialColor.PINK_TERRACOTTA;
            case "GRAY_TERRACOTTA":
                return MaterialColor.GRAY_TERRACOTTA;
            case "LIGHT_GRAY_TERRACOTTA":
                return MaterialColor.LIGHT_GRAY_TERRACOTTA;
            case "CYAN_TERRACOTTA":
                return MaterialColor.CYAN_TERRACOTTA;
            case "PURPLE_TERRACOTTA":
                return MaterialColor.PURPLE_TERRACOTTA;
            case "BLUE_TERRACOTTA":
                return MaterialColor.BLUE_TERRACOTTA;
            case "BROWN_TERRACOTTA":
                return MaterialColor.BROWN_TERRACOTTA;
            case "GREEN_TERRACOTTA":
                return MaterialColor.GREEN_TERRACOTTA;
            case "RED_TERRACOTTA":
                return MaterialColor.RED_TERRACOTTA;
            case "BLACK_TERRACOTTA":
                return MaterialColor.BLACK_TERRACOTTA;
            case "CRIMSON_NYLIUM":
                return MaterialColor.field_25702;
            case "CRIMSON_WOOD":
                return MaterialColor.field_25703;
            case "CRIMSON_HYPHAE":
                return MaterialColor.field_25704;
            case "WARPED_NYLIUM":
                return MaterialColor.field_25705;
            case "WARPED_WOOD":
                return MaterialColor.field_25706;
            case "WARPED_HYPHAE":
                return MaterialColor.field_25707;
            case "WARPED_WART_BLOCK":
                return MaterialColor.field_25708;

            default:
                log(Level.WARN, "MapColor " + color + " not found, using pink");
                return MaterialColor.PINK;
        }
    }

    private Block.Settings getSettings(Material material, BlockSettingsPojo bspj) {
        FabricBlockSettings fabricBlockSettings = FabricBlockSettings.of(material, material.getColor());
        AbstractBlockSettingsAccessor bs = (AbstractBlockSettingsAccessor) fabricBlockSettings;
        bs.setMaterial(material);
        bs.setMaterialColorFactory(ignored -> material.getColor());
        bs.setCollidable(bspj.collidable);
        fabricBlockSettings.sounds(getSoundGroup(bspj.soundGroup));
        bs.setLuminanceFunction(ignored -> bspj.lightLevel);
        bs.setResistance(bspj.explosionResistance);
        bs.setHardness(bspj.hardness);
        fabricBlockSettings.slipperiness(bspj.slipperiness);
        fabricBlockSettings.velocityMultiplier(bspj.slowDownMultiplier);
        fabricBlockSettings.jumpVelocityMultiplier(bspj.jumpVelocityMultiplier);
        bs.setLootTableId(getDropTableId(bspj.dropTableId));
        bs.setOpaque(bspj.opaque);
        return fabricBlockSettings;
    }

    private Identifier getDropTableId(String s) {
        if (s == null)
            return null;
        Identifier i = Identifier.tryParse(s);
        if (i == null) {
            log(Level.WARN, "Drop table invalid " + s + ", using default");
            i = null;
        }
        return i;
    }

    private BlockSoundGroup getSoundGroup(String s) {
        switch (s.toUpperCase()) {
            case "WOOD":
                return BlockSoundGroup.WOOD;
            case "GRAVEL":
                return BlockSoundGroup.GRAVEL;
            case "GRASS":
                return BlockSoundGroup.GRASS;
            case "LILY_PAD":
                return BlockSoundGroup.LILY_PAD;
            case "STONE":
                return BlockSoundGroup.STONE;
            case "METAL":
                return BlockSoundGroup.METAL;
            case "GLASS":
                return BlockSoundGroup.GLASS;
            case "WOOL":
                return BlockSoundGroup.WOOL;
            case "SAND":
                return BlockSoundGroup.SAND;
            case "SNOW":
                return BlockSoundGroup.SNOW;
            case "LADDER":
                return BlockSoundGroup.LADDER;
            case "ANVIL":
                return BlockSoundGroup.ANVIL;
            case "SLIME":
                return BlockSoundGroup.SLIME;
            case "HONEY":
                return BlockSoundGroup.HONEY;
            case "WET_GRASS":
                return BlockSoundGroup.WET_GRASS;
            case "CORAL":
                return BlockSoundGroup.CORAL;
            case "BAMBOO":
                return BlockSoundGroup.BAMBOO;
            case "BAMBOO_SAPLING":
                return BlockSoundGroup.BAMBOO_SAPLING;
            case "SCAFFOLDING":
                return BlockSoundGroup.SCAFFOLDING;
            case "SWEET_BERRY_BUSH":
                return BlockSoundGroup.SWEET_BERRY_BUSH;
            case "CROP":
                return BlockSoundGroup.CROP;
            case "STEM":
                return BlockSoundGroup.STEM;
            case "VINE":
                return BlockSoundGroup.VINE;
            case "NETHER_WART":
                return BlockSoundGroup.NETHER_WART;
            case "LANTERN":
                return BlockSoundGroup.LANTERN;
            case "NETHER_STEM":
                return BlockSoundGroup.NETHER_STEM;
            case "NYLIUM":
                return BlockSoundGroup.NYLIUM;
            case "FUNGUS":
                return BlockSoundGroup.FUNGUS;
            case "ROOTS":
                return BlockSoundGroup.ROOTS;
            case "SHROOMLIGHT":
                return BlockSoundGroup.SHROOMLIGHT;
            case "WEEPING_VINES":
                return BlockSoundGroup.WEEPING_VINES;
            case "WEEPING_VINES_LOW_PITCH":
                return BlockSoundGroup.WEEPING_VINES_LOW_PITCH;
            case "SOUL_SAND":
                return BlockSoundGroup.SOUL_SAND;
            case "SOUL_SOIL":
                return BlockSoundGroup.SOUL_SOIL;
            case "BASALT":
                return BlockSoundGroup.BASALT;
            case "WART_BLOCK":
                return BlockSoundGroup.WART_BLOCK;
            case "NETHERRACK":
                return BlockSoundGroup.NETHERRACK;
            case "NETHER_BRICKS":
                return BlockSoundGroup.NETHER_BRICKS;
            case "NETHER_SPROUTS":
                return BlockSoundGroup.NETHER_SPROUTS;
            case "NETHER_ORE":
                return BlockSoundGroup.NETHER_ORE;
            case "BONE":
                return BlockSoundGroup.BONE;
            case "NETHERITE":
                return BlockSoundGroup.NETHERITE;
            case "ANCIENT_DEBRIS":
                return BlockSoundGroup.ANCIENT_DEBRIS;
            case "LODESTONE":
                return BlockSoundGroup.LODESTONE;
            case "CHAIN":
                return BlockSoundGroup.CHAIN;
            case "NETHER_GOLD_ORE":
                return BlockSoundGroup.NETHER_GOLD_ORE;
            case "GILDED_BLACKSTONE":
                return BlockSoundGroup.GILDED_BLACKSTONE;
            default:
                log(Level.WARN, "Sound group " + s + " not found, using stone");
                return BlockSoundGroup.STONE;
        }
    }

    private Material getMaterial(String s) {
        switch (s.toUpperCase()) {
            case "AIR":
                return Material.AIR;
            case "STRUCTURE_VOID":
                return Material.STRUCTURE_VOID;
            case "PORTAL":
                return Material.PORTAL;
            case "CARPET":
                return Material.CARPET;
            case "PLANT":
                return Material.PLANT;
            case "UNDERWATER_PLANT":
                return Material.UNDERWATER_PLANT;
            case "REPLACEABLE_PLANT":
                return Material.REPLACEABLE_PLANT;
            case "NETHER_SHOOTS":
                return Material.NETHER_SHOOTS;
            case "REPLACEABLE_UNDERWATER_PLANT":
            case "SEAGRASS":
                return Material.REPLACEABLE_UNDERWATER_PLANT;
            case "WATER":
                return Material.WATER;
            case "BUBBLE_COLUMN":
                return Material.BUBBLE_COLUMN;
            case "LAVA":
                return Material.LAVA;
            case "SNOW_LAYER":
            case "SNOW":
                return Material.SNOW_LAYER;
            case "FIRE":
                return Material.FIRE;
            case "SUPPORTED":
            case "PART":
                return Material.SUPPORTED;
            case "COBWEB":
                return Material.COBWEB;
            case "REDSTONE_LAMP":
                return Material.REDSTONE_LAMP;
            case "ORGANIC_PRODUCT":
            case "CLAY":
                return Material.ORGANIC_PRODUCT;
            case "SOIL":
            case "EARTH":
                return Material.SOIL;
            case "SOLID_ORGANIC":
            case "ORGANIC":
                return Material.SOLID_ORGANIC;
            case "DENSE_ICE":
            case "PACKED_ICE":
                return Material.DENSE_ICE;
            case "AGGREGATE":
            case "SAND":
                return Material.AGGREGATE;
            case "SPONGE":
                return Material.SPONGE;
            case "SHULKER_BOX":
                return Material.SHULKER_BOX;
            case "WOOD":
                return Material.WOOD;
            case "NETHER_WOOD":
                return Material.NETHER_WOOD;
            case "BAMBOO_SAPLING":
                return Material.BAMBOO_SAPLING;
            case "BAMBOO":
                return Material.BAMBOO;
            case "WOOL":
                return Material.WOOL;
            case "TNT":
                return Material.TNT;
            case "LEAVES":
                return Material.LEAVES;
            case "GLASS":
                return Material.GLASS;
            case "ICE":
                return Material.ICE;
            case "CACTUS":
                return Material.CACTUS;
            case "STONE":
                return Material.STONE;
            case "METAL":
                return Material.METAL;
            case "SNOW_BLOCK":
                return Material.SNOW_BLOCK;
            case "REPAIR_STATION":
            case "ANVIL":
                return Material.REPAIR_STATION;
            case "BARRIER":
                return Material.BARRIER;
            case "PISTON":
                return Material.PISTON;
            case "UNUSED_PLANT":
                return Material.UNUSED_PLANT;
            case "GOURD":
            case "PUMPKIN":
                return Material.GOURD;
            case "EGG":
                return Material.EGG;
            case "CAKE":
                return Material.CAKE;
            default:
                log(Level.WARN, "Material " + s + " not found, using stone");
                return Material.STONE;

        }
    }

}
