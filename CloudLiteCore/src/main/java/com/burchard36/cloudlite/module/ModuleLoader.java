package com.burchard36.cloudlite.module;

import com.burchard36.cloudlite.CloudLiteCore;

import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {
    protected final CloudLiteCore pluginInstance;
    protected final List<PluginModule> modules;

    public ModuleLoader(final CloudLiteCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.modules = new ArrayList<>();
    }

    public void registerModule(final PluginModule pluginModule) {
        if (modules.contains(pluginModule)) throw new IllegalStateException("PluginModule is already initialized!");
        this.modules.add(pluginModule);
    }

    public void onServerLoadUp(final CloudLiteCore coreInstance) {
        this.modules.forEach(m -> m.loadModule(coreInstance));
    }

    public void onServerEnable() {
        this.modules.forEach(PluginModule::enableModule);
    }

    public void onServerShutdown() {
        this.modules.forEach(PluginModule::disableModule);
    }

    public void reloadModules() {
        this.pluginInstance.getConfigManager().reloadAll();
        this.modules.forEach(PluginModule::reload);
    }

    public PluginModule getModule(Class<?> clazz) {
        for (final PluginModule module : this.modules) {
            if (module.getClass().equals(clazz)) return module;
        }
        return null;
    }
}
