package configurator.api;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

/**
 * Allows you to serialize a config value as a full {@link JsonObject} rather than just a string.
 * Useful for representing arrays or maps.
 * @param <T> the serialized type
 */
public abstract class SerializedJsonConfigValue<T> extends JsonConfigValue {
    protected T cachedValue;

    public SerializedJsonConfigValue(String name, ConfigCategory parentCategory) {
        super(name, parentCategory);
    }

    @Nullable
    public T get(boolean cache) {
        if (cachedValue != null) return cachedValue;
        JsonObject json = get();
        if (json != null) {
            T value = translate(json);
            if (cache) cachedValue = value;
            return value;
        }
        return null;
    }

    @Nullable
    public T getAllowNull(boolean cache) {
        return get(cache);
    }

    @Nullable
    public T getAllowNull() {
        return getAllowNull(true);
    }

    public T getNotNull(boolean cache) {
        T value = get(cache);
        if (value == null) throw new NullPointerException("Attempt to getNotNull a null config value");
        return value;
    }

    public T getNotNull() {
        return getNotNull(true);
    }

    protected abstract T translate(JsonObject json);
}
