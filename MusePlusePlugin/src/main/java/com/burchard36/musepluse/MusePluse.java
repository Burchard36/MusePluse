package com.burchard36.musepluse;

public final class MusePluse extends MusePlusePlugin {

    private static MusePluse INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
        // Modules need to get injected before the rest of the plugin
        this.getModuleLoader().registerModule(new MusePluseMusicPlayer());
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    /**
     * Basic getter method to get the singleton from {@link MusePluseMusicPlayer}
     * @return {@link MusePluseMusicPlayer}
     */
    public static MusePluseMusicPlayer getMusicPlayer() {
        return (MusePluseMusicPlayer) INSTANCE.getModuleLoader().getModule(MusePluseMusicPlayer.class);
    }

    /**
     * Basic getter method to get the instance of this class.
     * <br>
     * ANYONE MAKING A PR SHOULD NOT USE THIS METHOD!
     * @return instance of this class
     */
    public static MusePluse getMusePluse() {
        return INSTANCE;
    }
}
