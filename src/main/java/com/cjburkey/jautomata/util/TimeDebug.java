package com.cjburkey.jautomata.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Created by CJ Burkey on 2018/11/27
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TimeDebug {
    
    public static boolean enabled = true;
    private static final Object2ObjectOpenHashMap<String, ObjectArrayList<Pair<Long, Long>>> timings = new Object2ObjectOpenHashMap<>();
    
    public static void start(String name) {
        if (!enabled) {
            return;
        }
        ObjectArrayList<Pair<Long, Long>> timing;
        long now = System.nanoTime();
        if (timings.containsKey(name)) {
            timing = timings.get(name);
            finalizeLast(timing, now);
        } else {
            timing = new ObjectArrayList<>();
            timings.put(name, timing);
        }
        timing.add(new Pair<>(now, null));
    }
    
    public static void pause(String name) {
        if (!enabled) {
            return;
        }
        if (timings.containsKey(name)) {
            finalizeLast(timings.get(name), System.nanoTime());
        }
    }
    
    private static void finalizeLast(ObjectArrayList<Pair<Long, Long>> timing, long time) {
        if (!enabled) {
            return;
        }
        Pair<Long, Long> last = timing.get(timing.size() - 1);
        if (last.k == null) {
            last.k = time;
        }
    }
    
    public static float finish(String name) {
        if (!enabled) {
            return -1.0f;
        }
        if (timings.containsKey(name)) {
            long time = 0L;
            for (Pair<Long, Long> timing : timings.get(name)) {
                time += ((timing.k == null) ? System.nanoTime() : timing.k) - ((timing.t == null) ? System.nanoTime() : timing.t);
            }
            timings.remove(name);
            return time / 1000000000.0f;
        }
        return -1.0f;
    }
    
    public static String finishAndString(String name) {
        if (!enabled) {
            return "Not enabled";
        }
        return genString(name, finish(name));
    }
    
    private static String genString(String name, float time) {
        if (!enabled) {
            return "Not enabled";
        }
        return (time >= 0.0f) ? String.format("\"%s\" took %.4f seconds", name, time) : String.format("Failed to locate \"%s\"", name);
    }
    
    public static void finishAndPrintAll(float min) {
        if (!enabled) {
            return;
        }
        for (String timing : timings.keySet()) {
            float time = finish(timing);
            if (time >= min) {
                System.out.println(genString(timing, time));
            }
        }
    }
    
    public static void finishAndPrintAll() {
        finishAndPrintAll(0.0f);
    }
    
}
