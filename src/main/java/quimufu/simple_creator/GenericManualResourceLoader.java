package quimufu.simple_creator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static quimufu.simple_creator.SimpleCreatorMod.log;

public abstract class GenericManualResourceLoader<T> {
    private Gson GSON;
    private String dataType;
    private SimpleCreatorConfig config;

    GenericManualResourceLoader(Gson gson, String dt) {
        GSON = gson;
        dataType = dt;
    }

    protected void loadItems(ArrayList<Pair<Identifier, JsonObject>> itemJsonList) {
        log(Level.INFO, "Start loading " + dataType);
        for (Pair<Identifier, JsonObject> e : itemJsonList) {
            Identifier id = e.getLeft();
            log(Level.INFO, "Loading " + dataType.substring(0, dataType.length() - 1) + " " + id);
            T thing = deserialize(e);
            save(id, thing);
            log(Level.INFO, "Registering " + dataType.substring(0, dataType.length() - 1) + " " + id);
            register(id, thing);
        }
        log(Level.INFO, "Finished loading " + dataType);
    }

    protected abstract void register(Identifier id, T thing);

    protected abstract T deserialize(Pair<Identifier, JsonObject> e);

    protected abstract void save(Identifier id, T item);

    public void load() {
        config = AutoConfig.getConfigHolder(SimpleCreatorConfig.class).getConfig();
        ResourcePackManager<ResourcePackProfile> resourcePackManager = new ResourcePackManager<>(ResourcePackProfile::new);
        resourcePackManager.registerProvider(new VanillaDataPackProvider());
        resourcePackManager.registerProvider(new FileResourcePackProvider(new File("./datapacks")));
        if (config.enableTestThings)
            resourcePackManager.registerProvider(new ModResourcePackCreator(ResourceType.SERVER_DATA));
        resourcePackManager.scanPacks();
        List<ResourcePackProfile> ep = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        for (ResourcePackProfile rpp : resourcePackManager.getProfiles()) {
            if (!ep.contains(rpp)) {
                rpp.getInitialPosition().insert(ep, rpp, resourcePackProfile -> resourcePackProfile, false);
            }
        }
        resourcePackManager.setEnabledProfiles(ep);


        ArrayList<Pair<Identifier, JsonObject>> itemJsonList = new ArrayList<>();
        HashMap<Identifier, JsonObject> itemJsonMap = Maps.newHashMap();
        for (ResourcePackProfile rpp : resourcePackManager.getEnabledProfiles()) {
            ResourcePack rp = rpp.createResourcePack();
            log(Level.INFO, "Loading ResourcePack " + rp.getName());
            for (String ns : rp.getNamespaces(ResourceType.SERVER_DATA)) {
                log(Level.INFO, "Loading namespace " + ns);
                Collection<Identifier> resources = rp.findResources(ResourceType.SERVER_DATA, ns, dataType, 5, s -> s.endsWith(".json"));
                for (Identifier id : resources) {
                    if (config.extendedLogging)
                        log(Level.INFO, "found: " + id.toString() + " in Pack: " + rp.getName());
                    Identifier idNice = new Identifier(id.getNamespace(), getName(id));
                    try {
                        InputStream is = rp.open(ResourceType.SERVER_DATA, id);
                        Reader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        JsonObject jo = JsonHelper.deserialize(GSON, r, JsonObject.class);
                        if (jo != null)
                            if (jo.entrySet().isEmpty()) {
                                itemJsonMap.remove(idNice);
                                if (config.extendedLogging)
                                    log(Level.INFO, "deleting " + idNice + " because of an empty override in " + rp.getName());
                            } else {
                                itemJsonMap.put(idNice, jo);
                                if (config.extendedLogging)
                                    log(Level.INFO, "adding " + idNice + " from " + rp.getName());
                            }
                    } catch (IOException e) {
                        log(Level.ERROR, "error loading " + id + " " + e.getMessage());
                    } catch (JsonParseException e) {
                        log(Level.ERROR, "error parsing json for " + id + " " + e.getMessage());
                    }
                }
            }
        }
        for (Map.Entry<Identifier, JsonObject> e : itemJsonMap.entrySet()) {
            itemJsonList.add(new Pair<>(e.getKey(), e.getValue()));
        }
        loadItems(itemJsonList);
    }

    private String getName(Identifier id) {
        String path = id.getPath();
        int startLength = dataType.length() + 1;
        int endLength = ".json".length();
        return path.substring(startLength, path.length() - endLength);
    }
}
