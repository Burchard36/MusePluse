package com.burchard36.cloudlite.utils;

import com.burchard36.cloudlite.CloudLiteCore;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskRunner {

    /**
     * Quick utility for creaking timers
     * @param runnable {@link Runnable} that is ran
     * @param ticks delay time between each timer in minecraft ticks
     * @return {@link BukkitTask} can be cancelled
     */
    public static BukkitTask runSyncTaskTimer(final Runnable runnable, final long ticks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(CloudLiteCore.INSTANCE, 0, ticks);
    }

    /**
     * Used when you want to throw a method call back onto the main thread
     * @param runnable {@link Runnable} that is ran
     */
    public static void runSyncTask(final Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(CloudLiteCore.INSTANCE);
    }

    /**
     * Used when you want a task to run later
     * @param runnable {@link Runnable} that is ran
     * @param ticks ticks to run later,
     * @return bukkit task to cancel later if needed
     */
    public static BukkitTask runSyncTaskLater(final Runnable runnable, final long ticks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(CloudLiteCore.INSTANCE, ticks);
    }


}
