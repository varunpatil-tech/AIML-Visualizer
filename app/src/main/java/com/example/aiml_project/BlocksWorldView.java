
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BlocksWorldView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // support[i] = -1 means block i is on the table.
    // Otherwise, the value is the index of the block supporting it.
    // A = 0, B = 1, C = 2.
    private int[] support = {-1, -1, 1};

    private final int[] blockColors = {
            Color.rgb(37, 99, 235),
            Color.rgb(22, 163, 74),
            Color.rgb(234, 88, 12)
    };

    private final String[] blockNames = {"A", "B", "C"};

    public BlocksWorldView(Context context) {
        super(context);
        init();
    }

    public BlocksWorldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlocksWorldView(Context context, AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    public void setState(int[] newSupport) {
        support = newSupport.clone();
        invalidate();
    }

    private void drawBlock(Canvas canvas, int block, float centerX,
                           float bottomY, float blockWidth, float blockHeight) {
        RectF rect = new RectF(
                centerX - blockWidth / 2f,
                bottomY - blockHeight,
                centerX + blockWidth / 2f,
                bottomY
        );

        paint.setColor(blockColors[block]);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 8f, 8f, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(20f);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textY = rect.centerY() - (metrics.ascent + metrics.descent) / 2f;
        canvas.drawText(blockNames[block], centerX, textY, paint);

        paint.setFakeBoldText(false);
    }

    private void drawStack(Canvas canvas, int block, float centerX,
                           float bottomY, float blockWidth,
                           float blockHeight) {
        drawBlock(canvas, block, centerX, bottomY, blockWidth, blockHeight);

        float nextBottom = bottomY - blockHeight;

        for (int child = 0; child < 3; child++) {
            if (support[child] == block) {
                drawStack(canvas, child, centerX, nextBottom,
                        blockWidth, blockHeight);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        float width = getWidth();
        float height = getHeight();

        float blockWidth = Math.min(width * 0.22f, 105f);
        float blockHeight = 43f;
        float tableY = height * 0.78f;

        paint.setColor(Color.rgb(100, 116, 139));
        paint.setStrokeWidth(5f);
        canvas.drawLine(width * 0.06f, tableY,
                width * 0.94f, tableY, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(13f);
        paint.setColor(Color.rgb(71, 85, 105));
        canvas.drawText("TABLE", width / 2f, tableY + 22f, paint);

        float[] rootPositions = {
                width * 0.22f,
                width * 0.50f,
                width * 0.78f
        };

        for (int block = 0; block < 3; block++) {
            if (support[block] == -1) {
                drawStack(canvas, block, rootPositions[block], tableY,
                        blockWidth, blockHeight);
            }
        }
    }
}
