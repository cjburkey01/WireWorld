package com.cjburkey.jautomata.world;

import com.cjburkey.jautomata.util.Helpers;

/**
 * Created by CJ Burkey on 2018/11/26
 */
@SuppressWarnings("unused")
public class AutomataHandler {
    
    private final AutomataWorld world;
    private final Thread thread;
    
    private double deltaTime;
    
    private boolean stop;
    private long ticks = 0L;
    private int tickQueue = 0;
    private boolean reset;
    private boolean runLoop;
    private double maxDeltaTime = 0.0d;
    
    public AutomataHandler(AutomataWorld world) {
        this.world = world;
        thread = new Thread(this::loop);
    }
    
    private void loop() {
        while (!stop) {
            while (runLoop) doTick();
            while (tickQueue > 0) doTick();
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                System.err.println("Failed to sleep:");
                e.printStackTrace(System.err);
            }
        }
    }
    
    private void doTick() {
        if (reset) {
            reset = false;
            world.reset();
        }
        
        long start = System.nanoTime();
        if (tickQueue > 0) tickQueue --;
        world.tick();
        ticks ++;
        deltaTime = calcDelta(System.nanoTime(), start);
        
        // Throttle if necessary
        while (deltaTime < maxDeltaTime) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                System.err.println("Failed to sleep:");
                e.printStackTrace(System.err);
            }
            deltaTime = calcDelta(System.nanoTime(), start);
        }
    }
    
    private double calcDelta(long end, long start) {
        return (end - start) / 1000000000.0d;
    }
    
    public void init() {
        thread.start();
    }
    
    public void exit() {
        runLoop = false;
        tickQueue = 0;
        stop = true;
    }
    
    public void run() {
        runLoop = true;
    }
    
    public void stop() {
        runLoop = false;
    }
    
    public void tick() {
        tickQueue ++;
    }
    
    public void reset() {
        reset = true;
    }
    
    public double getDeltaTime() {
        return deltaTime;
    }
    
    public void setMaxDeltaTime(double maxDeltaTime) {
        this.maxDeltaTime = Helpers.max(0.0d, maxDeltaTime);
    }
    
    public double getMaxDeltaTime() {
        return maxDeltaTime;
    }
    
    public long getTicks() {
        return ticks;
    }
    
}
