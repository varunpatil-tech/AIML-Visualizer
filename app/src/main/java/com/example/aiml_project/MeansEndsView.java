
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MeansEndsView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Index 1 = smallest disk; index 3 = largest disk.
    // Each value indicates the peg containing that disk.
    private int[] diskPeg = {0, 0, 0, 0};

    public MeansEndsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDiskPositions(int[] positions) {
        diskPeg = positions.clone();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float[] pegX = {
                width * 0.20f,
                width * 0.50f,
                width * 0.80f
        };

        float baseY = height * 0.78f;
        float pegTop = height * 0.20f;
        float pegWidth = Math.max(5f, width * 0.012f);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(71, 85, 105));

        // Draw the bases and vertical pegs.
        for (int i = 0; i < 3; i++) {
            canvas.drawRect(
                    pegX[i] - width * 0.13f,
                    baseY,
                    pegX[i] + width * 0.13f,
                    baseY + 10f,
                    paint
            );

            canvas.drawRect(
                    pegX[i] - pegWidth / 2,
                    pegTop,
                    pegX[i] + pegWidth / 2,
                    baseY,
                    paint
            );
        }

        // Draw disks from largest to smallest so they stack correctly.
        float diskHeight = height * 0.105f;
        float maxDiskWidth = width * 0.235f;
        float minDiskWidth = width * 0.095f;

        int[] diskColors = {
                Color.TRANSPARENT,
                Color.rgb(96, 165, 250),
                Color.rgb(59, 130, 246),
                Color.rgb(37, 99, 235)
        };

        for (int disk = 3; disk >= 1; disk--) {
            int peg = diskPeg[disk];

            if (peg < 0 || peg > 2) {
                continue;
            }

            int disksBelow = 0;

            for (int other = 1; other <= 3; other++) {
                if (other > disk && diskPeg[other] == peg) {
                    disksBelow++;
                }
            }

            float diskWidth =
                    minDiskWidth
                            + (disk - 1) * (maxDiskWidth - minDiskWidth) / 2f;

            float bottom = baseY - disksBelow * diskHeight;
            float left = pegX[peg] - diskWidth / 2f;
            float right = pegX[peg] + diskWidth / 2f;
            float top = bottom - diskHeight + 2f;

            paint.setColor(diskColors[disk]);
            canvas.drawRoundRect(
                    left, top, right, bottom - 2f,
                    10f, 10f, paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(15f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(
                    String.valueOf(disk),
                    pegX[peg],
                    top + diskHeight * 0.68f,
                    paint
            );
        }

        // Label the pegs.
        paint.setColor(Color.rgb(30, 41, 59));
        paint.setTextSize(17f);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("A", pegX[0], baseY + 31f, paint);
        canvas.drawText("B", pegX[1], baseY + 31f, paint);
        canvas.drawText("C", pegX[2], baseY + 31f, paint);

        paint.setTextSize(12f);
        paint.setColor(Color.rgb(100, 116, 139));
        canvas.drawText(
                "Move every disk from A to C",
                width / 2f,
                height - 7f,
                paint
        );
    }
}
