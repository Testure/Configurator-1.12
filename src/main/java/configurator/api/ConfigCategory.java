package configurator.api;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A category that can hold {@link ConfigValue}s and other categories
 */
public class ConfigCategory {
    protected final List<ConfigCategory> subCategories = new ArrayList<>();
    protected final String name;
    protected final ConfigCategory parent;
    protected final List<ConfigValue<?>> values = new ArrayList<>();

    public ConfigCategory(String name, @Nullable ConfigCategory parent) {
        this.name = name;
        this.parent = parent;
    }

    /**
     * <p>
     * Adds a {@link ConfigValue} into this category.
     * </p>
     * This method is for internal use.
     * @param value the ConfigValue to add
     */
    public void addValue(ConfigValue<?> value) {
        values.add(value);
    }

    /**
     * Gets the name of this category
     * @return the name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the category that this category exists in.
     * @return the parent category of this category, null if this category is at the top-level
     */
    @Nullable
    public ConfigCategory getParentCategory() {
        return parent;
    }

    /**
     * Gets the {@link ConfigValue}s that are stored in this category.
     * @return the ConfigValues in this category
     */
    public List<ConfigValue<?>> getValues() {
        return values;
    }

    /**
     * Gets the sub categories stored in this category.
     * @return the sub categories in this category
     */
    public List<ConfigCategory> getSubCategories() {
        return subCategories;
    }
}
