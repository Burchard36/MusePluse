package com.burchard36.cloudlite;

import com.burchard36.cloudlite.commands.ReloadServerCommand;
import com.burchard36.cloudlite.cropregrower.CropRegrower;

public final class ServerPlugin extends CloudLiteCore {
    @Override
    public void onLoad() {
        // Modules need to get injected before the rest of the plugin
        this.getModuleLoader().registerModule(new CloudLiteMusicPlayer());
        this.getModuleLoader().registerModule(new AutoCompressorModule());
        this.getModuleLoader().registerModule(new MMOItemsLevelUp());
        this.getModuleLoader().registerModule(new BlockDropsEditor());
        this.getModuleLoader().registerModule(new CropRegrower());
        super.onLoad();
    }

    @Override
    public void onEnable() {
        CloudLiteCore.registerCommand("reloadserver", new ReloadServerCommand(this));
        super.onEnable();
    }
}
