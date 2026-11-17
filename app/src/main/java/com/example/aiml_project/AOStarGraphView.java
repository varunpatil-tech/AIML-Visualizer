
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class AOStarGraphView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int step = 0;

    private final int blue = Color.rgb(37, 99, 235);
    private final int green = Color.rgb(22, 163, 74);
    private final int orange = Color.rgb(234, 88, 12);
    private final int dark = Color.rgb(51, 65, 85);
    private final int gray = Color.rgb(148, 163, 184);

    public AOStarGraphView(Context context) {
        super(context);
        init();
    }

    public AOStarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AOStarGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStrokeWidth(3f);
        paint.setTextSize(14f);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStep(int newStep) {
        step = newStep;
        invalidate();
    }

    private void drawLine(Canvas canvas, float x1, float y1,
                          float x2, float y2, int color, String label) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(color == green ? 5f : 3f);
        canvas.drawLine(x1, y1, x2, y2, paint);

        if (label != null && !label.isEmpty()) {
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(13f);
            paint.setFakeBoldText(true);
            canvas.drawText(label, (x1 + x2) / 2f + 4f,
                    (y1 + y2) / 2f - 5f, paint);
            paint.setFakeBoldText(false);
        }
    }

    private void drawNode(Canvas canvas, float x, float y,
                          String name, String heuristic, int fillColor,
                          boolean active) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawCircle(x, y, 27f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(active ? 5f : 2f);
        paint.setColor(active ? orange : dark);
        canvas.drawCircle(x, y, 27f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(17f);
        paint.setFakeBoldText(true);
        canvas.drawText(name, x - paint.measureText(name) / 2f, y + 6f, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(12f);
        paint.setColor(dark);
        canvas.drawText(heuristic, x - paint.measureText(heuristic) / 2f,
                y + 43f, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(255, 255, 255));

        float w = getWidth();
        float h = getHeight();

        float sx = w * 0.50f;
        float sy = h * 0.14f;

        float ax = w * 0.25f;
        float ay = h * 0.43f;

        float bx = w * 0.67f;
        float by = h * 0.43f;

        float cx = w * 0.88f;
        float cy = h * 0.43f;

        float dx = w * 0.12f;
        float dy = h * 0.78f;

        float ex = w * 0.36f;
        float ey = h * 0.78f;

        int saColor = step >= 5 ? green : dark;
        int aeColor = step >= 5 ? green : dark;

        drawLine(canvas, sx, sy + 27, ax, ay - 27,
                saColor, "1");

        drawLine(canvas, sx + 20, sy + 20, bx - 10, by - 27,
                gray, "1");

        drawLine(canvas, sx + 25, sy + 15, cx - 15, cy - 27,
                gray, "1");

        drawLine(canvas, ax - 8, ay + 25, dx + 8, dy - 27,
                gray, "1");

        drawLine(canvas, ax + 12, ay + 24, ex - 7, ey - 27,
                aeColor, "1");

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(dark);
        paint.setTextSize(12f);
        paint.setFakeBoldText(true);
        canvas.drawText("OR", sx - 12f, sy + 47f, paint);
        canvas.drawText("AND", w * 0.76f, sy + 47f, paint);
        canvas.drawText("OR", ax - 12f, ay + 50f, paint);
        paint.setFakeBoldText(false);

        int sColor = step >= 5 ? green : blue;
        int aColor = step >= 5 ? green : (step >= 3 ? orange : blue);
        int bColor = blue;
        int cColor = blue;
        int dColor = blue;
        int eColor = step >= 5 ? green : blue;

        drawNode(canvas, sx, sy, "S", "h=4", sColor, step == 1);
        drawNode(canvas, ax, ay, "A", step >= 4 ? "cost=3" : "h=2",
                aColor, step == 3);
        drawNode(canvas, bx, by, "B", "cost=2", bColor, false);
        drawNode(canvas, cx, cy, "C", "cost=2", cColor, false);
        drawNode(canvas, dx, dy, "D", "cost=4", dColor, false);
        drawNode(canvas, ex, ey, "E", "cost=2", eColor, step >= 5);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(dark);
        paint.setTextSize(12f);
        canvas.drawText("B AND C must both be solved", w * 0.48f,
                h * 0.63f, paint);

        if (step >= 5) {
            paint.setColor(green);
            paint.setTextSize(13f);
            paint.setFakeBoldText(true);
            canvas.drawText("OPTIMAL SOLUTION: S → A → E",
                    w * 0.10f, h * 0.94f, paint);
            paint.setFakeBoldText(false);
        }
    }
}
