package configurator;

import configurator.api.Config;
import configurator.api.ConfigValue;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
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

    static {
        Config.Builder builder = Config.Builder.builder("").ofType(Config.Type.UNCATEGORIZED).withName("Configurator");

        builder.push("general");
        CONTAINED = builder.define("contain_in_one_folder", false);
        builder.pop();

        registerConfig(builder);
    }

    public Configurator() {
        long time = System.currentTimeMillis();
        for (Config config : CONFIGS) {
            File file = ConfigWriter.getConfigFile(config);
            if (!file.exists()) ConfigWriter.writeConfig(config);
            else if (!ConfigWriter.jsonMatchesConfig(file, config)) {
                ConfigWriter.updateConfig(file, config, true);
            }
            ConfigWriter.readConfig(config);
        }
        LOGGER.info("Loaded {} configs in {} ms", CONFIGS.size(), System.currentTimeMillis() - time);
        CONFIGS.removeIf(config -> {
            config.categories.clear();
            return true;
        });
    }

    /**
     * registers a built config
     * @param config the config to register
     * @return the config that was registered
     */
    public static Config registerConfig(Config config) {
        CONFIGS.add(config);
        return config;
    }

    /**
     * registers a config builder
     * this is just a shortcut for register(builder::build);
     * @param config the config builder to register
     * @return the built config that was registered
     */
    public static Config registerConfig(Config.Builder config) {
        return registerConfig(config.build());
    }

    /**
     * registers a supplied config
     * common use is register(builder::build);
     * @param config the config to register
     * @return the config that was registered
     */
    public static Config registerConfig(Supplier<Config> config) {
        return registerConfig(config.get());
    }
}
