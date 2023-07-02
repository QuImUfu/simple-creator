package quimufu.simple_creator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
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
        Registry.register(Registries.BLOCK, id, thing.getLeft());
        Registry.register(Registries.ITEM, id, thing.getRight());
    }

    @Override
    protected void save(Identifier id, Pair<Block, Item> thing) {
        blocks.put(id, thing.getLeft());
        blockItems.put(id, thing.getRight());
    }

    @Override
    protected Pair<Block, Item> deserialize(Pair<Identifier, JsonObject> e) {
        JsonObject jo = e.getRight();

        // get block information
        BlockSettingsPojo bspj = GSON.fromJson(jo, BlockSettingsPojo.class);

        // move block information in Block.Settings (!!hacky!!)
        Block.Settings bs = getSettings(bspj);

        // parse item group
        String group = bspj.itemGroup;
        RegistryKey<ItemGroup> g = ItemResourceLoader.findGroup(group);
        //create block and corresponding item
        Block resB = new Block(bs);
        Item resI = new BlockItem(resB, new FabricItemSettings());
        ItemGroupEvents.modifyEntriesEvent(g).register(content -> content.add(resI));

        int burnChance = bspj.burnChance;
        int spreadChance = bspj.spreadChance;
        if (burnChance != -1 && spreadChance != -1) {
            FlammableBlockRegistry.getDefaultInstance().add(resB, spreadChance, burnChance);
        }
        return new Pair<>(resB, resI);
    }

    private Block.Settings getSettings(BlockSettingsPojo bspj) {
        FabricBlockSettings fabricBlockSettings = FabricBlockSettings.create();

        fabricBlockSettings
                .collidable(bspj.collidable)
                .slipperiness(bspj.slipperiness)
                .velocityMultiplier(bspj.slowDownMultiplier)
                .jumpVelocityMultiplier(bspj.jumpVelocityMultiplier)
                .sounds(getSoundGroup(bspj.soundGroup))
                .drops(getDropTableId(bspj.dropTableId))
                .mapColor(getMapColor(bspj.mapColor))
                .allowsSpawning(bspj.allowsSpawning ? BlockResourceLoader::always : BlockResourceLoader::never)
                .solidBlock(bspj.solidBlock ? BlockResourceLoader::always : BlockResourceLoader::never)
                .suffocates(bspj.suffocates ? BlockResourceLoader::always : BlockResourceLoader::never)
                .blockVision(bspj.blockVision ? BlockResourceLoader::always : BlockResourceLoader::never)
                .postProcess(bspj.postProcess ? BlockResourceLoader::always : BlockResourceLoader::never)
                .emissiveLighting(bspj.emissiveLighting ? BlockResourceLoader::always : BlockResourceLoader::never)
                .hardness(bspj.hardness)
                .resistance(bspj.explosionResistance)
                .offset(getOffset(bspj.modelOffset))
                .pistonBehavior(getPistonBehavior(bspj.pistonBehavior))
                .instrument(getInstrument(bspj.instrument))
                .luminance(bspj.lightLevel);
        if (bspj.burnable) {
            fabricBlockSettings.burnable();
        }
        //not supported for now
        //fabricBlockSettings.liquid()
        if (bspj.solidBlock) {
            fabricBlockSettings.solid();
        } else {
            fabricBlockSettings.notSolid();
        }
        if (bspj.replaceable) {
            fabricBlockSettings.replaceable();
        }
        //not supported for now
        //fabricBlockSettings.air()

        if (bspj.noBlockBreakParticles) {
            fabricBlockSettings.noBlockBreakParticles();
        }
        if (bspj.requiresTool) {
            fabricBlockSettings.requiresTool();

        }
        if (bspj.breaksInstantly) {
            fabricBlockSettings.breakInstantly();
        }

        if (!bspj.opaque) {
            fabricBlockSettings.nonOpaque();
        }

        return fabricBlockSettings;
    }

    private Instrument getInstrument(String instrument) {
        switch (instrument.toUpperCase()) {
            case "HARP":
                return Instrument.HARP;
            case "BASEDRUM":
                return Instrument.BASEDRUM;
            case "SNARE":
                return Instrument.SNARE;
            case "HAT":
                return Instrument.HAT;
            case "BASS":
                return Instrument.BASS;
            case "FLUTE":
                return Instrument.FLUTE;
            case "BELL":
                return Instrument.BELL;
            case "GUITAR":
                return Instrument.GUITAR;
            case "CHIME":
                return Instrument.CHIME;
            case "XYLOPHONE":
                return Instrument.XYLOPHONE;
            case "IRON_XYLOPHONE":
                return Instrument.IRON_XYLOPHONE;
            case "COW_BELL":
                return Instrument.COW_BELL;
            case "DIDGERIDOO":
                return Instrument.DIDGERIDOO;
            case "BIT":
                return Instrument.BIT;
            case "BANJO":
                return Instrument.BANJO;
            case "PLING":
                return Instrument.PLING;
            case "ZOMBIE":
                return Instrument.ZOMBIE;
            case "SKELETON":
                return Instrument.SKELETON;
            case "CREEPER":
                return Instrument.CREEPER;
            case "DRAGON":
                return Instrument.DRAGON;
            case "WITHER_SKELETON":
                return Instrument.WITHER_SKELETON;
            case "PIGLIN":
                return Instrument.PIGLIN;
            case "CUSTOM_HEAD":
                return Instrument.CUSTOM_HEAD;
            default:
                log(Level.WARN, "Instrument " + instrument + " not found, using harp");
                return Instrument.HARP;
        }

    }

    private AbstractBlock.OffsetType getOffset(String modelOffset) {
        switch (modelOffset.toUpperCase()) {
            case "NONE":
                return AbstractBlock.OffsetType.NONE;
            case "XZ":
                return AbstractBlock.OffsetType.XZ;
            case "XYZ":
                return AbstractBlock.OffsetType.XYZ;
            default:
                log(Level.WARN, "ModelOffset " + modelOffset + " not found, using none");
                return AbstractBlock.OffsetType.NONE;
        }

    }

    private static boolean always(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    /**
     * A shortcut to always return {@code false} in a typed context predicate with an
     * {@link EntityType}, used like {@code settings.allowSpawning(Blocks::never)}.
     */
    private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    /**
     * A shortcut to always return {@code true} in a typed context predicate with an
     * {@link EntityType}, used like {@code settings.allowSpawning(Blocks::always)}.
     */
    private static Boolean always(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return true;
    }

    private PistonBehavior getPistonBehavior(String pistonBehavior) {
        switch (pistonBehavior.toUpperCase()) {
            case "NORMAL" -> {
                return PistonBehavior.NORMAL;
            }
            case "DESTROY" -> {
                return PistonBehavior.DESTROY;
            }
            case "BLOCK" -> {
                return PistonBehavior.BLOCK;
            }
            case "IGNORE" -> {
                return PistonBehavior.IGNORE;
            }
            case "PUSH_ONLY" -> {
                return PistonBehavior.PUSH_ONLY;
            }
            default -> {
                log(Level.WARN, "Piston Behavior " + pistonBehavior + " not found, using normal");
                return PistonBehavior.NORMAL;
            }
        }
    }


    private MapColor getMapColor(String color) {
        switch (color.toUpperCase()) {
            case "CLEAR":
                return MapColor.CLEAR;
            case "PALE_GREEN":
                return MapColor.PALE_GREEN;
            case "PALE_YELLOW":
                return MapColor.PALE_YELLOW;
            case "WHITE_GRAY":
                return MapColor.WHITE_GRAY;
            case "BRIGHT_RED":
                return MapColor.BRIGHT_RED;
            case "PALE_PURPLE":
                return MapColor.PALE_PURPLE;
            case "IRON_GRAY":
                return MapColor.IRON_GRAY;
            case "DARK_GREEN":
                return MapColor.DARK_GREEN;
            case "WHITE":
                return MapColor.WHITE;
            case "LIGHT_BLUE_GRAY":
                return MapColor.LIGHT_BLUE_GRAY;
            case "DIRT_BROWN":
                return MapColor.DIRT_BROWN;
            case "STONE_GRAY":
                return MapColor.STONE_GRAY;
            case "WATER_BLUE":
                return MapColor.WATER_BLUE;
            case "OAK_TAN":
                return MapColor.OAK_TAN;
            case "OFF_WHITE":
                return MapColor.OFF_WHITE;
            case "ORANGE":
                return MapColor.ORANGE;
            case "MAGENTA":
                return MapColor.MAGENTA;
            case "LIGHT_BLUE":
                return MapColor.LIGHT_BLUE;
            case "YELLOW":
                return MapColor.YELLOW;
            case "LIME":
                return MapColor.LIME;
            case "PINK":
                return MapColor.PINK;
            case "GRAY":
                return MapColor.GRAY;
            case "LIGHT_GRAY":
                return MapColor.LIGHT_GRAY;
            case "CYAN":
                return MapColor.CYAN;
            case "PURPLE":
                return MapColor.PURPLE;
            case "BLUE":
                return MapColor.BLUE;
            case "BROWN":
                return MapColor.BROWN;
            case "GREEN":
                return MapColor.GREEN;
            case "RED":
                return MapColor.RED;
            case "BLACK":
                return MapColor.BLACK;
            case "GOLD":
                return MapColor.GOLD;
            case "DIAMOND_BLUE":
                return MapColor.DIAMOND_BLUE;
            case "LAPIS_BLUE":
                return MapColor.LAPIS_BLUE;
            case "EMERALD_GREEN":
                return MapColor.EMERALD_GREEN;
            case "SPRUCE_BROWN":
                return MapColor.SPRUCE_BROWN;
            case "DARK_RED":
                return MapColor.DARK_RED;
            case "TERRACOTTA_WHITE":
                return MapColor.TERRACOTTA_WHITE;
            case "TERRACOTTA_ORANGE":
                return MapColor.TERRACOTTA_ORANGE;
            case "TERRACOTTA_MAGENTA":
                return MapColor.TERRACOTTA_MAGENTA;
            case "TERRACOTTA_LIGHT_BLUE":
                return MapColor.TERRACOTTA_LIGHT_BLUE;
            case "TERRACOTTA_YELLOW":
                return MapColor.TERRACOTTA_YELLOW;
            case "TERRACOTTA_LIME":
                return MapColor.TERRACOTTA_LIME;
            case "TERRACOTTA_PINK":
                return MapColor.TERRACOTTA_PINK;
            case "TERRACOTTA_GRAY":
                return MapColor.TERRACOTTA_GRAY;
            case "TERRACOTTA_LIGHT_GRAY":
                return MapColor.TERRACOTTA_LIGHT_GRAY;
            case "TERRACOTTA_CYAN":
                return MapColor.TERRACOTTA_CYAN;
            case "TERRACOTTA_PURPLE":
                return MapColor.TERRACOTTA_PURPLE;
            case "TERRACOTTA_BLUE":
                return MapColor.TERRACOTTA_BLUE;
            case "TERRACOTTA_BROWN":
                return MapColor.TERRACOTTA_BROWN;
            case "TERRACOTTA_GREEN":
                return MapColor.TERRACOTTA_GREEN;
            case "TERRACOTTA_RED":
                return MapColor.TERRACOTTA_RED;
            case "TERRACOTTA_BLACK":
                return MapColor.TERRACOTTA_BLACK;
            case "DULL_RED":
                return MapColor.DULL_RED;
            case "DULL_PINK":
                return MapColor.DULL_PINK;
            case "DARK_CRIMSON":
                return MapColor.DARK_CRIMSON;
            case "TEAL":
                return MapColor.TEAL;
            case "DARK_AQUA":
                return MapColor.DARK_AQUA;
            case "DARK_DULL_PINK":
                return MapColor.DARK_DULL_PINK;
            case "BRIGHT_TEAL":
                return MapColor.BRIGHT_TEAL;
            case "DEEPSLATE_GRAY":
                return MapColor.DEEPSLATE_GRAY;
            case "RAW_IRON_PINK":
                return MapColor.RAW_IRON_PINK;
            case "LICHEN_GREEN":
                return MapColor.LICHEN_GREEN;

            default:
                log(Level.WARN, "MapColor " + color + " not found, using pink");
                return MapColor.PINK;
        }
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
            case "INTENTIONALLY_EMPTY":
                return BlockSoundGroup.INTENTIONALLY_EMPTY;
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
            case "POWDER_SNOW":
                return BlockSoundGroup.POWDER_SNOW;
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
            case "CANDLE":
                return BlockSoundGroup.CANDLE;
            case "AMETHYST_BLOCK":
                return BlockSoundGroup.AMETHYST_BLOCK;
            case "AMETHYST_CLUSTER":
                return BlockSoundGroup.AMETHYST_CLUSTER;
            case "SMALL_AMETHYST_BUD":
                return BlockSoundGroup.SMALL_AMETHYST_BUD;
            case "MEDIUM_AMETHYST_BUD":
                return BlockSoundGroup.MEDIUM_AMETHYST_BUD;
            case "LARGE_AMETHYST_BUD":
                return BlockSoundGroup.LARGE_AMETHYST_BUD;
            case "TUFF":
                return BlockSoundGroup.TUFF;
            case "CALCITE":
                return BlockSoundGroup.CALCITE;
            case "DRIPSTONE_BLOCK":
                return BlockSoundGroup.DRIPSTONE_BLOCK;
            case "POINTED_DRIPSTONE":
                return BlockSoundGroup.POINTED_DRIPSTONE;
            case "COPPER":
                return BlockSoundGroup.COPPER;
            case "CAVE_VINES":
                return BlockSoundGroup.CAVE_VINES;
            case "SPORE_BLOSSOM":
                return BlockSoundGroup.SPORE_BLOSSOM;
            case "AZALEA":
                return BlockSoundGroup.AZALEA;
            case "FLOWERING_AZALEA":
                return BlockSoundGroup.FLOWERING_AZALEA;
            case "MOSS_CARPET":
                return BlockSoundGroup.MOSS_CARPET;
            case "PINK_PETALS":
                return BlockSoundGroup.PINK_PETALS;
            case "MOSS_BLOCK":
                return BlockSoundGroup.MOSS_BLOCK;
            case "BIG_DRIPLEAF":
                return BlockSoundGroup.BIG_DRIPLEAF;
            case "SMALL_DRIPLEAF":
                return BlockSoundGroup.SMALL_DRIPLEAF;
            case "ROOTED_DIRT":
                return BlockSoundGroup.ROOTED_DIRT;
            case "HANGING_ROOTS":
                return BlockSoundGroup.HANGING_ROOTS;
            case "AZALEA_LEAVES":
                return BlockSoundGroup.AZALEA_LEAVES;
            case "SCULK_SENSOR":
                return BlockSoundGroup.SCULK_SENSOR;
            case "SCULK_CATALYST":
                return BlockSoundGroup.SCULK_CATALYST;
            case "SCULK":
                return BlockSoundGroup.SCULK;
            case "SCULK_VEIN":
                return BlockSoundGroup.SCULK_VEIN;
            case "SCULK_SHRIEKER":
                return BlockSoundGroup.SCULK_SHRIEKER;
            case "GLOW_LICHEN":
                return BlockSoundGroup.GLOW_LICHEN;
            case "DEEPSLATE":
                return BlockSoundGroup.DEEPSLATE;
            case "DEEPSLATE_BRICKS":
                return BlockSoundGroup.DEEPSLATE_BRICKS;
            case "DEEPSLATE_TILES":
                return BlockSoundGroup.DEEPSLATE_TILES;
            case "POLISHED_DEEPSLATE":
                return BlockSoundGroup.POLISHED_DEEPSLATE;
            case "FROGLIGHT":
                return BlockSoundGroup.FROGLIGHT;
            case "FROGSPAWN":
                return BlockSoundGroup.FROGSPAWN;
            case "MANGROVE_ROOTS":
                return BlockSoundGroup.MANGROVE_ROOTS;
            case "MUDDY_MANGROVE_ROOTS":
                return BlockSoundGroup.MUDDY_MANGROVE_ROOTS;
            case "MUD":
                return BlockSoundGroup.MUD;
            case "MUD_BRICKS":
                return BlockSoundGroup.MUD_BRICKS;
            case "PACKED_MUD":
                return BlockSoundGroup.PACKED_MUD;
            case "HANGING_SIGN":
                return BlockSoundGroup.HANGING_SIGN;
            case "NETHER_WOOD_HANGING_SIGN":
                return BlockSoundGroup.NETHER_WOOD_HANGING_SIGN;
            case "BAMBOO_WOOD_HANGING_SIGN":
                return BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN;
            case "BAMBOO_WOOD":
                return BlockSoundGroup.BAMBOO_WOOD;
            case "NETHER_WOOD":
                return BlockSoundGroup.NETHER_WOOD;
            case "CHERRY_WOOD":
                return BlockSoundGroup.CHERRY_WOOD;
            case "CHERRY_SAPLING":
                return BlockSoundGroup.CHERRY_SAPLING;
            case "CHERRY_LEAVES":
                return BlockSoundGroup.CHERRY_LEAVES;
            case "CHERRY_WOOD_HANGING_SIGN":
                return BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN;
            case "CHISELED_BOOKSHELF":
                return BlockSoundGroup.CHISELED_BOOKSHELF;
            case "SUSPICIOUS_SAND":
                return BlockSoundGroup.SUSPICIOUS_SAND;
            case "SUSPICIOUS_GRAVEL":
                return BlockSoundGroup.SUSPICIOUS_GRAVEL;
            case "DECORATED_POT":
                return BlockSoundGroup.DECORATED_POT;
            case "DECORATED_POT_SHATTER":
                return BlockSoundGroup.DECORATED_POT_SHATTER;
            default:
                log(Level.WARN, "Sound group " + s + " not found, using stone");
                return BlockSoundGroup.STONE;
        }
    }


}
