package com.cjburkey.jautomata.util;

import java.util.Random;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings("unused")
public class Helpers {
    
    public static byte min(byte a, byte b) {
        return a > b ? b : a;
    }
    
    public static short min(short a, short b) {
        return a > b ? b : a;
    }
    
    public static int min(int a, int b) {
        return a > b ? b : a;
    }
    
    public static float min(float a, float b) {
        return a > b ? b : a;
    }
    
    public static long min(long a, long b) {
        return a > b ? b : a;
    }
    
    public static double min(double a, double b) {
        return a > b ? b : a;
    }
    
    public static byte max(byte a, byte b) {
        return a <= b ? b : a;
    }
    
    public static short max(short a, short b) {
        return a <= b ? b : a;
    }
    
    public static int max(int a, int b) {
        return a <= b ? b : a;
    }
    
    public static float max(float a, float b) {
        return a <= b ? b : a;
    }
    
    public static long max(long a, long b) {
        return a <= b ? b : a;
    }
    
    public static double max(double a, double b) {
        return a <= b ? b : a;
    }
    
    public static int div(int a, int b) {
        return Math.floorDiv(a, b);
    }
    
    public static byte mod(byte a, byte b) {
        return (byte) Math.floorMod(a, b);
    }
    
    public static short mod(short a, short b) {
        return (short) Math.floorMod(a, b);
    }
    
    public static int mod(int a, int b) {
        return Math.floorMod(a, b);
    }
    
    // Not necessary
    public static float mod(float a, float b) {
        return a % b;
    }
    
    public static long mod(long a, long b) {
        return Math.floorMod(a, b);
    }
    
    // Not necessary
    public static double mod(double a, double b) {
        return a % b;
    }
    
    public static byte rand(Random random, byte minInc, byte maxInc) {
        return (byte) (minInc + random.nextInt(maxInc - minInc + 1));
    }
    
    public static short rand(Random random, short minInc, short maxInc) {
        return (short) (minInc + random.nextInt(maxInc - minInc + 1));
    }
    
    public static int rand(Random random, int minInc, int maxInc) {
        return minInc + random.nextInt(maxInc - minInc + 1);
    }
    
}
