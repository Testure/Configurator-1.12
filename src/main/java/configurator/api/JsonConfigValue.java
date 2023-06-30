package configurator.api;

import com.google.gson.JsonObject;

/**
 * <p>
 * config value that stores a json object.
 * </p>
 * same as ConfigValue of JsonObject
 */
public class JsonConfigValue extends ConfigValue<JsonObject> {
    public JsonConfigValue(String name, ConfigCategory parentCategory) {
        super(name, parentCategory);
    }
}
