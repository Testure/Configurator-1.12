package configurator.api;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>
 * stores an Enum as a string.
 * </p><p>
 * you must provide EnumClass::valueOf.
 * </p>
 * use {@link EnumConfigValue#getReal()} to get the actual Enum.
 */
public class EnumConfigValue<T extends Enum<T>> extends SerializedConfigValue<T> {
    protected final Function<String, T> toReal;
    private final String defaultName;

    public EnumConfigValue(String name, Function<String, T> toReal, String defaultName, ConfigCategory parentCategory) {
        super(name, parentCategory);
        this.toReal = toReal;
        this.defaultName = defaultName;
    }

    @Override
    public T deserialize(@Nullable String serializedValue) {
        return serializedValue != null ? toReal.apply(serializedValue.toUpperCase()) : toReal.apply(defaultName);
    }

    @Override
    public String serialize(T value) {
        return value.name().toLowerCase();
    }
}
