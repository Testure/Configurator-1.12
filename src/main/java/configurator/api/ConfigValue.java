package configurator.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;

public class ConfigValue<T> {
    protected final String name;
    protected final ConfigCategory parent;
    protected T value;

    public ConfigValue(String name, ConfigCategory parent) {
        this.name = name;
        this.parent = parent;
    }

    public void writeToJson(JsonObject json) {
        if (value instanceof Number) json.addProperty(name, (Number)value);
        else if (value instanceof Boolean) json.addProperty(name, (Boolean)value);
        else if (value instanceof JsonElement) json.add(name, (JsonElement)value);
        else json.addProperty(name, value.toString());
    }

    /**
     * <p>
     * Sets the value in this ConfigValue to the given value.
     * </p>
     * This method for internal use.
     * @param value the value to put into this ConfigValue
     */
    public void set(@Nullable T value) {
        this.value = value;
    }

    /**
     * Gets the {@link ConfigCategory} that this ConfigValue exists under.
     * @return the category this ConfigValue exists in
     */
    public ConfigCategory getParentCategory() {
        return parent;
    }

    /**
     * Gets the name of this ConfigValue.
     * @return the name of this config
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the stored value from this ConfigValue.
     * @return the stored value
     */
    @Nullable
    public T get() {
        return value;
    }
}
