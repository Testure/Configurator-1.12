package configurator.api;

import configurator.Configurator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * <p>
 * stores an item stack as a string.
 * </p>
 * use {@link ItemStackConfigValue#getReal()} to get the actual item stack.
 */
public class ItemStackConfigValue extends SerializedConfigValue<ItemStack> {
    public ItemStackConfigValue(String name, ConfigCategory parent) {
        super(name, parent);
    }

    @Override
    public ItemStack deserialize(@Nullable String serializedValue) {
        if (serializedValue != null) {
            try {
                return new ItemStack(JsonToNBT.getTagFromJson(serializedValue));
            } catch (NBTException e) {
                Configurator.LOGGER.error(e);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public String serialize(ItemStack value) {
        return value.writeToNBT(new NBTTagCompound()).toString();
    }
}
