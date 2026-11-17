
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CSPGraphView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final String[] regions = {"A", "B", "C", "D"};

    // Positions of regions in the graph.
    private final float[][] positions = {
            {0.22f, 0.28f},
            {0.72f, 0.28f},
            {0.22f, 0.70f},
            {0.72f, 0.70f}
    };

    // Constraints: A-B, A-C, B-C, B-D, C-D.
    private final int[][] edges = {
            {0, 1},
            {0, 2},
            {1, 2},
            {1, 3},
            {2, 3}
    };

    private int[] assignments = {-1, -1, -1, -1};
    private int activeRegion = -1;
    private int conflictRegion = -1;
    private int successfulRegion = -1;

    public CSPGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateState(
            int[] assignments,
            int activeRegion,
            int conflictRegion,
            int successfulRegion
    ) {
        this.assignments = assignments.clone();
        this.activeRegion = activeRegion;
        this.conflictRegion = conflictRegion;
        this.successfulRegion = successfulRegion;
        invalidate();
    }

    private int colorFor(int value) {
        if (value == 0) return Color.rgb(239, 68, 68);
        if (value == 1) return Color.rgb(34, 197, 94);
        if (value == 2) return Color.rgb(59, 130, 246);
        return Color.rgb(203, 213, 225);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        // Draw constraint edges.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(Color.rgb(148, 163, 184));

        for (int[] edge : edges) {
            float x1 = positions[edge[0]][0] * width;
            float y1 = positions[edge[0]][1] * height;
            float x2 = positions[edge[1]][0] * width;
            float y2 = positions[edge[1]][1] * height;

            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        // Draw regions.
        for (int i = 0; i < regions.length; i++) {
            float x = positions[i][0] * width;
            float y = positions[i][1] * height;
            float radius = Math.min(width * 0.09f, 39f);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colorFor(assignments[i]));
            canvas.drawCircle(x, y, radius, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(i == conflictRegion ? 6f : 3f);

            if (i == conflictRegion) {
                paint.setColor(Color.rgb(185, 28, 28));
            } else if (i == activeRegion) {
                paint.setColor(Color.rgb(234, 179, 8));
            } else if (i == successfulRegion) {
                paint.setColor(Color.rgb(22, 101, 52));
            } else {
                paint.setColor(Color.rgb(51, 65, 85));
            }

            canvas.drawCircle(x, y, radius, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize(22f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(regions[i], x, y + 7f, paint);

            if (assignments[i] != -1) {
                String[] names = {"Red", "Green", "Blue"};
                paint.setTextSize(13f);
                paint.setColor(Color.rgb(30, 41, 59));
                canvas.drawText(
                        names[assignments[i]],
                        x,
                        y + radius + 19f,
                        paint
                );
            }
        }

        // Legend.
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(12f);
        paint.setColor(Color.rgb(71, 85, 105));
        canvas.drawText(
                "Lines = constraints | Red outline = conflict",
                10f,
                height - 10f,
                paint
        );
    }
}
