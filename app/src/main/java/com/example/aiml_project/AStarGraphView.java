
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AStarGraphView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String activeNode = "";
    private final List<String> finalPath = new ArrayList<>();

    public AStarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    public void setActiveNode(String node) {
        activeNode = node == null ? "" : node;
        invalidate();
    }

    public void setFinalPath(List<String> path) {
        finalPath.clear();

        if (path != null) {
            finalPath.addAll(path);
        }

        invalidate();
    }

    private boolean isPathEdge(String from, String to) {
        for (int i = 0; i < finalPath.size() - 1; i++) {
            String first = finalPath.get(i);
            String second = finalPath.get(i + 1);

            if ((first.equals(from) && second.equals(to))
                    || (first.equals(to) && second.equals(from))) {
                return true;
            }
        }

        return false;
    }

    private boolean isPathNode(String node) {
        return finalPath.contains(node);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xFFFFFFFF);

        float w = getWidth();
        float h = getHeight();

        float sx = w * 0.15f, sy = h * 0.27f;
        float ax = w * 0.50f, ay = h * 0.27f;
        float gx = w * 0.85f, gy = h * 0.27f;
        float bx = w * 0.30f, by = h * 0.76f;
        float cx = w * 0.68f, cy = h * 0.76f;

        // Edges: path edges become green after the goal is found.
        drawEdge(canvas, sx, sy, ax, ay, "2", "S", "A", 0, -dp(13));
        drawEdge(canvas, ax, ay, gx, gy, "3", "A", "G", 0, -dp(13));
        drawEdge(canvas, sx, sy, bx, by, "4", "S", "B", -dp(15), 0);
        drawEdge(canvas, ax, ay, cx, cy, "2", "A", "C", dp(15), 0);
        drawEdge(canvas, bx, by, cx, cy, "1", "B", "C", 0, -dp(13));
        drawEdge(canvas, cx, cy, gx, gy, "5", "C", "G", dp(15), 0);

        drawNode(canvas, sx, sy, "S");
        drawNode(canvas, ax, ay, "A");
        drawNode(canvas, gx, gy, "G");
        drawNode(canvas, bx, by, "B");
        drawNode(canvas, cx, cy, "C");
    }

    private void drawEdge(
            Canvas canvas,
            float x1, float y1,
            float x2, float y2,
            String cost,
            String from, String to,
            float offsetX, float offsetY) {

        boolean highlighted = isPathEdge(from, to);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(highlighted ? dp(4) : dp(2));
        paint.setColor(highlighted ? 0xFF24945A : 0xFF9AA6B8);

        canvas.drawLine(x1, y1, x2, y2, paint);

        float midX = (x1 + x2) / 2f + offsetX;
        float midY = (y1 + y2) / 2f + offsetY;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(midX, midY, dp(11), paint);

        paint.setColor(highlighted ? 0xFF167541 : 0xFF33466B);
        paint.setTextSize(dp(13));
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(cost, midX, midY + dp(4), paint);
    }

    private void drawNode(Canvas canvas, float x, float y, String label) {
        float radius = dp(23);

        boolean onPath = isPathNode(label);
        boolean active = activeNode.equals(label);

        int fillColor;
        int borderColor;

        if (onPath) {
            fillColor = 0xFFDDF5E5;
            borderColor = 0xFF24945A;
        } else if (active) {
            fillColor = 0xFFFFD166;
            borderColor = 0xFFE0A526;
        } else {
            fillColor = 0xFFDFE9FF;
            borderColor = 0xFF5975B8;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawCircle(x, y, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(onPath ? dp(3) : dp(2));
        paint.setColor(borderColor);
        canvas.drawCircle(x, y, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF172B55);
        paint.setTextSize(dp(17));
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(label, x, y + dp(6), paint);
    }
}
