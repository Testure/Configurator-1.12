package configurator.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * <p>
 * stores a resource location as a modid:value string
 * </p>
 * use {@link ResourceLocationConfigValue#getReal()} to get the raw string
 */
public class ResourceLocationConfigValue extends SerializedConfigValue<ResourceLocation> {
    public ResourceLocationConfigValue(String name, ConfigCategory parentCategory) {
        super(name, parentCategory);
    }

    @Override
    public ResourceLocation deserialize(@Nullable String serializedValue) {
        return new ResourceLocation(serializedValue != null ? serializedValue : "");
    }

    @Override
    public String serialize(ResourceLocation value) {
        return value.toString();
    }
}
