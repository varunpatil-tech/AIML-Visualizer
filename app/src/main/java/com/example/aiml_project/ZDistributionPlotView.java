
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class ZDistributionPlotView extends View {

    private final Paint curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint curveFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rejectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint criticalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint observedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private double zScore = 0.0;
    private double alpha = 0.05;
    private boolean hasResult = false;

    private final double minZ = -4.0;
    private final double maxZ = 4.0;

    public ZDistributionPlotView(Context context) {
        super(context);
        init();
    }

    public ZDistributionPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZDistributionPlotView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        curvePaint.setColor(0xFF2563EB);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(dp(2.5f));

        curveFillPaint.setColor(0x333B82F6);
        curveFillPaint.setStyle(Paint.Style.FILL);

        rejectionPaint.setColor(0x55EF4444);
        rejectionPaint.setStyle(Paint.Style.FILL);

        axisPaint.setColor(0xFF94A3B8);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(dp(1f));

        criticalPaint.setColor(0xFFDC2626);
        criticalPaint.setStyle(Paint.Style.STROKE);
        criticalPaint.setStrokeWidth(dp(1.5f));
        criticalPaint.setPathEffect(
                new android.graphics.DashPathEffect(
                        new float[]{dp(5f), dp(4f)}, 0
                )
        );

        observedPaint.setColor(0xFF0F766E);
        observedPaint.setStyle(Paint.Style.STROKE);
        observedPaint.setStrokeWidth(dp(2.5f));

        textPaint.setColor(0xFF334155);
        textPaint.setTextSize(dp(11f));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    /**
     * Updates the observed Z-score and significance level from the calculator.
     */
    public void setZScore(double z, double significanceLevel) {
        if (!Double.isFinite(z)
                || !Double.isFinite(significanceLevel)
                || significanceLevel <= 0
                || significanceLevel >= 1) {
            hasResult = false;
        } else {
            zScore = z;
            alpha = significanceLevel;
            hasResult = true;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        float left = dp(24f);
        float right = width - dp(24f);
        float top = dp(36f);
        float bottom = height - dp(37f);

        if (right <= left || bottom <= top) {
            return;
        }

        float graphWidth = right - left;
        float graphHeight = bottom - top;

        float curveHeight = graphHeight * 0.78f;
        float baseline = bottom;

        // Draw a light baseline and the vertical zero line.
        canvas.drawLine(left, baseline, right, baseline, axisPaint);

        float zeroX = mapX(0, left, graphWidth);
        canvas.drawLine(
                zeroX, top, zeroX, baseline,
                makeLightAxisPaint()
        );

        double criticalZ = inverseNormalCdf(1.0 - alpha / 2.0);

        // Shade the two rejection regions for a two-tailed test.
        drawArea(
                canvas, left, graphWidth, baseline, curveHeight,
                minZ, -criticalZ, rejectionPaint
        );

        drawArea(
                canvas, left, graphWidth, baseline, curveHeight,
                criticalZ, maxZ, rejectionPaint
        );

        // Fill the entire area beneath the normal curve.
        Path fillPath = createCurvePath(
                left, graphWidth, baseline, curveHeight
        );
        fillPath.lineTo(right, baseline);
        fillPath.lineTo(left, baseline);
        fillPath.close();
        canvas.drawPath(fillPath, curveFillPaint);

        // Repaint the rejection areas on top of the general curve fill.
        drawArea(
                canvas, left, graphWidth, baseline, curveHeight,
                minZ, -criticalZ, rejectionPaint
        );

        drawArea(
                canvas, left, graphWidth, baseline, curveHeight,
                criticalZ, maxZ, rejectionPaint
        );

        // Draw the normal curve.
        canvas.drawPath(
                createCurvePath(left, graphWidth, baseline, curveHeight),
                curvePaint
        );

        // Critical Z boundaries.
        drawVerticalBoundary(
                canvas, -criticalZ, left, graphWidth, top, baseline
        );
        drawVerticalBoundary(
                canvas, criticalZ, left, graphWidth, top, baseline
        );

        textPaint.setColor(0xFFDC2626);
        canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", -criticalZ),
                mapX(-criticalZ, left, graphWidth),
                baseline + dp(16f),
                textPaint
        );
        canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", criticalZ),
                mapX(criticalZ, left, graphWidth),
                baseline + dp(16f),
                textPaint
        );

        // Z = 0 marker.
        textPaint.setColor(0xFF64748B);
        canvas.drawText("0", zeroX, baseline + dp(16f), textPaint);

        if (hasResult) {
            float plottedZ = (float) Math.max(minZ, Math.min(maxZ, zScore));
            float observedX = mapX(plottedZ, left, graphWidth);
            float observedY = baseline
                    - (float) normalDensity(plottedZ) * curveHeight
                    / (float) normalDensity(0);

            // Observed Z marker.
            canvas.drawLine(
                    observedX, observedY, observedX, baseline,
                    observedPaint
            );

            Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerPaint.setColor(0xFF0F766E);
            markerPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(
                    observedX, observedY, dp(4f), markerPaint
            );

            textPaint.setColor(0xFF0F766E);
            textPaint.setTextSize(dp(12f));

            String observedLabel = String.format(
                    Locale.getDefault(),
                    "Observed Z = %.2f",
                    zScore
            );

            float labelX = observedX;
            float labelY = Math.max(top + dp(13f), observedY - dp(9f));

            // Keep the label within the chart width.
            float halfLabel = textPaint.measureText(observedLabel) / 2f;
            labelX = Math.max(
                    left + halfLabel,
                    Math.min(right - halfLabel, labelX)
            );

            canvas.drawText(observedLabel, labelX, labelY, textPaint);

            textPaint.setTextSize(dp(10f));
            textPaint.setColor(0xFFDC2626);
            canvas.drawText(
                    String.format(
                            Locale.getDefault(),
                            "α/2 = %.3f",
                            alpha / 2.0
                    ),
                    left + graphWidth * 0.12f,
                    top + dp(13f),
                    textPaint
            );

            canvas.drawText(
                    String.format(
                            Locale.getDefault(),
                            "α/2 = %.3f",
                            alpha / 2.0
                    ),
                    right - graphWidth * 0.12f,
                    top + dp(13f),
                    textPaint
            );
        } else {
            textPaint.setColor(0xFF64748B);
            textPaint.setTextSize(dp(12f));
            canvas.drawText(
                    "Calculate the test to plot the observed Z-score",
                    (left + right) / 2f,
                    top + dp(18f),
                    textPaint
            );
        }

        textPaint.setColor(0xFF334155);
        textPaint.setTextSize(dp(11f));
        canvas.drawText(
                "Z-score",
                (left + right) / 2f,
                height - dp(5f),
                textPaint
        );

        // Tick labels at -4, -2, 0, 2, and 4.
        for (int tick = -4; tick <= 4; tick += 2) {
            float x = mapX(tick, left, graphWidth);
            canvas.drawLine(x, baseline, x, baseline + dp(3f), axisPaint);

            textPaint.setColor(0xFF64748B);
            canvas.drawText(
                    String.valueOf(tick),
                    x,
                    baseline + dp(27f),
                    textPaint
            );
        }
    }

    private Paint makeLightAxisPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x3394A3B8);
        paint.setStrokeWidth(dp(1f));
        return paint;
    }

    private float mapX(double z, float left, float graphWidth) {
        return left + (float) ((z - minZ) / (maxZ - minZ)) * graphWidth;
    }

    private double normalDensity(double z) {
        return Math.exp(-0.5 * z * z) / Math.sqrt(2.0 * Math.PI);
    }

    private Path createCurvePath(
            float left,
            float graphWidth,
            float baseline,
            float curveHeight
    ) {
        Path path = new Path();

        int steps = 240;

        for (int i = 0; i <= steps; i++) {
            double z = minZ + (maxZ - minZ) * i / steps;

            float x = mapX(z, left, graphWidth);
            float y = baseline
                    - (float) normalDensity(z) * curveHeight
                    / (float) normalDensity(0);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        return path;
    }

    private void drawArea(
            Canvas canvas,
            float left,
            float graphWidth,
            float baseline,
            float curveHeight,
            double startZ,
            double endZ,
            Paint paint
    ) {
        double start = Math.max(minZ, startZ);
        double end = Math.min(maxZ, endZ);

        if (start >= end) {
            return;
        }

        Path path = new Path();
        path.moveTo(mapX(start, left, graphWidth), baseline);

        int steps = Math.max(12, (int) ((end - start) * 45));

        for (int i = 0; i <= steps; i++) {
            double z = start + (end - start) * i / steps;

            float x = mapX(z, left, graphWidth);
            float y = baseline
                    - (float) normalDensity(z) * curveHeight
                    / (float) normalDensity(0);

            path.lineTo(x, y);
        }

        path.lineTo(mapX(end, left, graphWidth), baseline);
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawVerticalBoundary(
            Canvas canvas,
            double z,
            float left,
            float graphWidth,
            float top,
            float baseline
    ) {
        float x = mapX(z, left, graphWidth);

        canvas.drawLine(
                x, top, x, baseline, criticalPaint
        );
    }

    /**
     * Inverse standard normal CDF approximation.
     */
    private double inverseNormalCdf(double p) {
        double[] a = {
                -39.69683028665376,
                220.9460984245205,
                -275.9285104469687,
                138.3577518672690,
                -30.66479806614716,
                2.506628277459239
        };

        double[] b = {
                -54.47609879822406,
                161.5858368580409,
                -155.6989798598866,
                66.80131188771972,
                -13.28068155288572
        };

        double[] c = {
                -0.007784894002430293,
                -0.3223964580411365,
                -2.400758277161838,
                -2.549732539343734,
                4.374664141464968,
                2.938163982698783
        };

        double[] d = {
                0.007784695709041462,
                0.3224671290700398,
                2.445134137142996,
                3.754408661907416
        };

        double low = 0.02425;
        double high = 1.0 - low;

        if (p < low) {
            double q = Math.sqrt(-2.0 * Math.log(p));

            return (((((c[0] * q + c[1]) * q + c[2]) * q
                    + c[3]) * q + c[4]) * q + c[5])
                    / ((((d[0] * q + d[1]) * q + d[2]) * q
                    + d[3]) * q + 1.0);
        }

        if (p > high) {
            double q = Math.sqrt(-2.0 * Math.log(1.0 - p));

            return -(((((c[0] * q + c[1]) * q + c[2]) * q
                    + c[3]) * q + c[4]) * q + c[5])
                    / ((((d[0] * q + d[1]) * q + d[2]) * q
                    + d[3]) * q + 1.0);
        }

        double q = p - 0.5;
        double r = q * q;

        return (((((a[0] * r + a[1]) * r + a[2]) * r
                + a[3]) * r + a[4]) * r + a[5]) * q
                / (((((b[0] * r + b[1]) * r + b[2]) * r
                + b[3]) * r + b[4]) * r + 1.0);
    }
}
