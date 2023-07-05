package quimufu.simple_creator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import static quimufu.simple_creator.SimpleCreatorMod.MOD_ID;
import static quimufu.simple_creator.SimpleCreatorMod.log;

public abstract class GenericManualResourceLoader<T> {
    private final Gson gson;
    private final String dataType;

    GenericManualResourceLoader(Gson gson, String dt) {
        this.gson = gson;
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

        if (SimpleCreatorConfig.enableTestThings) {
            createFromResource("simple_creator/blocks/test_block.json");
            createFromResource("simple_creator/items/test_item.json");
        }
        File location = new File("./simplyCreated");

        if (!location.exists() && !location.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + location);
        }

        if (!location.isDirectory()) {
            throw new IllegalStateException("Not a dir: " + location);
        }

        File[] modIds = location.listFiles();
        ArrayList<Pair<Identifier, JsonObject>> itemJsonList = new ArrayList<>();
        if(modIds == null){
            log(Level.INFO, "No files  found at " + location + " quitting!");
            return;
        }
        for (File mod : modIds) {
            if (!mod.isDirectory()) {
                continue;
            }
            String modId = mod.getName();

            File entryDir = new File(mod + "/" + dataType);
            if (entryDir.isDirectory()) {
                File[] entries = entryDir.listFiles();
                if(entries == null){
                    log(Level.INFO, "No files  found at " + entryDir + " skipping!");
                    continue;
                }

                for (File entryJson : entries) {
                    String blockJsonName = entryJson.getName();
                    if(!blockJsonName.endsWith(".json")){
                        log(Level.INFO, "Non json found at " + entryJson + " ignoring!");
                        continue;
                    }
                    String entryName = blockJsonName.substring(0,blockJsonName.length()-5);
                    Identifier identifier = new Identifier(modId, entryName);

                    try (Reader reader = new FileReader(entryJson)) {
                        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                        itemJsonList.add(new Pair<>(identifier, jsonObject));
                    } catch (IOException e) {
                        log(Level.INFO, "Could not parse " + entryJson + " ignoring!");
                        e.printStackTrace();
                        log(Level.INFO, e.getMessage());
                    }
                }
            }
        }

        loadItems(itemJsonList);

    }

    private static void createFromResource(String path) {
        Optional<ModContainer> modContainerOp = FabricLoader.getInstance().getModContainer(MOD_ID);
        if(modContainerOp.isEmpty()){
            log(Level.ERROR,"ModContainer " + MOD_ID + " not Found" );
            return;
        }
        Optional<Path> nioPath = modContainerOp
                .flatMap(modContainer -> modContainer.findPath("data/" + path));
        if(nioPath.isEmpty()){
            log(Level.ERROR,"data/" + path + " Not Found" );
            return;
        }
        try (InputStream blocks = Files.newInputStream(nioPath.get())) {

            File file = new File("./simplyCreated/" + path);
            if (!file.exists()) {

                File parent = file.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException("Couldn't create dir: " + parent);
                }
                if (!file.createNewFile()) {
                    throw new IllegalStateException("Couldn't create file: " + file);
                }

                try (FileOutputStream out = new FileOutputStream(file)) {
                    //copy stream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = blocks.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
