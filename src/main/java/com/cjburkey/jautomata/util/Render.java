package com.cjburkey.jautomata.util;

import com.sun.istack.internal.Nullable;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3dc;
import org.joml.Vector4dc;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "WeakerAccess", "unused"})
public final class Render {
    
    private static Optional<Canvas> ocanvas = Optional.empty();
    private static Optional<GraphicsContext> octx = Optional.empty();
    
    public static final Vector2d offset = new Vector2d();
    private static double zoom = 1.0d;
    
    private static final Vector2d canvasSize = new Vector2d();
    private static final Vector2d halfCanvasSize = new Vector2d();
    private static ChangeListener<? super Number> listenerW = (wp, o, n) -> {
        canvasSize.x = n.doubleValue();
        halfCanvasSize.x = canvasSize.x / 2.0d;
    };
    private static ChangeListener<? super Number> listenerH = (hp, o, n) -> {
        canvasSize.y = n.doubleValue();
        halfCanvasSize.y = canvasSize.y / 2.0d;
    };
    
    // -- RESET -- //
    
    public static void clear() {
        octx.ifPresent(ctx -> ocanvas.ifPresent(canvas -> ctx.clearRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight())));
    }
    
    public static void clear(Vector3dc color) {
        clear();
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            setFill(color);
            ctx.fillRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight());
        }));
    }
    
    // -- DRAWING -- //
    
    // -- RECT -- //
    
    public static void fillRect(double x, double y, double w, double h, Vector3dc fill) {
        setFill(fill);
        octx.ifPresent(ctx -> ctx.fillRect(coord(x), coord(y), w, h));
    }
    
    public static void fillRect(Vector2dc pos, Vector2dc size, Vector3dc fill) {
        pos = coord(pos);
        fillRect(pos.x(), pos.y(), size.x(), size.y(), fill);
    }
    
    public static void strokeRect(double x, double y, double w, double h, Vector3dc stroke, double strokeWidth) {
        setStroke(stroke, strokeWidth);
        octx.ifPresent(ctx -> ctx.strokeRect(coord(x), coord(y), w, h));
    }
    
    public static void strokeRect(Vector2dc pos, Vector2dc size, Vector3dc stroke, double strokeWidth) {
        pos = coord(pos);
        strokeRect(pos.x(), pos.y(), size.x(), size.y(), stroke, strokeWidth);
    }
    
    public static void fillAndStrokeRect(double x, double y, double w, double h, Vector3dc fill, Vector3dc stroke, double strokeWidth) {
        fillRect(coord(x), coord(y), w, h, fill);
        strokeRect(coord(x), coord(y), w, h, stroke, strokeWidth);
    }
    
    public static void fillAndStrokeRect(Vector2dc pos, Vector2dc size, Vector3dc fill, Vector3dc stroke, double strokeWidth) {
        pos = coord(pos);
        fillAndStrokeRect(pos.x(), pos.y(), size.x(), size.y(), fill, stroke, strokeWidth);
    }
    
    // -- OVAL -- //
    
    public static void fillOval(double x, double y, double w, double h, Vector3dc fill) {
        setFill(fill);
        octx.ifPresent(ctx -> ctx.fillOval(coord(x), coord(y), w, h));
    }
    
    public static void fillOval(Vector2dc pos, Vector2dc size, Vector3dc fill) {
        pos = coord(pos);
        fillOval(pos.x(), pos.y(), size.x(), size.y(), fill);
    }
    
    public static void strokeOval(double x, double y, double w, double h, Vector3dc stroke, double lineWidth) {
        setStroke(stroke, lineWidth);
        octx.ifPresent(ctx -> ctx.fillOval(coord(x), coord(y), w, h));
    }
    
    public static void strokeOval(Vector2dc pos, Vector2dc size, Vector3dc stroke, double lineWidth) {
        pos = coord(pos);
        strokeOval(pos.x(), pos.y(), size.x(), size.y(), stroke, lineWidth);
    }
    
    public static void fillAndStrokeOval(double x, double y, double w, double h, Vector3dc fill, Vector3dc stroke, double lineWidth) {
        fillOval(coord(x), coord(y), w, h, fill);
        strokeOval(coord(x), coord(y), w, h, stroke, lineWidth);
    }
    
    public static void fillAndStrokeOval(Vector2dc pos, Vector2dc size, Vector3dc fill, Vector3dc stroke, double lineWidth) {
        pos = coord(pos);
        fillAndStrokeOval(pos.x(), pos.y(), size.x(), size.y(), fill, stroke, lineWidth);
    }
    
    // -- LINE -- //
    
    public static void strokeLine(double x1, double y1, double x2, double y2, Vector3dc stroke, double lineWidth) {
        setStroke(stroke, lineWidth);
        octx.ifPresent(ctx -> ctx.strokeLine(coord(x1), coord(y1), coord(x2), coord(y2)));
    }
    
    public static void strokeLine(Vector2dc pos1, Vector2dc pos2, Vector3dc stroke, double lineWidth) {
        pos1 = coord(pos1);
        pos2 = coord(pos2);
        strokeLine(pos1.x(), pos1.y(), pos2.x(), pos2.y(), stroke, lineWidth);
    }
    
    // -- TRANSFORM -- //
    
    public static double getZoom() {
        return zoom;
    }
    
    public static void setZoom(double zoom) {
        Render.zoom = (zoom < 0.5d ? 0.5d : (zoom > 200.0d ? 200.0d : zoom));
    }
    
    public static Vector2d transformPoint(Vector2dc input) {
        return input.sub(halfCanvasSize, new Vector2d()).mul(1.0d / zoom).sub(offset).sub(0.5d, 0.5d);
    }
    
    public static Vector2d deTransformPoint(Vector2dc input) {
        return input.add(0.5d, 0.5d, new Vector2d()).add(offset).mul(zoom).add(halfCanvasSize);
    }
    
    public static boolean getIsOnScreen(Vector2dc point, double squareWidth) {
        double rsq = (squareWidth * squareWidth);
        Vector2d at = deTransformPoint(point);
        double x = Math.signum(at.x) * at.x * at.x;
        double y = Math.signum(at.y) * at.y * at.y;
        double a = zoom * zoom * rsq;
        return (x > -a) && (y > -a) && (x < canvasSize.x * canvasSize.x) && (y < canvasSize.y * canvasSize.y);
    }
    
    public static void applyTransformation() {
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            ctx.translate(halfCanvasSize.x, halfCanvasSize.y);
            ctx.scale(zoom, zoom);
            ctx.translate(offset.x, offset.y);
        }));
    }
    
    public static void removeTransformation() {
        ocanvas.ifPresent(canvas -> octx.ifPresent(ctx -> {
            ctx.translate(-offset.x, -offset.y);
            ctx.scale(1.0d / zoom, 1.0d / zoom);
            ctx.translate(-halfCanvasSize.x, -halfCanvasSize.y);
        }));
    }
    
    // -- STATE MANAGEMENT -- //
    
    private static double coord(double a) {
        return Helpers.floor(a) + 0.5d;
    }
    
    private static Vector2d coord(Vector2dc a) {
        return a.floor(new Vector2d()).add(0.5d, 0.5d);
    }
    
    private static void setFill(Vector3dc fill) {
        octx.ifPresent(ctx -> {
            Paint nFill = toColor(fill);
            if (!ctx.getFill().equals(nFill)) ctx.setFill(nFill);
        });
    }
    
    private static void setFill(Vector4dc fill) {
        octx.ifPresent(ctx -> {
            Paint nFill = toColor(fill);
            if (!ctx.getFill().equals(nFill)) ctx.setFill(nFill);
        });
    }
    
    private static void setStroke(Vector3dc stroke, double strokeWidth) {
        octx.ifPresent(ctx -> {
            Paint nStroke = toColor(stroke);
            if (!ctx.getStroke().equals(nStroke)) ctx.setStroke(nStroke);
            if (ctx.getLineWidth() != strokeWidth) ctx.setLineWidth(strokeWidth);
        });
    }
    
    private static void setStroke(Vector4dc stroke, double strokeWidth) {
        octx.ifPresent(ctx -> {
            Paint nStroke = toColor(stroke);
            if (!ctx.getStroke().equals(nStroke)) ctx.setStroke(nStroke);
            if (ctx.getLineWidth() != strokeWidth) ctx.setLineWidth(strokeWidth);
        });
    }
    
    private static Paint toColor(double r, double g, double b, double a) {
        return Color.rgb((int) (r * 255.0d), (int) (g * 255.0d), (int) (b * 255.0d), a);
    }
    
    private static Paint toColor(Vector4dc vec) {
        return toColor(vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    private static Paint toColor(Vector3dc vec) {
        return toColor(vec.x(), vec.y(), vec.z(), 1.0d);
    }
    
    public static void setCanvas(@Nullable Canvas ctx) {
        ocanvas.ifPresent(canvas -> {
            canvas.widthProperty().removeListener(listenerW);
            canvas.heightProperty().removeListener(listenerH);
        });
        ocanvas = Optional.ofNullable(ctx);
        ocanvas.ifPresent(canvas -> {
            canvas.widthProperty().addListener(listenerW);
            canvas.heightProperty().addListener(listenerH);
        });
        if (ctx == null) octx = Optional.empty();
        else octx = Optional.of(ctx.getGraphicsContext2D());
    }
    
}
