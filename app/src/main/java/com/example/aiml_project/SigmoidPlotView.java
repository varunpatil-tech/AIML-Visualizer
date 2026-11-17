
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Locale;

public class SigmoidPlotView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private double currentZ = 2.0;
    private double currentProbability = 1.0 / (1.0 + Math.exp(-2.0));

    public SigmoidPlotView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setPoint(double z, double probability) {
        currentZ = z;
        currentProbability = probability;
        invalidate();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float mapX(double x, float left, float width) {
        return left + (float) ((x + 6.0) / 12.0) * width;
    }

    private float mapY(double y, float top, float height) {
        return top + height - (float) y * height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(248, 250, 254));

        float left = dp(42);
        float right = getWidth() - dp(16);
        float top = dp(22);
        float bottom = getHeight() - dp(36);

        float width = right - left;
        float height = bottom - top;

        if (width <= 0 || height <= 0) return;

        // Grid and axes
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.rgb(220, 227, 237));

        for (int i = 0; i <= 10; i += 2) {
            float x = mapX(i - 5, left, width);
            canvas.drawLine(x, top, x, bottom, paint);
        }

        for (int i = 0; i <= 4; i++) {
            float y = mapY(i * 0.25, top, height);
            canvas.drawLine(left, y, right, y, paint);
        }

        paint.setColor(Color.rgb(90, 105, 125));
        paint.setStrokeWidth(dp(1.4f));

        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, left, bottom, paint);

        // Axis labels
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp(10));
        paint.setColor(Color.rgb(70, 83, 105));

        for (int i = -6; i <= 6; i += 2) {
            canvas.drawText(
                    String.valueOf(i),
                    mapX(i, left, width) - dp(4),
                    bottom + dp(16),
                    paint
            );
        }

        for (int i = 0; i <= 4; i++) {
            String label = String.format(Locale.US, "%.2f", i * 0.25);
            canvas.drawText(
                    label,
                    dp(2),
                    mapY(i * 0.25, top, height) + dp(4),
                    paint
            );
        }

        // Draw sigmoid curve: sigma(z) = 1 / (1 + e^(-z))
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.rgb(7, 134, 160));

        float previousX = mapX(-6, left, width);
        float previousY = mapY(sigmoid(-6), top, height);

        for (double z = -5.95; z <= 6.0; z += 0.05) {
            float x = mapX(z, left, width);
            float y = mapY(sigmoid(z), top, height);

            canvas.drawLine(previousX, previousY, x, y, paint);

            previousX = x;
            previousY = y;
        }

        // Clamp marker to the visible graph range.
        double visibleZ = Math.max(-6, Math.min(6, currentZ));
        float pointX = mapX(visibleZ, left, width);
        float pointY = mapY(currentProbability, top, height);

        // Vertical guide
        paint.setColor(Color.rgb(210, 65, 65));
        paint.setStrokeWidth(dp(1.5f));
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(pointX, top, pointX, bottom, paint);

        // Horizontal guide
        paint.setColor(Color.rgb(230, 135, 35));
        canvas.drawLine(left, pointY, pointX, pointY, paint);

        // Current point
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(210, 65, 65));
        canvas.drawCircle(pointX, pointY, dp(5), paint);

        // Highlight the probability value.
        paint.setTextSize(dp(11));
        paint.setFakeBoldText(true);
        paint.setColor(Color.rgb(190, 55, 55));

        String probabilityLabel = String.format(
                Locale.US, "P = %.3f", currentProbability
        );

        float labelX = Math.min(
                pointX + dp(8),
                right - dp(66)
        );

        float labelY = Math.max(
                top + dp(14),
                pointY - dp(9)
        );

        canvas.drawText(
                probabilityLabel,
                labelX,
                labelY,
                paint
        );

        paint.setFakeBoldText(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp(11));
        paint.setColor(Color.rgb(23, 54, 93));

        canvas.drawText("z", right - dp(5), bottom + dp(30), paint);
        canvas.drawText("P(y=1)", dp(2), top - dp(7), paint);

        // Explain when the current score is outside the visible range.
        if (currentZ < -6 || currentZ > 6) {
            paint.setTextSize(dp(10));
            paint.setColor(Color.rgb(100, 110, 125));

            canvas.drawText(
                    "z = " + String.format(Locale.US, "%.2f", currentZ)
                            + " (outside displayed range)",
                    left + dp(4),
                    top + dp(14),
                    paint
            );
        }
    }

    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }
}
