package com.cjburkey.jautomata;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public class Input {
    
    final ObjectOpenHashSet<KeyCode> keysDown = new ObjectOpenHashSet<>();
    final ObjectOpenHashSet<KeyCode> keysFresh = new ObjectOpenHashSet<>();
    final ObjectOpenHashSet<MouseButton> mouseDown = new ObjectOpenHashSet<>();
    final ObjectOpenHashSet<MouseButton> mouseFresh = new ObjectOpenHashSet<>();
    final Vector2d mousePos = new Vector2d();
    final Vector2d prevMouse = new Vector2d();
    final Vector2d mouseDelta = new Vector2d();
    double scroll = 0.0d;
    
    public boolean isKeyDown(KeyCode key) {
        return keysDown.contains(key);
    }
    
    public boolean isKeyJustDown(KeyCode key) {
        return keysFresh.contains(key);
    }
    
    public boolean isMouseDown(MouseButton key) {
        return mouseDown.contains(key);
    }
    
    public boolean isMouseJustDown(MouseButton key) {
        return mouseFresh.contains(key);
    }
    
    public Vector2dc getMousePos() {
        return mousePos;
    }
    
    public Vector2dc getPreviousMousePos() {
        return prevMouse;
    }
    
    public Vector2dc getMouseDelta() {
        return mouseDelta;
    }
    
    public double getScroll() {
        return scroll;
    }
    
    void reset() {
        keysFresh.clear();
        mouseFresh.clear();
        prevMouse.set(mousePos);
        mouseDelta.set(0.0d);
        scroll = 0.0d;
    }
    
}
