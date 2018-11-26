package com.cjburkey.wireworld;

import com.cjburkey.wireworld.world.Chunk;
import com.cjburkey.wireworld.world.World;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"unused", "WeakerAccess"})
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
    private final Vector2d prevMouse = new Vector2d();
    private final Vector2d mouseDelta = new Vector2d();
    private double zoom = 0.0d;
    private boolean movingWithMouse = false;
    
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
        Render.zoom = 50.0d;
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
        canvas.setOnScroll(e -> zoom += e.getDeltaY() * Render.zoom / zoomSlow);
        canvas.setOnMousePressed(e -> {
            if (e.getButton().equals(MouseButton.MIDDLE)) {
                movingWithMouse = true;
            }
        });
        canvas.setOnMouseReleased(e -> {
            if (e.getButton().equals(MouseButton.MIDDLE)) movingWithMouse = false;
        });
        canvas.setOnMouseMoved(e -> {
            mouseDelta.set(e.getX() - prevMouse.x, e.getY() - prevMouse.y);
            prevMouse.set(e.getX(), e.getY());
            if (movingWithMouse) {
                Render.offset.add(mouseDelta.mul(1.0d / Render.zoom, new Vector2d()));
            }
        });
        canvas.setOnMouseDragged(e -> {
            mouseDelta.set(e.getX() - prevMouse.x, e.getY() - prevMouse.y);
            prevMouse.set(e.getX(), e.getY());
            if (movingWithMouse) {
                Render.offset.add(mouseDelta.mul(1.0d / Render.zoom, new Vector2d()));
            }
        });
        
        world.setTile(new Vector2i(), Chunk.TileType.CONDUCTOR);
    }
    
    private void handleInput(double deltaTime) {
        if (keysDown.contains(KeyCode.W) || keysDown.contains(KeyCode.UP)) Render.offset.y += moveSpeed / Render.zoom * deltaTime;
        if (keysDown.contains(KeyCode.S) || keysDown.contains(KeyCode.DOWN)) Render.offset.y -= moveSpeed / Render.zoom * deltaTime;
        if (keysDown.contains(KeyCode.D) || keysDown.contains(KeyCode.RIGHT)) Render.offset.x -= moveSpeed / Render.zoom * deltaTime;
        if (keysDown.contains(KeyCode.A) || keysDown.contains(KeyCode.LEFT)) Render.offset.x += moveSpeed / Render.zoom * deltaTime;
        
        Render.zoom += zoom;
    }
    
    private void render(double deltaTime) {
        handleInput(deltaTime);
        keysFresh.clear();
        zoom = 0.0d;
        
        stage.setTitle(String.format("CJ Burkey's WireWorld 0.0.1 | Avg. FPS: %.2f", 1.0d / deltaTime));
        Render.clear(new Vector3d(0.078d, 0.078d, 0.078d));
        
        Render.applyTransformation();
        world.getChunk(new Vector2i()).ifPresent(Chunk::render);
        Render.removeTransformation();
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
}
