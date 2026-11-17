
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class NetworkDiagramView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float[][] inputHidden = new float[2][3];
    private final float[] hiddenOutput = new float[3];
    private final float[] hiddenValues = new float[3];

    private float input1;
    private float input2;
    private float output;

    public NetworkDiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateNetwork(
            float[][] weights1,
            float[] weights2,
            float[] hidden,
            float x1,
            float x2,
            float result
    ) {
        for (int i = 0; i < 2; i++) {
            System.arraycopy(weights1[i], 0, inputHidden[i], 0, 3);
        }

        System.arraycopy(weights2, 0, hiddenOutput, 0, 3);
        System.arraycopy(hidden, 0, hiddenValues, 0, 3);

        input1 = x1;
        input2 = x2;
        output = result;

        invalidate();
    }

    private void text(Canvas canvas, String value, float x, float y,
                      float size, int color) {
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1f);
        canvas.drawText(value, x, y, paint);
    }

    private void node(Canvas canvas, float x, float y, String label,
                      int color, String value) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(x, y, 23f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(0xFF172B4D);
        canvas.drawCircle(x, y, 23f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(12f);
        paint.setColor(0xFF172B4D);

        float labelWidth = paint.measureText(label);
        canvas.drawText(label, x - labelWidth / 2f, y + 4f, paint);

        paint.setTextSize(11f);
        paint.setColor(0xFF52627A);
        float valueWidth = paint.measureText(value);
        canvas.drawText(value, x - valueWidth / 2f, y + 39f, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        canvas.drawColor(0xFFF7F9FC);

        float inputX = width * 0.15f;
        float hiddenX = width * 0.50f;
        float outputX = width * 0.84f;

        float[] inputY = {
                height * 0.35f,
                height * 0.70f
        };

        float[] hiddenY = {
                height * 0.24f,
                height * 0.50f,
                height * 0.76f
        };

        float outputY = height * 0.50f;

        text(canvas, "INPUT", inputX - 20, 24, 12f, 0xFF4263A5);
        text(canvas, "HIDDEN", hiddenX - 22, 24, 12f, 0xFF4263A5);
        text(canvas, "OUTPUT", outputX - 25, 24, 12f, 0xFF4263A5);

        // Input-to-hidden connections.
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                drawConnection(
                        canvas,
                        inputX, inputY[i],
                        hiddenX, hiddenY[j],
                        inputHidden[i][j]
                );
            }
        }

        // Hidden-to-output connections.
        for (int j = 0; j < 3; j++) {
            drawConnection(
                    canvas,
                    hiddenX, hiddenY[j],
                    outputX, outputY,
                    hiddenOutput[j]
            );
        }

        node(canvas, inputX, inputY[0], "x1",
                0xFFDCEAFE, String.format("%.1f", input1));

        node(canvas, inputX, inputY[1], "x2",
                0xFFDCEAFE, String.format("%.1f", input2));

        for (int j = 0; j < 3; j++) {
            node(canvas, hiddenX, hiddenY[j], "H" + (j + 1),
                    0xFFDDF5E4,
                    String.format("%.2f", hiddenValues[j]));
        }

        node(canvas, outputX, outputY, "OUT",
                0xFFFFE4B8, String.format("%.3f", output));

        text(canvas, "Connection colour and thickness represent weight",
                12, height - 10, 11f, 0xFF52627A);
    }

    private void drawConnection(Canvas canvas,
                                float x1, float y1,
                                float x2, float y2,
                                float weight) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.2f + Math.min(Math.abs(weight), 3f));

        if (weight >= 0) {
            paint.setColor(0xFF21865B);
        } else {
            paint.setColor(0xFFD04A4A);
        }

        canvas.drawLine(x1, y1, x2, y2, paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
