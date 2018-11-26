package com.cjburkey.wireworld;

/**
 * Created by CJ Burkey on 2018/11/25
 */
public class Util {
    
    public static byte min(byte a, byte b) {
        return a > b ? b : a;
    }
    
    public static short min(short a, short b) {
        return a > b ? b : a;
    }
    
    public static int min(int a, int b) {
        return a > b ? b : a;
    }
    
    public static long min(long a, long b) {
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
    
    public static long max(long a, long b) {
        return a <= b ? b : a;
    }
    
}
