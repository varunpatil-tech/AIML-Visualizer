
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class HillLandscapeView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int[] values = {1, 3, 5, 4, 7, 6, 9, 8, 6};
    private int currentIndex = -1;
    private int startIndex = -1;
    private boolean finished = false;

    public HillLandscapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
        invalidate();
    }

    public void setStartIndex(int index) {
        startIndex = index;
        invalidate();
    }

    public void setFinished(boolean value) {
        finished = value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xFFFFFFFF);

        float width = getWidth();
        float height = getHeight();

        float left = dp(35);
        float right = width - dp(25);
        float top = dp(38);
        float bottom = height - dp(48);

        float usableWidth = right - left;
        float usableHeight = bottom - top;

        // Horizontal baseline.
        paint.setColor(0xFFD5DDE9);
        paint.setStrokeWidth(dp(1.5f));
        canvas.drawLine(left, bottom, right, bottom, paint);

        // Connect neighboring states.
        Path path = new Path();

        for (int i = 0; i < values.length; i++) {
            float x = getX(i, left, usableWidth);
            float y = getY(values[i], top, usableHeight);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(0xFF9AA6B8);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);

        // Draw states and their values.
        for (int i = 0; i < values.length; i++) {
            float x = getX(i, left, usableWidth);
            float y = getY(values[i], top, usableHeight);

            boolean current = i == currentIndex;
            boolean start = i == startIndex;

            int fillColor = 0xFFDFE9FF;
            int borderColor = 0xFF5975B8;

            // Highlight the global maximum.
            if (i == 6) {
                fillColor = 0xFFDDF5E5;
                borderColor = 0xFF24945A;
            }

            if (start) {
                fillColor = 0xFFFFE5B4;
                borderColor = 0xFFE0A526;
            }

            if (current) {
                fillColor = finished
                        ? 0xFFDDF5E5
                        : 0xFFFFD166;
                borderColor = finished
                        ? 0xFF24945A
                        : 0xFFE0A526;
            }

            paint.setColor(fillColor);
            canvas.drawCircle(x, y, dp(16), paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(borderColor);
            canvas.drawCircle(x, y, dp(16), paint);
            paint.setStyle(Paint.Style.FILL);

            paint.setColor(0xFF172B55);
            paint.setTextSize(dp(13));
            paint.setTypeface(Typeface.create(
                    Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    String.valueOf(values[i]),
                    x,
                    y + dp(5),
                    paint
            );

            paint.setTextSize(dp(12));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setColor(0xFF52627A);

            canvas.drawText(
                    "S" + i,
                    x,
                    bottom + dp(22),
                    paint
            );
        }

        // Label the global maximum.
        float globalX = getX(6, left, usableWidth);
        float globalY = getY(values[6], top, usableHeight);

        paint.setColor(0xFF167541);
        paint.setTextSize(dp(11));
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                "GLOBAL MAX",
                globalX,
                globalY - dp(24),
                paint
        );

        // Legend.
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(dp(11));
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(0xFF52627A);

        canvas.drawText(
                "State values: 1–9",
                left,
                dp(20),
                paint
        );
    }

    private float getX(int index, float left, float usableWidth) {
        return left + index * usableWidth / (values.length - 1);
    }

    private float getY(int value, float top, float usableHeight) {
        return top + usableHeight * (9 - value) / 9f;
    }
}
