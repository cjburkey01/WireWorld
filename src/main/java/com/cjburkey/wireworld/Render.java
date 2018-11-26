package com.cjburkey.wireworld;

import com.sun.istack.internal.Nullable;
import java.util.Optional;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "WeakerAccess", "unused"})
public final class Render {
    
    private static Optional<Canvas> ocanvas = Optional.empty();
    private static Optional<GraphicsContext> octx = Optional.empty();
    
    public static final Vector2d offset = new Vector2d();
    public static double zoom = 1.0d;
    
    // -- RESET -- //
    
    public static void clear() {
        octx.ifPresent(ctx -> ocanvas.ifPresent(canvas -> ctx.clearRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight())));
    }
    
    public static void clear(Vector3d color) {
        clear();
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            setFill(color);
            ctx.fillRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight());
        }));
    }
    
    // -- DRAWING -- //
    
    // -- RECT -- //
    
    public static void fillRect(double x, double y, double w, double h, Vector3d fill) {
        setFill(fill);
        octx.ifPresent(ctx -> ctx.fillRect(x - w / 2.0d, y - h / 2.0d, w, h));
    }
    
    public static void fillRect(Vector2d pos, Vector2d size, Vector3d fill) {
        fillRect(pos.x, pos.y, size.x, size.y, fill);
    }
    
    public static void strokeRect(double x, double y, double w, double h, Vector3d stroke, double strokeWidth) {
        setStroke(stroke, strokeWidth);
        octx.ifPresent(ctx -> ctx.strokeRect(x - w / 2.0d, y - h / 2.0d, w, h));
    }
    
    public static void strokeRect(Vector2d pos, Vector2d size, Vector3d stroke, double strokeWidth) {
        strokeRect(pos.x, pos.y, size.x, size.y, stroke, strokeWidth);
    }
    
    public static void fillAndStrokeRect(double x, double y, double w, double h, Vector3d fill, Vector3d stroke, double strokeWidth) {
        fillRect(x, y, w, h, fill);
        strokeRect(x, y, w, h, stroke, strokeWidth);
    }
    
    public static void fillAndStrokeRect(Vector2d pos, Vector2d size, Vector3d fill, Vector3d stroke, double strokeWidth) {
        fillAndStrokeRect(pos.x, pos.y, size.x, size.y, fill, stroke, strokeWidth);
    }
    
    // -- OVAL -- //
    
    public static void fillOval(double x, double y, double w, double h, Vector3d fill) {
        setFill(fill);
        octx.ifPresent(ctx -> ctx.fillOval(x - w / 2.0d, y - h / 2.0d, w, h));
    }
    
    public static void fillOval(Vector2d pos, Vector2d size, Vector3d fill) {
        fillOval(pos.x, pos.y, size.x, size.y, fill);
    }
    
    public static void strokeOval(double x, double y, double w, double h, Vector3d stroke, double lineWidth) {
        setStroke(stroke, lineWidth);
        octx.ifPresent(ctx -> ctx.fillOval(x - w / 2.0d, y - h / 2.0d, w, h));
    }
    
    public static void strokeOval(Vector2d pos, Vector2d size, Vector3d stroke, double lineWidth) {
        strokeOval(pos.x, pos.y, size.x, size.y, stroke, lineWidth);
    }
    
    public static void fillAndStrokeOval(double x, double y, double w, double h, Vector3d fill, Vector3d stroke, double lineWidth) {
        fillOval(x, y, w, h, fill);
        strokeOval(x, y, w, h, stroke, lineWidth);
    }
    
    public static void fillAndStrokeOval(Vector2d pos, Vector2d size, Vector3d fill, Vector3d stroke, double lineWidth) {
        fillAndStrokeOval(pos.x, pos.y, size.x, size.y, fill, stroke, lineWidth);
    }
    
    // -- LINE -- //
    
    public static void strokeLine(double x1, double y1, double x2, double y2, Vector3d stroke, double lineWidth) {
        setStroke(stroke, lineWidth);
        octx.ifPresent(ctx -> ctx.strokeLine(x1, y1, x2, y2));
    }
    
    public static void strokeLine(Vector2d pos1, Vector2d pos2, Vector3d stroke, double lineWidth) {
        strokeLine(pos1.x, pos1.y, pos2.x, pos2.y, stroke, lineWidth);
    }
    
    // -- TRANSFORM -- //
    
    public static Vector2d transformPoint(Vector2d input) {
        return input.add(offset, new Vector2d()).mul(zoom);
    }
    
    public static void applyTransformation() {
        zoom = (zoom < 0.5d ? 0.5d : (zoom > 200.0d ? 200.0d : zoom));
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            ctx.translate(canvas.getWidth() / 2.0d, canvas.getHeight() / 2.0d);
            ctx.scale(zoom, zoom);
            ctx.translate(offset.x, offset.y);
        }));
    }
    
    public static void removeTransformation() {
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            ctx.translate(-offset.x, -offset.y);
            ctx.scale(1.0d / zoom, 1.0d / zoom);
            ctx.translate(-canvas.getWidth() / 2.0d, -canvas.getHeight() / 2.0d);
        }));
    }
    
    // -- STATE MANAGEMENT -- //
    
    private static void setFill(Vector3d fill) {
        octx.ifPresent(ctx -> {
            Paint nFill = toColor(fill);
            if (!ctx.getFill().equals(nFill)) ctx.setFill(nFill);
        });
    }
    
    private static void setFill(Vector4d fill) {
        octx.ifPresent(ctx -> {
            Paint nFill = toColor(fill);
            if (!ctx.getFill().equals(nFill)) ctx.setFill(nFill);
        });
    }
    
    private static void setStroke(Vector3d stroke, double strokeWidth) {
        octx.ifPresent(ctx -> {
            Paint nStroke = toColor(stroke);
            if (!ctx.getStroke().equals(nStroke)) ctx.setStroke(nStroke);
            if (ctx.getLineWidth() != strokeWidth) ctx.setLineWidth(strokeWidth);
        });
    }
    
    private static void setStroke(Vector4d stroke, double strokeWidth) {
        octx.ifPresent(ctx -> {
            Paint nStroke = toColor(stroke);
            if (!ctx.getStroke().equals(nStroke)) ctx.setStroke(nStroke);
            if (ctx.getLineWidth() != strokeWidth) ctx.setLineWidth(strokeWidth);
        });
    }
    
    private static Paint toColor(double r, double g, double b, double a) {
        return Color.rgb((int) (r * 255.0d), (int) (g * 255.0d), (int) (b * 255.0d), a);
    }
    
    private static Paint toColor(Vector4d vec) {
        return toColor(vec.x, vec.y, vec.z, vec.w);
    }
    
    private static Paint toColor(Vector3d vec) {
        return toColor(vec.x, vec.y, vec.z, 1.0d);
    }
    
    public static void setCanvas(@Nullable Canvas ctx) {
        ocanvas = Optional.ofNullable(ctx);
        if (ctx == null) octx = Optional.empty();
        else octx = Optional.of(ctx.getGraphicsContext2D());
    }
    
}
