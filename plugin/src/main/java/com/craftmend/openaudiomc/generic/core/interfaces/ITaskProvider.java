package com.craftmend.openaudiomc.generic.core.interfaces;

public interface ITaskProvider {

    int scheduleAsyncRepeatingTask(Runnable runnable, int period, int delay);
    int scheduleSyncRepeatingTask(Runnable runnable, int period, int delay);
    int schduleSyncDelayedTask(Runnable runnable, int delay);
    int schduleAsyncRepeatingTask(Runnable runnable, int period, int delay);
    void cancelRepeatingTask(int id);
    void runAsync(Runnable runnable);

}
