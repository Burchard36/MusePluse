package com.burchard36.musepluse;

import org.bstats.bukkit.Metrics;

public class BStatsImpl {
    protected final int id = 18818;

    public BStatsImpl(final MusePluse pluginInstance) {
        final Metrics metrics = new Metrics(pluginInstance, id);
    }

}
