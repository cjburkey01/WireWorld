package com.cjburkey.wireworld;

import com.cjburkey.jautomata.AutomataProvider;
import com.cjburkey.jautomata.IAutomataHandler;
import com.cjburkey.jautomata.Input;
import java.util.Random;
import javafx.scene.input.KeyCode;
import org.joml.Vector2i;

import static com.cjburkey.jautomata.util.Helpers.*;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public class AutomataWireWorld extends AutomataProvider {
    
    private final AutomataWireWorldHandler handler = new AutomataWireWorldHandler();
    
    public IAutomataHandler getHandler() {
        return handler;
    }
    
    protected void init() {
        getAutomata().automataWorld.setTile(new Vector2i(5, 1), (byte) 0x03, true);
        getAutomata().automataWorld.setTile(new Vector2i(-5, 1), (byte) 0x02, true);
        for (int i = -4; i < 5; i ++) {
            getAutomata().automataWorld.setTile(new Vector2i(i, 0), (byte) 0x03, true);
            getAutomata().automataWorld.setTile(new Vector2i(i, 2), (byte) 0x03, true);
        }
        getAutomata().automataWorld.setTile(new Vector2i(-4, 0), (byte) 0x01, true);
//        Random r = new Random();
        for (int y = -512; y < 512; y ++) {
            for (int x = -512; x < 512; x ++) {
//                getAutomata().automataWorld.setTile(new Vector2i(x, y), (byte) (rand(r, 0, 1) == 0 ? 0x03 : 0x00), true);
                getAutomata().automataWorld.setTile(new Vector2i(x, y), (byte) 0x03, true);
            }
        }
        getAutomata().automataWorld.setTile(new Vector2i(), (byte) 0x01, true);
    }
    
    public boolean handleExtraInput(Input input) {
        if (input.isKeyJustDown(KeyCode.ESCAPE)) exit();
        if (input.isKeyJustDown(KeyCode.T)) getAutomata().singleTick();
        if (input.isKeyJustDown(KeyCode.L)) getAutomata().startTickLoop();
        if (input.isKeyJustDown(KeyCode.E)) getAutomata().stopTickLoop();
        if (input.isKeyJustDown(KeyCode.R)) getAutomata().reset();
        return false;
    }
    
    public static void main(String[] args) {
        start(new AutomataWireWorld(), args);
    }
    
}
