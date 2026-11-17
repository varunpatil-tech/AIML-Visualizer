
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class PerceptronGraphView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private double x1 = 1;
    private double x2 = 1;
    private double w1 = 1;
    private double w2 = 1;
    private double bias = 0;
    private double threshold = 0;

    private static final double MIN = -5;
    private static final double MAX = 5;

    public PerceptronGraphView(Context context) {
        super(context);
    }

    public PerceptronGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPerceptronValues(
            double x1, double x2,
            double w1, double w2,
            double bias, double threshold) {

        this.x1 = x1;
        this.x2 = x2;
        this.w1 = w1;
        this.w2 = w2;
        this.bias = bias;
        this.threshold = threshold;
        invalidate();
    }

    private float screenX(double x, float left, float width) {
        return (float) (left + (x - MIN) / (MAX - MIN) * width);
    }

    private float screenY(double y, float top, float height) {
        return (float) (top + (MAX - y) / (MAX - MIN) * height);
    }

    private int classify(double a, double b) {
        return (w1 * a + w2 * b + bias >= threshold) ? 1 : 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = 70;
        float top = 35;
        float right = getWidth() - 25;
        float bottom = getHeight() - 65;

        float graphWidth = right - left;
        float graphHeight = bottom - top;

        if (graphWidth <= 0 || graphHeight <= 0) {
            return;
        }

        // Background
        canvas.drawColor(Color.WHITE);

        // Classification regions
        paint.setStyle(Paint.Style.FILL);

        int cells = 60;
        float cellWidth = graphWidth / cells;
        float cellHeight = graphHeight / cells;

        for (int row = 0; row < cells; row++) {
            for (int col = 0; col < cells; col++) {
                double gx = MIN + (col + 0.5) / cells * (MAX - MIN);
                double gy = MAX - (row + 0.5) / cells * (MAX - MIN);

                paint.setColor(classify(gx, gy) == 1
                        ? Color.rgb(218, 242, 225)
                        : Color.rgb(250, 225, 225));

                canvas.drawRect(
                        left + col * cellWidth,
                        top + row * cellHeight,
                        left + (col + 1) * cellWidth + 1,
                        top + (row + 1) * cellHeight + 1,
                        paint);
            }
        }

        // Grid
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = -5; i <= 5; i++) {
            float px = screenX(i, left, graphWidth);
            float py = screenY(i, top, graphHeight);

            canvas.drawLine(px, top, px, bottom, paint);
            canvas.drawLine(left, py, right, py, paint);
        }

        // Axes
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(2);

        canvas.drawLine(
                screenX(0, left, graphWidth), top,
                screenX(0, left, graphWidth), bottom, paint);

        canvas.drawLine(
                left, screenY(0, top, graphHeight),
                right, screenY(0, top, graphHeight), paint);

        // Decision boundary: w1*x1 + w2*x2 + bias = threshold
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);

        Path boundary = new Path();
        boolean started = false;

        for (int i = 0; i <= 500; i++) {
            double gx = MIN + i / 500.0 * (MAX - MIN);

            if (Math.abs(w2) < 1e-9) {
                if (i == 0 && Math.abs(w1) > 1e-9) {
                    double verticalX = (threshold - bias) / w1;
                    if (verticalX >= MIN && verticalX <= MAX) {
                        boundary.moveTo(
                                screenX(verticalX, left, graphWidth),
                                top);
                        boundary.lineTo(
                                screenX(verticalX, left, graphWidth),
                                bottom);
                    }
                }
                break;
            }

            double gy = (threshold - bias - w1 * gx) / w2;

            if (gy >= MIN && gy <= MAX) {
                float px = screenX(gx, left, graphWidth);
                float py = screenY(gy, top, graphHeight);

                if (!started) {
                    boundary.moveTo(px, py);
                    started = true;
                } else {
                    boundary.lineTo(px, py);
                }
            } else {
                started = false;
            }
        }

        canvas.drawPath(boundary, paint);

        // Current input point
        float pointX = screenX(x1, left, graphWidth);
        float pointY = screenY(x2, top, graphHeight);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(pointX, pointY, 10, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(pointX, pointY, 4, paint);

        // Axis labels and legend
        paint.setColor(Color.BLACK);
        paint.setTextSize(28);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("x1", right - 25, bottom + 35, paint);
        canvas.drawText("x2", left - 40, top + 10, paint);

        paint.setTextSize(24);
        canvas.drawText("Green: Output 1", left, getHeight() - 20, paint);

        paint.setColor(Color.RED);
        canvas.drawText("Red: Output 0", left + 220, getHeight() - 20, paint);
    }
}
