package configurator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import configurator.api.*;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigWriter {
    /** the top-level config folder */
    public static final File CONFIG_DIR = new File("config");
    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    /**
     * Gets the folder path for this config to put into.
     * @param config the config
     * @return a string of the config's full folder path
     */
    public static String getFolder(Config config) {
        return config.type.getFolder(CONFIG_DIR.getPath() + (!config.folder.isEmpty() ? "/" + config.folder : ""));
    }

    /**
     * Gets the config file for the given config.
     * @param config the config to get a file of
     * @return the config's {@link File}
     */
    public static File getConfigFile(Config config) {
        Path path = Paths.get(getFolder(config) + "/" + config.name + ".json");
        return path.toFile();
    }

    /**
     * Directly writes a config file using a pre-built {@link JsonObject}
     * @param json the json object to write
     * @param fileName the name of the config file
     * @param folder the name of the folder to create the config file in
     * @param type the config's type category
     * @return if the write operation was successful
     */
    public static boolean writeConfigJson(JsonObject json, String fileName, String folder, Config.Type type) {
        String folderName = !folder.isEmpty() ? "/" + folder : "";
        if (!initFolder(new File(type.getFolder(CONFIG_DIR.getPath() + folderName)))) throw new NullPointerException();

        Path path = Paths.get(CONFIG_DIR.getPath() + folderName + "/" + fileName + ".json");

        if (path.toFile().exists()) throw new IllegalStateException(String.format("Config file %s already exists!", fileName));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(GSON.toJson(json));
            return true;
        } catch (IOException e) {
            Configurator.LOGGER.error(e);
            return false;
        }
    }

    /**
     * Writes a config to a .json file.
     * @param config the config to write
     */
    public static void writeConfig(Config config) {
        JsonObject json = serialize(config);
        String name = config.name + ".json";
        String folderName = getFolder(config);
        writeConfigJson(json, name, new File(folderName));
    }

    /**
     * Reads the given config's .json file and puts the read values into the config values.
     * @param config the config to read
     */
    public static void readConfig(Config config) {
        JsonObject json = readConfigJson(config.name + ".json", new File(getFolder(config)));
        if (json == null) throw new NullPointerException("Could not read config json!");

        for (ConfigCategory category : config.categories) readCategory(json, category);
    }

    /**
     * Converts a config into a {@link JsonObject}.
     * @param config the config to convert
     * @return the json version of the given config
     */
    public static JsonObject serialize(Config config) {
        JsonObject json = new JsonObject();

        for (ConfigCategory category : config.categories) writeCategory(json, category);

        return json;
    }

    private static void writeCategory(JsonObject parent, ConfigCategory category) {
        JsonObject categoryJson = new JsonObject();

        for (ConfigValue<?> value : category.getValues()) value.writeToJson(categoryJson);
        for (ConfigCategory subCategory : category.getSubCategories()) writeCategory(categoryJson, subCategory);

        parent.add(category.getName(), categoryJson);
    }

    private static boolean initFolder(File dir) {
        if (!dir.exists() && !dir.mkdir()) {
            Configurator.LOGGER.error("Could not make folder at {}", dir.getAbsolutePath());
            return false;
        }
        return true;
    }

    private static boolean validateCategory(JsonObject json, ConfigCategory category) {
        JsonObject categoryJson = json.getAsJsonObject(category.getName());
        if (categoryJson == null) return false;

        for (ConfigValue<?> value : category.getValues())
            if (!categoryJson.has(value.getName())) return false;

        for (ConfigCategory subCategory : category.getSubCategories())
            if (!validateCategory(categoryJson, subCategory)) return false;

        return true;
    }

    private static void writeConfigJson(JsonObject json, String name, File folder) {
        if (!initFolder(folder)) throw new NullPointerException("Could not write config!");

        String fileName = folder.getPath() + "/" + name;
        Path path = Paths.get(fileName);

        if (path.toFile().exists()) throw new IllegalStateException(String.format("Config %s already exists!", name));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(GSON.toJson(json));
        } catch (IOException e) {
            Configurator.LOGGER.error(e);
        }
    }

    @Nullable
    private static JsonObject readConfigJson(String name, File folder) {
        if (!folder.exists()) throw new NullPointerException("Attempt to read config from non-existent folder!");

        String fileName = folder.getPath() + "/" + name;
        Path path = Paths.get(fileName);

        if (!path.toFile().exists()) throw new NullPointerException("Attempt to read json from non-existent config file!");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            Configurator.LOGGER.error(e);
        }
        return null;
    }

    private static void readCategory(JsonObject json, ConfigCategory category, boolean update) {
        JsonObject categoryJson = json.getAsJsonObject(category.getName());
        if (categoryJson == null) {
            if (update) return;
            else throw new NullPointerException();
        }

        for (ConfigValue<?> value : category.getValues()) {
            if (categoryJson.has(value.getName())) {
                if (value instanceof BooleanConfigValue) ((BooleanConfigValue)value).set(categoryJson.get(value.getName()).getAsBoolean());
                else if (value instanceof StringConfigValue) ((StringConfigValue)value).set(categoryJson.get(value.getName()).getAsString());
                else if (value instanceof IntegerConfigValue) ((IntegerConfigValue)value).set(categoryJson.get(value.getName()).getAsInt());
                else if (value instanceof FloatConfigValue) ((FloatConfigValue)value).set(categoryJson.get(value.getName()).getAsFloat());
                else if (value instanceof DoubleConfigValue) ((DoubleConfigValue)value).set(categoryJson.get(value.getName()).getAsDouble());
                else if (value instanceof ByteConfigValue) ((ByteConfigValue)value).set(categoryJson.get(value.getName()).getAsByte());
                else if (value instanceof ShortConfigValue) ((ShortConfigValue)value).set(categoryJson.get(value.getName()).getAsShort());
                else if (value instanceof LongConfigValue) ((LongConfigValue)value).set(categoryJson.get(value.getName()).getAsLong());
                else if (value instanceof JsonConfigValue) ((JsonConfigValue)value).set(categoryJson.getAsJsonObject(value.getName()));
                else if (value instanceof ArrayConfigValue) ((ArrayConfigValue)value).set(categoryJson.getAsJsonArray(value.getName()));
            }
        }

        for (ConfigCategory subCategory : category.getSubCategories()) readCategory(categoryJson, subCategory, update);
    }

    private static void readCategory(JsonObject json, ConfigCategory category) {
        readCategory(json, category, false);
    }

    protected static boolean jsonMatchesConfig(File jsonFile, Config config) {
        if (jsonFile.exists()) {
            JsonObject json;

            try (BufferedReader reader = Files.newBufferedReader(jsonFile.toPath())) {
                json = GSON.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                Configurator.LOGGER.error(e);
                return false;
            }

            for (ConfigCategory category : config.categories)
                if (!validateCategory(json, category)) return false;

            return true;
        }
        return false;
    }

    protected static void updateConfig(File jsonFile, Config config, boolean alreadyChecked) {
        if (jsonFile.exists() && (alreadyChecked || !jsonMatchesConfig(jsonFile, config))) {
            JsonObject json;

            try (BufferedReader reader = Files.newBufferedReader(jsonFile.toPath())) {
                json = GSON.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                Configurator.LOGGER.error(e);
                return;
            }

            if (jsonFile.delete()) {
                for (ConfigCategory category : config.categories) readCategory(json, category, true);
                writeConfig(config);
            } else Configurator.LOGGER.error("Could not overwrite config!");
        }
    }
}
