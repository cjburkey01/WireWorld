package com.cjburkey.jautomata;

import com.cjburkey.jautomata.edit.EditorHandler;
import com.cjburkey.jautomata.util.Render;
import com.cjburkey.jautomata.util.TimeDebug;
import com.cjburkey.jautomata.world.AutomataHandler;
import com.cjburkey.jautomata.world.AutomataWorld;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"unused", "WeakerAccess", "MismatchedQueryAndUpdateOfCollection"})
public final class JAutomata extends Application {
    
    public static AutomataProvider providerTmp;
    
    private final AutomataProvider provider;
    public final AutomataWorld automataWorld;
    private final AutomataHandler automataHandler;
    public final EditorHandler editor;
    
    public double zoomSlow = 100.0d;
    public double moveSpeed = 750.0d;
    
    private Stage stage;
    private Canvas canvas;
    private AnimationTimer loop = new AnimationTimer() {
        long lastRender = System.nanoTime();
        public void handle(long now) {
            render((now - lastRender) / 1000000000.0d);
            lastRender = now;
        }
    };
    private final Input input = new Input();
    
    public static void boot(AutomataProvider provider, String[] args) {
        if (provider == null) throw new IllegalStateException("Provided AutomataProvider is null");
        JAutomata.providerTmp = provider;
        launch(JAutomata.class, args);
    }
    
    public JAutomata() {
        provider = providerTmp;
        providerTmp = null;
        
        automataWorld = new AutomataWorld(provider.getHandler());
        automataHandler = new AutomataHandler(automataWorld);
        automataHandler.init();
        editor = new EditorHandler(automataWorld, provider.getHandler());
        
        provider.setAutomata(this);
        provider.init();
    }
    
    public void start(Stage stage) {
        this.stage = stage;
        initWindow();
    }
    
    public void exit() {
        System.out.println("Closing");
        automataHandler.exit();
        loop.stop();
        stage.hide();
        Platform.exit();
    }
    
    private void initWindow() {
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(e -> {
            e.consume();
            exit();
        });
        
        canvas = new Canvas();
        VBox root = new VBox();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        Render.setCanvas(canvas);
        Render.setZoom(50.0d);
        loop.start();
        stage.show();
        stage.centerOnScreen();
        stage.requestFocus();
        canvas.requestFocus();
        
        canvas.setOnKeyPressed(e -> {
            if (!input.keysDown.contains(e.getCode())) input.keysFresh.add(e.getCode());
            input.keysDown.add(e.getCode());
        });
        canvas.setOnKeyReleased(e -> {
            input.keysDown.remove(e.getCode());
            input.keysFreshUp.add(e.getCode());
        });
        canvas.setOnScroll(e -> input.scroll += e.getDeltaY() * Render.getZoom() / zoomSlow);
        canvas.setOnMousePressed(e -> {
            input.mouseDown.add(e.getButton());
            input.mouseFresh.add(e.getButton());
        });
        canvas.setOnMouseReleased(e -> {
            input.mouseDown.remove(e.getButton());
            input.mouseFreshUp.add(e.getButton());
        });
        EventHandler<? super MouseEvent> mm = e -> {
            input.mousePos.set(e.getX(), e.getY());
            input.mousePos.sub(input.prevMouse, input.mouseDelta);
        };
        canvas.setOnMouseMoved(mm);
        canvas.setOnMouseDragged(mm);
    }
    
