package configurator;

import configurator.api.Config;
import configurator.api.ConfigValue;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(modid = Configurator.MOD_ID)
public class Configurator {
    public static final String MOD_ID = "configurator";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ConfigValue<Boolean> CONTAINED;

    protected static final List<Config> CONFIGS = new ArrayList<>();

    private static final Config MAIN_CONFIG;

    static {
        Config.Builder builder = Config.Builder.builder("").ofType(Config.Type.UNCATEGORIZED).withName("Configurator");

        builder.push("general");
        CONTAINED = builder.define("contain_in_one_folder", false);
        builder.pop();

        MAIN_CONFIG = builder.build();
        CONFIGS.add(0, MAIN_CONFIG);
    }

    private static void forceLoadConfig() {
        if (!MAIN_CONFIG.isLoaded()) {
            loadConfig(MAIN_CONFIG, false, true);
        }
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        boolean isClient = FMLCommonHandler.instance().getSide().isClient();
        long time = System.currentTimeMillis();
        for (Config config : CONFIGS) {
            Config.Type category = config.type;
            if ((category == Config.Type.COMMON || category == Config.Type.UNCATEGORIZED) || (category == Config.Type.CLIENT && isClient) || (category == Config.Type.SERVER && !isClient))
                loadConfig(config, false, false);
        }
        LOGGER.info("Loaded {} configs in {} ms", CONFIGS.size(), System.currentTimeMillis() - time);
    }

    protected static void loadConfig(Config config, boolean log, boolean remove) {
        if (!config.isLoaded()) {
            long time = System.currentTimeMillis();
            if (remove) CONFIGS.remove(config);
            if (config != MAIN_CONFIG) forceLoadConfig();

            File file = ConfigWriter.getConfigFile(config);
            if (!file.exists()) ConfigWriter.writeConfig(config);
            else if (!ConfigWriter.jsonMatchesConfig(file, config)) ConfigWriter.updateConfig(file, config, true);
            ConfigWriter.readConfig(config);
            config.loaded();

            if (log) LOGGER.info("Loaded config {} in {} ms", config.name, System.currentTimeMillis() - time);
        }
    }

    /**
     * Loads the given config if it has not already been loaded
     * @param config The config to load
     */
    public static void loadConfig(Config config) {
        loadConfig(config, true, true);
    }

    /**
     * registers a config to be loaded on mod construction
     * @param config the config to register
     * @return the config that was registered
     */
    public static Config registerConfig(Config config) {
        CONFIGS.add(config);
        return config;
    }

    @Deprecated
    public static Config registerConfig(Config.Builder builder) {
        return registerConfig(builder.build());
    }

    @Deprecated
    public static Config registerConfig(Supplier<Config> configSupplier) {
        return registerConfig(configSupplier.get());
    }
}
