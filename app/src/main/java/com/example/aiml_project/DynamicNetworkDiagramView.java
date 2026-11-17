
package com.example.aiml_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DynamicNetworkDiagramView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int[] architecture = {2, 4, 3, 1};

    public DynamicNetworkDiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicNetworkDiagramView(Context context) {
        super(context);
    }

    public void updateNetwork(
            int[] architecture,
            float[][][] weights,
            float[][] activations
    ) {
        if (architecture == null || architecture.length < 2) {
            return;
        }

        this.architecture = architecture.clone();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        canvas.drawColor(Color.WHITE);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25f);
        paint.setColor(Color.BLACK);

        canvas.drawText(
                "NETWORK DIAGRAM TEST",
                width / 2f,
                30f,
                paint
        );

        if (width <= 0 || height <= 0) {
            return;
        }

        float left = 65f;
        float right = width - 65f;
        float top = 75f;
        float bottom = height - 55f;

        float[] layerX = new float[architecture.length];

        for (int layer = 0; layer < architecture.length; layer++) {

            layerX[layer] = left +
                    (right - left) * layer /
                            (architecture.length - 1f);
        }

        // Draw connections.
        paint.setStrokeWidth(2f);

        for (int layer = 0; layer < architecture.length - 1; layer++) {

            for (int i = 0; i < architecture[layer]; i++) {

                for (int j = 0; j < architecture[layer + 1]; j++) {

                    float y1 = nodeY(i, architecture[layer], top, bottom);
                    float y2 = nodeY(j, architecture[layer + 1], top, bottom);

                    paint.setColor(0xFF9AA9BC);

                    canvas.drawLine(
                            layerX[layer],
                            y1,
                            layerX[layer + 1],
                            y2,
                            paint
                    );
                }
            }
        }

        // Draw neurons and headings.
        for (int layer = 0; layer < architecture.length; layer++) {

            paint.setColor(Color.BLACK);
            paint.setTextSize(13f);

            String heading;

            if (layer == 0) {
                heading = "INPUT";
            } else if (layer == architecture.length - 1) {
                heading = "OUTPUT";
            } else {
                heading = "HIDDEN " + layer;
            }

            canvas.drawText(
                    heading,
                    layerX[layer],
                    55f,
                    paint
            );

            for (int neuron = 0; neuron < architecture[layer]; neuron++) {

                float y = nodeY(
                        neuron,
                        architecture[layer],
                        top,
                        bottom
                );

                paint.setColor(
                        layer == 0 ? 0xFFDCEAFE :
                                layer == architecture.length - 1 ? 0xFFFFE4B8 :
                                        0xFFDDF5E4
                );

                canvas.drawCircle(
                        layerX[layer],
                        y,
                        20f,
                        paint
                );

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2f);
                paint.setColor(Color.BLACK);

                canvas.drawCircle(
                        layerX[layer],
                        y,
                        20f,
                        paint
                );

                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(12f);
                paint.setColor(Color.BLACK);

                canvas.drawText(
                        "N" + (neuron + 1),
                        layerX[layer],
                        y + 4f,
                        paint
                );
            }
        }

        paint.setTextSize(15f);
        paint.setColor(Color.BLACK);

        StringBuilder architectureText = new StringBuilder();

        for (int i = 0; i < architecture.length; i++) {
            architectureText.append(architecture[i]);

            if (i < architecture.length - 1) {
                architectureText.append(" → ");
            }
        }

        canvas.drawText(
                architectureText.toString(),
                width / 2f,
                height - 15f,
                paint
        );
    }

    private float nodeY(
            int neuron,
            int count,
            float top,
            float bottom
    ) {
        if (count <= 1) {
            return (top + bottom) / 2f;
        }

        return top + (bottom - top) * neuron / (count - 1f);
    }
}
