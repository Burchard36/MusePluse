package com.burchard36.musepluse.module;

import com.burchard36.musepluse.MusePlusePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public interface PluginModule {

    /**
     * Simulates {@link JavaPlugin#onLoad()} for PluginModules
     * */
    void loadModule(final MusePlusePlugin coreInstance);

    /**
     * Simulated {@link JavaPlugin#onEnable()} for PluginModules
     */
    void enableModule();

    /**
     * Simulates {@link JavaPlugin#onDisable()} for PluginModules
     */
    void disableModule();

    void reload();

}
