package quimufu.simple_creator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
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

import java.lang.reflect.Field;
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

        PistonBehavior pistonBehavior;
        boolean blocksMovement;
        boolean burnable;
        boolean breakByHand;
        boolean liquid;
        boolean replaceable;
        boolean solid;
        MaterialColor color;
        boolean blocksLight;
        Material material;
        String materialStr = JsonHelper.getString(jo, "material", "stone");
        material = getMaterial(materialStr);


        // construct block information
        BlockSoundGroup soundGroup;
        Identifier dropTableId;
        boolean collidable;
        int luminance;
        float resistance;
        float hardness;
        float slipperiness;
        float slowDownMultiplier;
        float jumpVelocityMultiplier;
        boolean opaque;

        String soundGroupStr = JsonHelper.getString(jo, "soundGroup", "stone");
        soundGroup = getSoundGroup(soundGroupStr);
        String dropTableIdStr = JsonHelper.getString(jo, "dropTableId", null);
        dropTableId = getDropTableId(dropTableIdStr);
        collidable = JsonHelper.getBoolean(jo, "collidable", true);
        luminance = JsonHelper.getInt(jo, "lightLevel", 0);
        resistance = JsonHelper.getFloat(jo, "explosionResistance", 6.0F);
        hardness = JsonHelper.getFloat(jo, "hardness", 1.5F);
        slipperiness = JsonHelper.getFloat(jo, "slipperiness", 0.6F);
        slowDownMultiplier = JsonHelper.getFloat(jo, "slowDownMultiplier", 1.0F);
        jumpVelocityMultiplier = JsonHelper.getFloat(jo, "jumpVelocityMultiplier", 1.0F);
        opaque = JsonHelper.getBoolean(jo, "opaque", true);

        // save block information in Block.Settings (!!hacky!!)
        Block.Settings bs = Block.Settings.of(material, material.getColor());
        Field[] fields = Block.Settings.class.getDeclaredFields();
        try {
            fields[0].setAccessible(true);
            fields[0].set(bs, material);
            fields[1].setAccessible(true);
            fields[1].set(bs, material.getColor());
            fields[2].setAccessible(true);
            fields[2].setBoolean(bs, collidable);
            fields[3].setAccessible(true);
            fields[3].set(bs, soundGroup);
            fields[4].setAccessible(true);
            fields[4].setInt(bs, luminance);
            fields[5].setAccessible(true);
            fields[5].setFloat(bs, resistance);
            fields[6].setAccessible(true);
            fields[6].setFloat(bs, hardness);
            fields[8].setAccessible(true);
            fields[8].setFloat(bs, slipperiness);
            fields[9].setAccessible(true);
            fields[9].setFloat(bs, slowDownMultiplier);
            fields[10].setAccessible(true);
            fields[10].setFloat(bs, jumpVelocityMultiplier);
            fields[11].setAccessible(true);
            fields[11].set(bs, dropTableId);
            fields[12].setAccessible(true);
            fields[12].setBoolean(bs, opaque);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        // parse item group
        String group = JsonHelper.getString(jo, "group", "misc");
        ItemGroup g = ItemResourceLoader.findGroup(group);
        //create block and corresponding item
        Block resB = new Block(bs);
        Item resI = new BlockItem(resB, new Item.Settings().group(g));

        return new Pair<>(resB, resI);
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
            case "NETHER_WART":
                return BlockSoundGroup.NETHER_WART;
            case "LANTERN":
                return BlockSoundGroup.LANTERN;
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
            case "SEAGRASS":
                return Material.SEAGRASS;
            case "WATER":
                return Material.WATER;
            case "BUBBLE_COLUMN":
                return Material.BUBBLE_COLUMN;
            case "LAVA":
                return Material.LAVA;
            case "SNOW":
                return Material.SNOW;
            case "FIRE":
                return Material.FIRE;
            case "PART":
                return Material.PART;
            case "COBWEB":
                return Material.COBWEB;
            case "REDSTONE_LAMP":
                return Material.REDSTONE_LAMP;
            case "CLAY":
                return Material.CLAY;
            case "EARTH":
                return Material.EARTH;
            case "ORGANIC":
                return Material.ORGANIC;
            case "PACKED_ICE":
                return Material.PACKED_ICE;
            case "SAND":
                return Material.SAND;
            case "SPONGE":
                return Material.SPONGE;
            case "SHULKER_BOX":
                return Material.SHULKER_BOX;
            case "WOOD":
                return Material.WOOD;
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
            case "ANVIL":
                return Material.ANVIL;
            case "BARRIER":
                return Material.BARRIER;
            case "PISTON":
                return Material.PISTON;
            case "UNUSED_PLANT":
                return Material.UNUSED_PLANT;
            case "PUMPKIN":
                return Material.PUMPKIN;
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
