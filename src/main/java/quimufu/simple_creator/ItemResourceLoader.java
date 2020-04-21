package quimufu.simple_creator;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.Map;

import static quimufu.simple_creator.SimpleCreatorMod.log;

public class ItemResourceLoader extends GenericManualResourceLoader<Item> {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final String dataType = "items";
    public static Map<Identifier, Item> items = Maps.newHashMap();

    ItemResourceLoader() {
        super(GSON, dataType);
    }

    @Override
    protected void register(Identifier id, Item thing) {
        Registry.register(Registry.ITEM, id, thing);
    }

    public Item deserialize(Pair<Identifier, JsonObject> e) {
        JsonObject jo = e.getRight();
        String group = JsonHelper.getString(jo, "group", "misc");
        ItemGroup g = findGroup(group);
        int durability = JsonHelper.getInt(jo, "durability", 0);
        byte stackSize = JsonHelper.getByte(jo, "stackSize", (byte) 1);
        boolean isFood = JsonHelper.hasElement(jo, "food");
        String rarity = JsonHelper.getString(jo, "rarity", "common");


        Item.Settings settings = new Item.Settings();
        settings.group(g);
        if (isFood) {
            if (durability != 0) {
                log(Level.WARN, "durability does not work with food");
                log(Level.WARN, "ignoring");
            }
            settings.maxCount(stackSize);
            JsonObject jsonFoodObject = JsonHelper.getObject(jo, "food");
            settings.food(deserializeFoodComponent(jsonFoodObject));
        } else if (durability != 0 && stackSize != 1) {
            log(Level.WARN, "durability and stackSize do not work together");
            log(Level.WARN, "ignoring stackSize");
            settings.maxDamage(durability);
        } else {
            if (durability != 0) {
                settings.maxDamage(durability);
            } else {
                settings.maxCount(stackSize);
            }
        }
        settings.rarity(findRarity(rarity));
        return new Item(settings);
    }

    @Override
    protected void save(Identifier id, Item item) {
        items.put(id, item);
    }

    private static FoodComponent deserializeFoodComponent(JsonObject jo) {
        FoodComponent fc;
        FoodComponent.Builder fcb = new FoodComponent.Builder();
        fcb.hunger(JsonHelper.getInt(jo, "hunger", 4));
        fcb.saturationModifier(JsonHelper.getFloat(jo, "saturationModifier", 0.3F));
        if (JsonHelper.getBoolean(jo, "isAlwaysEdible", false))
            fcb.alwaysEdible();
        if (JsonHelper.getBoolean(jo, "isWolfFood", false))
            fcb.meat();
        if (JsonHelper.getBoolean(jo, "isFast", false))
            fcb.snack();
        if (JsonHelper.hasArray(jo, "effects")) {
            JsonArray jsonEffectsArray = JsonHelper.getArray(jo, "effects");
            deserializeEffects(fcb, jsonEffectsArray);
        }
        fc = fcb.build();
        return fc;
    }

    private static void deserializeEffects(FoodComponent.Builder fcb, JsonArray ja) {
        for (JsonElement e : ja) {
            StatusEffect type;
            int duration = 0;
            int amplifier = 0;
            boolean ambient = false;
            boolean visible = true;
            float chance = 1.F;
            JsonObject jo = JsonHelper.asObject(e, "effects");
            String effect = JsonHelper.getString(jo, "effect");
            Identifier ei = Identifier.tryParse(effect);
            if (ei != null) {
                StatusEffect se = Registry.STATUS_EFFECT.get(ei);
                if (se != null) {
                    type = se;
                } else {
                    log(Level.WARN, "Effect " + ei + " not found, skipping");
                    continue;
                }
            } else {
                log(Level.WARN, "Effect id " + effect + " invalid, skipping");
                continue;
            }
            duration = JsonHelper.getInt(jo, "duration", duration);
            amplifier = JsonHelper.getInt(jo, "amplifier", amplifier);
            ambient = JsonHelper.getBoolean(jo, "ambient", ambient);
            visible = JsonHelper.getBoolean(jo, "visible", visible);
            chance = JsonHelper.getFloat(jo, "chance", chance);
            fcb.statusEffect(new StatusEffectInstance(type, duration, amplifier, ambient, visible), chance);
        }
    }

    private static Rarity findRarity(String filter) {
        for (Rarity r : Rarity.values()) {
            if (r.name().toLowerCase().equals(filter.toLowerCase()))
                return r;
        }
        log(Level.WARN, "Rarity " + filter + " not found, using common");
        log(Level.INFO, "Valid groups:" + Arrays.stream(Rarity.values()).map(Rarity::name).map(String::toLowerCase));
        return Rarity.COMMON;
    }

    public static ItemGroup findGroup(String filter) {
        for (ItemGroup g : ItemGroup.GROUPS) {
            if (g.getName().toLowerCase().equals(filter.toLowerCase())) {
                return g;
            }
        }
        log(Level.WARN, "Item Group " + filter + " not found, using misc");
        log(Level.INFO, "Valid groups:" + Arrays.stream(ItemGroup.GROUPS).map(ItemGroup::getId));
        return ItemGroup.MISC;
    }


}
