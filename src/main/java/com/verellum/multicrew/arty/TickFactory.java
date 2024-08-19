package com.verellum.multicrew.arty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TickFactory {

    private static List<Tick> ticks = new ArrayList<Tick>();
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private TickFactory() {}

    public static Tick createTick(long tickTime) {
        Tick tick = new Tick();
        tick.setRunning(true);
        ticks.add(tick);
        executor.scheduleAtFixedRate(tick, 0, tickTime, TimeUnit.MILLISECONDS);
        return tick;
    }

    public static List<Tick> getTicks() {
        return ticks;
    }

    public static int getTickNum() {
        System.out.println(ticks.size());
        return ticks.size();
    }

    public static void stop(Tick tick) {
        tick.stop();
    }

    public static void stopAll() {
        //fun double colon stuff
        ticks.stream().forEach(Tick::stop);
    }

    /**
     * closes executor
     */
    public static void blowUpTheFactory() {
        ticks.stream().forEach(Tick::stop);
        executor.close();
        ticks.clear();
    }

    public static void reviveTicks() {
        for (Tick t : ticks) {
            if (!t.getRunning())
                t.setRunning(true);
        }
    }

}
