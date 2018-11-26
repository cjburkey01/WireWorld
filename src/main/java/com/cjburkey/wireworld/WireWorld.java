package com.cjburkey.wireworld;

import com.cjburkey.jautomata.world.World;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
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
import org.joml.Vector2i;
import org.joml.Vector3d;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"unused", "WeakerAccess", "MismatchedQueryAndUpdateOfCollection"})
public final class WireWorld extends Application {
    
    private Stage stage;
    private Canvas canvas;
    private AnimationTimer loop = new AnimationTimer() {
        long lastRender = System.nanoTime();
        public void handle(long now) {
            render((now - lastRender) / 1000000000.0d);
            lastRender = now;
        }
    };
    public final World world = new World();
    private static final double zoomSlow = 100.0d;
    private static final double moveSpeed = 750.0d;
    
    // Input
    private final ObjectOpenHashSet<KeyCode> keysDown = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<KeyCode> keysFresh = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<MouseButton> mouseDown = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<MouseButton> mouseFresh = new ObjectOpenHashSet<>();
    private final Vector2d mousePos = new Vector2d();
    private final Vector2d prevMouse = new Vector2d();
    private final Vector2d mouseDelta = new Vector2d();
    private double zoom = 0.0d;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage stage) {
        this.stage = stage;
        initWindow();
    }
    
    private void initWindow() {
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
            keysDown.add(e.getCode());
            keysFresh.add(e.getCode());
        });
        canvas.setOnKeyReleased(e -> keysDown.remove(e.getCode()));
        canvas.setOnScroll(e -> zoom += e.getDeltaY() * Render.getZoom() / zoomSlow);
        canvas.setOnMousePressed(e -> {
            mouseDown.add(e.getButton());
            mouseFresh.add(e.getButton());
        });
        canvas.setOnMouseReleased(e -> mouseDown.remove(e.getButton()));
        EventHandler<? super MouseEvent> mm = e -> {
            mousePos.set(e.getX(), e.getY());
            mousePos.sub(prevMouse, mouseDelta);
        };
        canvas.setOnMouseMoved(mm);
        canvas.setOnMouseDragged(mm);
        
        world.setTile(new Vector2i(0, 0), (byte) 0x03);
    }
    
    private void handleInput(double deltaTime) {
        if (mouseDown.contains(MouseButton.MIDDLE)) Render.offset.add(mouseDelta.mul(1.0d / Render.getZoom(), new Vector2d()));
        
        if (keysDown.contains(KeyCode.W) || keysDown.contains(KeyCode.UP)) Render.offset.y += moveSpeed / Render.getZoom() * deltaTime;
        if (keysDown.contains(KeyCode.S) || keysDown.contains(KeyCode.DOWN)) Render.offset.y -= moveSpeed / Render.getZoom() * deltaTime;
        if (keysDown.contains(KeyCode.D) || keysDown.contains(KeyCode.RIGHT)) Render.offset.x -= moveSpeed / Render.getZoom() * deltaTime;
        if (keysDown.contains(KeyCode.A) || keysDown.contains(KeyCode.LEFT)) Render.offset.x += moveSpeed / Render.getZoom() * deltaTime;
        
        if (zoom != 0.0d) {
            Vector2dc before = Render.transformPoint(mousePos);
            Render.setZoom(Render.getZoom() + zoom);
            Render.offset.add(Render.transformPoint(mousePos).sub(before));
        }
    }
    
    private void render(double deltaTime) {
        handleInput(deltaTime);
        keysFresh.clear();
        mouseFresh.clear();
        zoom = 0.0d;
        mouseDelta.set(0.0d);
        prevMouse.set(mousePos);
        
        stage.setTitle(String.format("CJ Burkey's WireWorld 0.0.1 | Avg. FPS: %.2f", 1.0d / deltaTime));
        Render.clear(new Vector3d(0.078d, 0.078d, 0.078d));
        
        Render.applyTransformation();
        world.render((x, y, color) -> Render.fillRect(x, y, 1.0d, 1.0d, color));
        Render.removeTransformation();
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
}
