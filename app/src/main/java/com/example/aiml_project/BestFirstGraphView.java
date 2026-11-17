
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class BestFirstGraphView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final String[] nodes = {"S", "A", "B", "C", "G"};
    private final int[] heuristics = {7, 6, 2, 1, 0};

    private final float[][] positions = {
            {0.10f, 0.50f},
            {0.32f, 0.25f},
            {0.32f, 0.75f},
            {0.62f, 0.75f},
            {0.88f, 0.40f}
    };

    private final int[][] edges = {
            {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {1, 4}
    };

    private int currentNode = -1;
    private boolean[] visited = new boolean[5];
    private boolean[] inOpen = new boolean[5];
    private boolean[] inClosed = new boolean[5];
    private boolean[] inPath = new boolean[5];

    public BestFirstGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateState(
            int currentNode,
            boolean[] visited,
            boolean[] inOpen,
            boolean[] inClosed,
            boolean[] inPath
    ) {
        this.currentNode = currentNode;
        this.visited = visited.clone();
        this.inOpen = inOpen.clone();
        this.inClosed = inClosed.clone();
        this.inPath = inPath.clone();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(27);
        paint.setStrokeWidth(4);

        // Draw graph edges.
        for (int[] edge : edges) {
            int a = edge[0];
            int b = edge[1];

            float x1 = positions[a][0] * w;
            float y1 = positions[a][1] * h;
            float x2 = positions[b][0] * w;
            float y2 = positions[b][1] * h;

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(x1, y1, x2, y2, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        // Draw nodes and their heuristic values.
        for (int i = 0; i < nodes.length; i++) {
            float x = positions[i][0] * w;
            float y = positions[i][1] * h;

            int color = Color.rgb(219, 234, 254);

            if (inPath[i]) {
                color = Color.rgb(134, 239, 172);
            } else if (i == currentNode) {
                color = Color.rgb(253, 224, 71);
            } else if (inClosed[i]) {
                color = Color.rgb(254, 215, 170);
            } else if (inOpen[i]) {
                color = Color.rgb(187, 247, 208);
            }

            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y,  thirty(), paint);

            paint.setColor(Color.rgb(30, 41, 59));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawCircle(x, y, thirty(), paint);
            paint.setStyle(Paint.Style.FILL);

            paint.setTextSize(20);
            paint.setColor(Color.rgb(15, 23, 42));
            canvas.drawText(nodes[i], x, y + 7, paint);

            paint.setTextSize(14);
            canvas.drawText("h=" + heuristics[i], x, y + 48, paint);
        }

        paint.setTextSize(13);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.DKGRAY);
        canvas.drawText(
                "Yellow: Current   Green: Solution path",
                10,
                h - 10,
                paint
        );
    }

    private float thirty() {
        return 29f;
    }
}
