
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class NeuralNetworkTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView result = new TextView(this);
        result.setPadding(30, 40, 30, 40);
        result.setTextSize(16);

        setContentView(result);

        // XOR dataset
        float[][] inputs = {
                {0f, 0f},
                {0f, 1f},
                {1f, 0f},
                {1f, 1f}
        };

        float[][] targets = {
                {0f},
                {1f},
                {1f},
                {0f}
        };

        // Architecture: 2 inputs -> 4 hidden -> 3 hidden -> 1 output
        NeuralNetworkEngine network = new NeuralNetworkEngine(
                new int[]{2, 4, 3, 1},
                NeuralNetworkEngine.Activation.SIGMOID
        );

        float initialLoss =
                network.calculateLoss(inputs, targets);

        float finalLoss = initialLoss;

        // Train for 10,000 epochs
        for (int epoch = 0; epoch < 10000; epoch++) {
            finalLoss = network.trainEpoch(
                    inputs,
                    targets,
                    0.5f
            );
        }

        StringBuilder report = new StringBuilder();

        report.append("NEURAL NETWORK ENGINE TEST\n\n");

        report.append("Architecture: ")
                .append(network.getArchitectureDescription())
                .append("\n\n");

        report.append("Activation: SIGMOID\n\n");

        report.append(String.format(
                Locale.US,
                "Initial Loss: %.6f\n",
                initialLoss
        ));

        report.append(String.format(
                Locale.US,
                "Final Loss: %.6f\n\n",
                network.calculateLoss(inputs, targets)
        ));

        report.append("PREDICTIONS\n\n");

        for (int i = 0; i < inputs.length; i++) {

            float prediction = network.forward(inputs[i])[0];

            report.append(String.format(
                    Locale.US,
                    "(%.0f, %.0f) -> %.4f | Target: %.0f\n",
                    inputs[i][0],
                    inputs[i][1],
                    prediction,
                    targets[i][0]
            ));
        }

        report.append("\nTest completed.");

        result.setText(report.toString());
    }
}
