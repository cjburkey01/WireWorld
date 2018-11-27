package com.cjburkey.jautomata.edit;

import com.cjburkey.jautomata.IAutomataHandler;
import com.cjburkey.jautomata.util.Helpers;
import com.cjburkey.jautomata.util.Render;
import com.cjburkey.jautomata.world.AutomataWorld;
import javafx.scene.input.MouseButton;
import org.joml.Vector2d;
import org.joml.Vector2i;

/**
 * Created by CJ Burkey on 2018/11/27
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class EditorHandler {
    
    private static final byte modes = 2;
    
    private AutomataWorld world;
    private IAutomataHandler automataHandler;
    private byte current;
    private Vector2i currentPos = new Vector2i();
    private byte mode = 0;
    
    private Vector2i startPos = new Vector2i();
    
    public EditorHandler(AutomataWorld world, IAutomataHandler automataHandler) {
        this.world = world;
        this.automataHandler = automataHandler;
    }
    
    public void onNumPressed(int val) {
        if (val == 0) setCurrent((byte) 0x00);
        else if (automataHandler.getTileCount() >= val) setCurrent(automataHandler.getTiles()[val - 1]);
    }
    
    public void onUpdate(Vector2d mousePos) {
        Vector2d rawMouse = Render.transformPoint(mousePos).floor();
        currentPos.set((int) rawMouse.x, (int) rawMouse.y);
    }
    
    public void onMouseJustDown(MouseButton button) {
        startPos = new Vector2i(currentPos);
    }
    
    public void onMouseJustUp(MouseButton button) {
        if (mode == 1) {
            byte at = getType(button);
            if (at < 0) return;
            for (int x = Helpers.min(startPos.x, currentPos.x); x <= Helpers.max(startPos.x, currentPos.x); x++) {
                for (int y = Helpers.min(startPos.y, currentPos.y); y <= Helpers.max(startPos.y, currentPos.y); y++) {
                    world.setTile(new Vector2i(x, y), at, at == 0x00);
                }
            }
        }
    }
    
    public void onMousePressed(MouseButton button) {
        if (mode == 0) {
            byte at = getType(button);
            if (at < 0) return;
            world.setTile(currentPos, at, at == 0x00);
        }
    }
    
    private byte getType(MouseButton button) {
        if (button.equals(MouseButton.PRIMARY)) return current;
        if (button.equals(MouseButton.SECONDARY)) return 0x00;
        return -0x01;
    }
    
    public void setCurrent(byte current) {
        this.current = current;
    }
    
    public byte getCurrent() {
        return current;
    }
    
    public void setMode(byte mode) {
        this.mode = Helpers.mod(mode, modes);
    }
    
    public byte getMode() {
        return mode;
    }
    
}