    private void handleInput(double deltaTime) {
        if (provider.handleExtraInput(input, deltaTime)) return;
        
        if (input.mouseDown.contains(MouseButton.MIDDLE)) Render.offset.add(input.mouseDelta.mul(1.0d / Render.getZoom(), new Vector2d()));
        
        if (input.isKeyDown(KeyCode.W) || input.isKeyDown(KeyCode.UP)) Render.offset.y += moveSpeed / Render.getZoom() * deltaTime;
        if (input.isKeyDown(KeyCode.S) || input.isKeyDown(KeyCode.DOWN)) Render.offset.y -= moveSpeed / Render.getZoom() * deltaTime;
        if (input.isKeyDown(KeyCode.D) || input.isKeyDown(KeyCode.RIGHT)) Render.offset.x -= moveSpeed / Render.getZoom() * deltaTime;
        if (input.isKeyDown(KeyCode.A) || input.isKeyDown(KeyCode.LEFT)) Render.offset.x += moveSpeed / Render.getZoom() * deltaTime;
        
        if (input.scroll != 0.0d) {
            Vector2dc before = Render.transformPoint(input.mousePos);
            Render.setZoom(Render.getZoom() + input.scroll);
            Render.offset.add(Render.transformPoint(input.mousePos).sub(before));
        }
        
        // Editor
        
        boolean numPressed = false;
        int num = 0;
        if (input.isKeyJustDown(KeyCode.DIGIT0)) { numPressed = true; }
        if (input.isKeyJustDown(KeyCode.DIGIT1)) { numPressed = true; num = 1; }
        if (input.isKeyJustDown(KeyCode.DIGIT2)) { numPressed = true; num = 2; }
        if (input.isKeyJustDown(KeyCode.DIGIT3)) { numPressed = true; num = 3; }
        if (input.isKeyJustDown(KeyCode.DIGIT4)) { numPressed = true; num = 4; }
        if (input.isKeyJustDown(KeyCode.DIGIT5)) { numPressed = true; num = 5; }
        if (input.isKeyJustDown(KeyCode.DIGIT6)) { numPressed = true; num = 6; }
        if (input.isKeyJustDown(KeyCode.DIGIT7)) { numPressed = true; num = 7; }
        if (input.isKeyJustDown(KeyCode.DIGIT8)) { numPressed = true; num = 8; }
        if (input.isKeyJustDown(KeyCode.DIGIT9)) { numPressed = true; num = 9; }
        
        if (numPressed) editor.onNumPressed(num);
        editor.onUpdate(input.mousePos);
        
        if (input.isMouseJustDown(MouseButton.PRIMARY)) editor.onMouseJustDown(MouseButton.PRIMARY);
        if (input.isMouseDown(MouseButton.PRIMARY)) editor.onMousePressed(MouseButton.PRIMARY);
        if (input.isMouseJustUp(MouseButton.PRIMARY)) editor.onMouseJustUp(MouseButton.PRIMARY);
        if (input.isMouseJustDown(MouseButton.SECONDARY)) editor.onMouseJustDown(MouseButton.SECONDARY);
        if (input.isMouseDown(MouseButton.SECONDARY)) editor.onMousePressed(MouseButton.SECONDARY);
        if (input.isMouseJustUp(MouseButton.SECONDARY)) editor.onMouseJustUp(MouseButton.SECONDARY);
        
        if (input.isKeyJustDown(KeyCode.M)) editor.setMode((byte) (editor.getMode() + 1)); 
    }
    
    private void render(double deltaTime) {
        handleInput(deltaTime);
        input.reset();
        
        stage.setTitle(String.format("JAutomata 0.0.1 | Rendering %s chunks | Avg. FPS: %.2f | Avg. TPS: %.2f | Ticks: %s",
                automataWorld.getRenderingChunks(), 1.0d / deltaTime, 1.0d / automataHandler.getDeltaTime(), automataHandler.getTicks()));
        Render.clear(new Vector3d(0.078d, 0.078d, 0.078d));
        
        Render.applyTransformation();
        automataWorld.render(Render::fillRect);
        Render.removeTransformation();
        
        TimeDebug.finishAndPrintAll();
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public void startTickLoop() {
        automataHandler.run();
    }
    
    public void stopTickLoop() {
        automataHandler.stop();
    }
    
    public void singleTick() {
        automataHandler.tick();
    }
    
    public void reset() {
        automataHandler.reset();
    }
    
}
