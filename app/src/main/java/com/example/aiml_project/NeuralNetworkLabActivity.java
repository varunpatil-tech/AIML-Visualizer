
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class NeuralNetworkLabActivity extends AppCompatActivity {

    private DynamicNetworkDiagramView diagram;
    private Spinner inputSpinner;

    private TextView predictionText;
    private TextView errorText;
    private TextView rateText;
    private TextView explanationText;

    // Network architecture: 2 inputs -> 4 -> 3 -> 1 output
    private final int[] architecture = {2, 4, 3, 1};

    private NeuralNetworkEngine network;

    // XOR training dataset
    private final float[][] inputs = {
            {0f, 0f},
            {0f, 1f},
            {1f, 0f},
            {1f, 1f}
    };

    private final float[][] targets = {
            {0f},
            {1f},
            {1f},
            {0f}
    };

    private float learningRate = 0.30f;
    private int epochs = 0;
    private float loss = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_neural_network_lab);

        diagram = findViewById(R.id.networkDiagramView);
        inputSpinner = findViewById(R.id.xorInputSpinner);

        predictionText = findViewById(R.id.networkPredictionText);
        errorText = findViewById(R.id.networkErrorText);
        rateText = findViewById(R.id.networkLearningRateText);
        explanationText = findViewById(R.id.networkExplanationText);

        Button trainButton = findViewById(R.id.networkTrainButton);
        Button resetButton = findViewById(R.id.networkResetButton);

        SeekBar rateBar = findViewById(
                R.id.networkLearningRateSeekBar
        );

        setupInputSpinner();
        setupLearningRate(rateBar);

        trainButton.setOnClickListener(v -> {
            trainOneEpoch();
            updateDisplay();
        });

        resetButton.setOnClickListener(v -> {
            initialiseNetwork();
            updateDisplay();

            explanationText.setText(
                    "Network reset successfully. The weights and biases " +
                            "have been reinitialised."
            );
        });

        initialiseNetwork();
        updateDisplay();
    }

    private void setupInputSpinner() {

        String[] examples = {
                "Input (0, 0) → Target 0",
                "Input (0, 1) → Target 1",
                "Input (1, 0) → Target 1",
                "Input (1, 1) → Target 0"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                examples
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        inputSpinner.setAdapter(adapter);

        inputSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {
                        updateDisplay();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    private void setupLearningRate(SeekBar rateBar) {

        rateBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        learningRate = 0.01f + progress * 0.01f;

                        rateText.setText(String.format(
                                Locale.US,
                                "Learning rate: %.2f",
                                learningRate
                        ));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
    }

    private void initialiseNetwork() {

        network = new NeuralNetworkEngine(
                architecture,
                NeuralNetworkEngine.Activation.SIGMOID
        );

        epochs = 0;

        loss = network.calculateLoss(inputs, targets);
    }

    private void trainOneEpoch() {

        // One epoch processes all four XOR training examples.
        network.trainEpoch(
                inputs,
                targets,
                learningRate
        );

        epochs++;

        // Calculate the loss after the parameter updates.
        loss = network.calculateLoss(inputs, targets);

        explanationText.setText(
                "Epoch " + epochs +
                        " completed. The network processed all four XOR examples. " +
                        "Forward propagation calculated the predictions, while " +
                        "backpropagation calculated gradients and updated the " +
                        "weights and biases across all network layers."
        );
    }

    private void updateDisplay() {

        if (network == null) {
            return;
        }

        int selected = inputSpinner.getSelectedItemPosition();

        if (selected < 0 || selected >= inputs.length) {
            selected = 0;
        }

        // Run forward propagation for the selected example.
        float[] output = network.forward(inputs[selected]);

        // Obtain intermediate activations from every layer.
        float[][] activations = network.getActivations();

        // Obtain all network weights.
        float[][][] weights = network.getWeights();

        // Update the dynamic network diagram.
        diagram.updateNetwork(
                network.getArchitecture(),
                weights,
                activations
        );

        float prediction = output[0];
        float target = targets[selected][0];

        predictionText.setText(String.format(
                Locale.US,
                "Prediction: %.4f   |   Target: %.0f",
                prediction,
                target
        ));

        errorText.setText(String.format(
                Locale.US,
                "Epochs: %d   |   Mean squared error: %.6f",
                epochs,
                loss
        ));
    }
}
