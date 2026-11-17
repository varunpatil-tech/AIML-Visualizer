
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HierarchicalTreeView extends View {

    public interface OnNodeSelectedListener {
        void onNodeSelected(int nodeIndex);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentStep = -1;

    private OnNodeSelectedListener nodeSelectedListener;

    private final String[] labels = {
            "Prepare\nBreakfast",
            "Prepare\nTea",
            "Prepare\nToast",
            "Boil\nWater",
            "Pour\nWater",
            "Steep\nTea",
            "Insert\nBread",
            "Toast\nBread"
    };

    public HierarchicalTreeView(Context context) {
        super(context);
        init();
    }

    public HierarchicalTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HierarchicalTreeView(
            Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.rgb(248, 247, 255));
        setClickable(true);
    }

    public void setOnNodeSelectedListener(
            OnNodeSelectedListener listener) {
        nodeSelectedListener = listener;
    }

    public void setCurrentStep(int step) {
        currentStep = step;
        invalidate();
    }

    private void drawConnection(
            Canvas canvas,
            float x1,
            float y1,
            float x2,
            float y2) {

        paint.setColor(Color.rgb(135, 143, 175));
        paint.setStrokeWidth(3.5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(x1, y1, x2, y2, paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawNode(
            Canvas canvas,
            float cx,
            float cy,
            float width,
            float height,
            String label,
            int nodeIndex,
            int level) {

        RectF rect = new RectF(
                cx - width / 2,
                cy - height / 2,
                cx + width / 2,
                cy + height / 2
        );

        boolean active = nodeIndex == currentStep;

        int fill;

        if (active) {
            fill = Color.rgb(255, 193, 7);
        } else if (level == 0) {
            fill = Color.rgb(48, 65, 125);
        } else if (level == 1 && nodeIndex == 1) {
            fill = Color.rgb(205, 228, 255);
        } else if (level == 1) {
            fill = Color.rgb(205, 239, 213);
        } else {
            fill = nodeIndex <= 5
                    ? Color.rgb(224, 237, 255)
                    : Color.rgb(224, 244, 229);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fill);
        canvas.drawRoundRect(rect, 15, 15, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(active ? 4 : 1.8f);
        paint.setColor(active
                ? Color.rgb(190, 125, 0)
                : Color.rgb(135, 147, 180));

        canvas.drawRoundRect(rect, 15, 15, paint);

        paint.setStyle(Paint.Style.FILL);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(
                Typeface.create("sans-serif-medium", Typeface.NORMAL));

        paint.setTextSize(level == 2 ? 14 : 17);
        paint.setColor(level == 0
                ? Color.WHITE
                : Color.rgb(35, 43, 65));

        String[] lines = label.split("\n");
        float lineHeight = level == 2 ? 18 : 22;

        float startY = cy
                - (lines.length - 1) * lineHeight / 2
                - (paint.ascent() + paint.descent()) / 2;

        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(
                    lines[i],
                    cx,
                    startY + i * lineHeight,
                    paint
            );
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        if (w <= 0 || h <= 0) {
            return;
        }

        canvas.drawColor(Color.rgb(248, 247, 255));

        // Vertical positions for the three tree levels.
        float rootY = h * 0.12f;
        float middleY = h * 0.39f;
        float leafY = h * 0.76f;

        float rootH = h * 0.12f;
        float middleH = h * 0.15f;
        float leafH = h * 0.16f;

        float rootW = w * 0.43f;
        float middleW = w * 0.34f;
        float leafW = w * 0.185f;

        // Root and middle-level positions.
        float rootX = w * 0.50f;
        float teaX = w * 0.29f;
        float toastX = w * 0.76f;

        // Leaf-level positions.
        float boilX = w * 0.10f;
        float pourX = w * 0.30f;
        float steepX = w * 0.50f;
        float insertX = w * 0.70f;
        float toastBreadX = w * 0.90f;

        // Prepare Breakfast -> Prepare Tea.
        drawConnection(
                canvas,
                rootX, rootY + rootH / 2,
                teaX, middleY - middleH / 2
        );

        // Prepare Breakfast -> Prepare Toast.
        drawConnection(
                canvas,
                rootX, rootY + rootH / 2,
                toastX, middleY - middleH / 2
        );

        // Prepare Tea -> its three actions.
        drawConnection(
                canvas,
                teaX, middleY + middleH / 2,
                boilX, leafY - leafH / 2
        );

        drawConnection(
                canvas,
                teaX, middleY + middleH / 2,
                pourX, leafY - leafH / 2
        );

        drawConnection(
                canvas,
                teaX, middleY + middleH / 2,
                steepX, leafY - leafH / 2
        );

        // Prepare Toast -> its two actions.
        drawConnection(
                canvas,
                toastX, middleY + middleH / 2,
                insertX, leafY - leafH / 2
        );

        drawConnection(
                canvas,
                toastX, middleY + middleH / 2,
                toastBreadX, leafY - leafH / 2
        );

        // Draw nodes on top of the connection lines.
        drawNode(canvas, rootX, rootY,
                rootW, rootH, labels[0], 0, 0);

        drawNode(canvas, teaX, middleY,
                middleW, middleH, labels[1], 1, 1);

        drawNode(canvas, toastX, middleY,
                middleW, middleH, labels[2], 2, 1);

        drawNode(canvas, boilX, leafY,
                leafW, leafH, labels[3], 3, 2);

        drawNode(canvas, pourX, leafY,
                leafW, leafH, labels[4], 4, 2);

        drawNode(canvas, steepX, leafY,
                leafW, leafH, labels[5], 5, 2);

        drawNode(canvas, insertX, leafY,
                leafW, leafH, labels[6], 6, 2);

        drawNode(canvas, toastBreadX, leafY,
                leafW, leafH, labels[7], 7, 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        float w = getWidth();
        float h = getHeight();

        float rootY = h * 0.12f;
        float middleY = h * 0.39f;
        float leafY = h * 0.76f;

        float rootH = h * 0.12f;
        float middleH = h * 0.15f;
        float leafH = h * 0.16f;

        float rootW = w * 0.43f;
        float middleW = w * 0.34f;
        float leafW = w * 0.185f;

        float[] centersX = {
                w * 0.50f,
                w * 0.29f,
                w * 0.76f,
                w * 0.10f,
                w * 0.30f,
                w * 0.50f,
                w * 0.70f,
                w * 0.90f
        };

        float[] centersY = {
                rootY,
                middleY,
                middleY,
                leafY,
                leafY,
                leafY,
                leafY,
                leafY
        };

        float[] widths = {
                rootW,
                middleW,
                middleW,
                leafW,
                leafW,
                leafW,
                leafW,
                leafW
        };

        float[] heights = {
                rootH,
                middleH,
                middleH,
                leafH,
                leafH,
                leafH,
                leafH,
                leafH
        };

        for (int i = 0; i < centersX.length; i++) {

            boolean inside =
                    x >= centersX[i] - widths[i] / 2 &&
                            x <= centersX[i] + widths[i] / 2 &&
                            y >= centersY[i] - heights[i] / 2 &&
                            y <= centersY[i] + heights[i] / 2;

            if (inside) {
                currentStep = i;
                invalidate();

                if (nodeSelectedListener != null) {
                    nodeSelectedListener.onNodeSelected(i);
                }

                performClick();
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
