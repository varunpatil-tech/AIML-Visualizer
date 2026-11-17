package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Locale;

public class ROCPlotView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private double fpr = 0.20;
    private double tpr = 0.80;
    private boolean hasPoint = true;

    public ROCPlotView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setPoint(double fpr, double tpr) {
        this.fpr = Math.max(0.0, Math.min(1.0, fpr));
        this.tpr = Math.max(0.0, Math.min(1.0, tpr));
        this.hasPoint = true;
        invalidate();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float xPosition(double value, float left, float width) {
        return left + (float) value * width;
    }

    private float yPosition(double value, float top, float height) {
        return top + height - (float) value * height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(248, 250, 254));

        float left = dp(48);
        float right = getWidth() - dp(18);
        float top = dp(30);
        float bottom = getHeight() - dp(48);

        float width = right - left;
        float height = bottom - top;

        if (width <= 0 || height <= 0) {
            return;
        }

        // Grid
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.rgb(220, 227, 237));

        for (int i = 0; i <= 4; i++) {
            double value = i / 4.0;

            float x = xPosition(value, left, width);
            float y = yPosition(value, top, height);

            canvas.drawLine(x, top, x, bottom, paint);
            canvas.drawLine(left, y, right, y, paint);
        }

        // Axes
        paint.setColor(Color.rgb(90, 105, 125));
        paint.setStrokeWidth(dp(1.5f));

        canvas.drawLine(left, top, left, bottom, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);

        // Tick labels
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp(10));
        paint.setColor(Color.rgb(70, 83, 105));

        for (int i = 0; i <= 4; i++) {
            double value = i / 4.0;

            String label = String.format(Locale.US, "%.2f", value);

            float x = xPosition(value, left, width);
            float y = yPosition(value, top, height);

            canvas.drawText(label, x - dp(12), bottom + dp(17), paint);
            canvas.drawText(label, dp(3), y + dp(4), paint);
        }

        // Random-classifier reference line
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.rgb(145, 155, 170));

        paint.setPathEffect(new android.graphics.DashPathEffect(
                new float[]{dp(6), dp(5)}, 0
        ));

        canvas.drawLine(
                xPosition(0, left, width),
                yPosition(0, top, height),
                xPosition(1, left, width),
                yPosition(1, top, height),
                paint
        );

        paint.setPathEffect(null);

        // Current ROC point
        if (hasPoint) {
            float pointX = xPosition(fpr, left, width);
            float pointY = yPosition(tpr, top, height);

            // Vertical guide
            paint.setColor(Color.rgb(7, 134, 160));
            paint.setStrokeWidth(dp(1.5f));
            canvas.drawLine(pointX, pointY, pointX, bottom, paint);

            // Horizontal guide
            paint.setColor(Color.rgb(230, 135, 35));
            canvas.drawLine(left, pointY, pointX, pointY, paint);

            // Marker
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(210, 65, 65));
            canvas.drawCircle(pointX, pointY, dp(6), paint);

            // Marker outline
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.WHITE);
            canvas.drawCircle(pointX, pointY, dp(6), paint);
        }

        // Axis titles
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(23, 43, 77));
        paint.setTextSize(dp(11));
        paint.setFakeBoldText(true);

        canvas.drawText("FPR", right - dp(25), bottom + dp(36), paint);

        canvas.save();
        canvas.rotate(-90, dp(13), top + height / 2);
        canvas.drawText("TPR", dp(13), top + height / 2, paint);
        canvas.restore();

        // Reference-line label
        paint.setFakeBoldText(false);
        paint.setTextSize(dp(10));
        paint.setColor(Color.rgb(110, 120, 135));

        canvas.drawText(
                "Random baseline",
                left + dp(8),
                bottom - dp(8),
                paint
        );

        if (hasPoint) {
            paint.setTextSize(dp(11));
            paint.setFakeBoldText(true);
            paint.setColor(Color.rgb(190, 55, 55));

            String pointLabel = String.format(
                    Locale.US, "(%.2f, %.2f)", fpr, tpr
            );

            float labelX = Math.min(
                    xPosition(fpr, left, width) + dp(8),
                    right - dp(90)
            );

            float labelY = Math.max(
                    top + dp(15),
                    yPosition(tpr, top, height) - dp(10)
            );

            canvas.drawText(pointLabel, labelX, labelY, paint);
            paint.setFakeBoldText(false);
        }
    }
}