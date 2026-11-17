
package com.example.aiml_project;

import java.util.Locale;
import java.util.Random;

public class NeuralNetworkEngine {

    public enum Activation {
        SIGMOID,
        RELU,
        TANH
    }

    private final int[] architecture;
    private final Activation activation;
    private final Random random;

    // weights[layer][source neuron][destination neuron]
    private final float[][][] weights;

    // biases[layer][destination neuron]
    private final float[][] biases;

    // Values calculated during forward propagation
    private float[][] activations;
    private float[][] zValues;

    public NeuralNetworkEngine(
            int[] architecture,
            Activation activation
    ) {
        this(architecture, activation, 42L);
    }

    public NeuralNetworkEngine(
            int[] architecture,
            Activation activation,
            long seed
    ) {
        if (architecture == null || architecture.length < 2) {
            throw new IllegalArgumentException(
                    "Network must have at least an input and output layer."
            );
        }

        for (int size : architecture) {
            if (size < 1 || size > 64) {
                throw new IllegalArgumentException(
                        "Each layer must contain between 1 and 64 neurons."
                );
            }
        }

        if (activation == null) {
            throw new IllegalArgumentException(
                    "Activation function cannot be null."
            );
        }

        this.architecture = architecture.clone();
        this.activation = activation;
        this.random = new Random(seed);

        int layerCount = architecture.length - 1;

        weights = new float[layerCount][][];
        biases = new float[layerCount][];

        initialiseParameters();
    }

    private void initialiseParameters() {
        for (int layer = 0; layer < weights.length; layer++) {

            int sourceCount = architecture[layer];
            int destinationCount = architecture[layer + 1];

            weights[layer] =
                    new float[sourceCount][destinationCount];

            biases[layer] =
                    new float[destinationCount];

            // Xavier-style initialization
            float limit = (float) Math.sqrt(
                    6.0 / (sourceCount + destinationCount)
            );

            for (int i = 0; i < sourceCount; i++) {
                for (int j = 0; j < destinationCount; j++) {
                    weights[layer][i][j] =
                            (random.nextFloat() * 2f - 1f) * limit;
                }
            }

            for (int j = 0; j < destinationCount; j++) {
                biases[layer][j] = 0f;
            }
        }
    }

    // ---------------- FORWARD PROPAGATION ----------------

    public float[] forward(float[] input) {

        if (input == null || input.length != architecture[0]) {
            throw new IllegalArgumentException(
                    "Input size must match the input layer."
            );
        }

        for (float value : input) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Input values must be finite."
                );
            }
        }

        int layerCount = architecture.length;

        activations = new float[layerCount][];
        zValues = new float[layerCount][];

        activations[0] = input.clone();

        for (int layer = 0; layer < weights.length; layer++) {

            int destinationCount = architecture[layer + 1];

            activations[layer + 1] =
                    new float[destinationCount];

            zValues[layer + 1] =
                    new float[destinationCount];

            for (int j = 0; j < destinationCount; j++) {

                float z = biases[layer][j];

                for (int i = 0; i < architecture[layer]; i++) {
                    z += activations[layer][i]
                            * weights[layer][i][j];
                }

                zValues[layer + 1][j] = z;

                activations[layer + 1][j] =
                        activate(z);
            }
        }

        return activations[layerCount - 1].clone();
    }

    private float activate(float value) {

        switch (activation) {

            case SIGMOID:
                value = Math.max(-30f, Math.min(30f, value));
                return 1f / (1f + (float) Math.exp(-value));

            case RELU:
                return Math.max(0f, value);

            case TANH:
                return (float) Math.tanh(value);

            default:
                throw new IllegalStateException(
                        "Unsupported activation function."
                );
        }
    }

    private float activationDerivative(float z, float a) {

        switch (activation) {

            case SIGMOID:
                return a * (1f - a);

            case RELU:
                return z > 0f ? 1f : 0f;

            case TANH:
                return 1f - a * a;

            default:
                throw new IllegalStateException(
                        "Unsupported activation function."
                );
        }
    }

    // ---------------- BACKPROPAGATION ----------------

    public float trainSample(
            float[] input,
            float[] target,
            float learningRate
    ) {

        if (target == null ||
                target.length != architecture[architecture.length - 1]) {
            throw new IllegalArgumentException(
                    "Target size must match the output layer."
            );
        }

        if (!Float.isFinite(learningRate) ||
                learningRate <= 0f ||
                learningRate > 1f) {
            throw new IllegalArgumentException(
                    "Learning rate must be greater than 0 and at most 1."
            );
        }

        for (float value : target) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Target values must be finite."
                );
            }
        }

        // Step 1: Forward propagation
        float[] output = forward(input);

        int layerCount = architecture.length;

        float[][] deltas = new float[layerCount][];

        // Step 2: Calculate output-layer error
        int outputLayer = layerCount - 1;

        deltas[outputLayer] =
                new float[architecture[outputLayer]];

        float sampleLoss = 0f;

        for (int j = 0; j < architecture[outputLayer]; j++) {

            float error = output[j] - target[j];

            sampleLoss += 0.5f * error * error;

            deltas[outputLayer][j] =
                    error * activationDerivative(
                            zValues[outputLayer][j],
                            activations[outputLayer][j]
                    );
        }

        // Step 3: Backpropagate through hidden layers
        for (int layer = outputLayer - 1; layer >= 1; layer--) {

            deltas[layer] =
                    new float[architecture[layer]];

            for (int i = 0; i < architecture[layer]; i++) {

                float error = 0f;

                for (int j = 0;
                     j < architecture[layer + 1]; j++) {

                    error += weights[layer][i][j]
                            * deltas[layer + 1][j];
                }

                deltas[layer][i] =
                        error * activationDerivative(
                                zValues[layer][i],
                                activations[layer][i]
                        );
            }
        }

        // Step 4: Update weights and biases
        for (int layer = 0; layer < weights.length; layer++) {

            for (int i = 0; i < architecture[layer]; i++) {

                for (int j = 0;
                     j < architecture[layer + 1]; j++) {

                    weights[layer][i][j] -=
                            learningRate
                                    * activations[layer][i]
                                    * deltas[layer + 1][j];
                }
            }

            for (int j = 0;
                 j < architecture[layer + 1]; j++) {

                biases[layer][j] -=
                        learningRate * deltas[layer + 1][j];
            }
        }

        return sampleLoss;
    }

    // ---------------- TRAINING ----------------

    public float trainEpoch(
            float[][] inputs,
            float[][] targets,
            float learningRate
    ) {

        if (inputs == null || targets == null ||
                inputs.length == 0 ||
                inputs.length != targets.length) {
            throw new IllegalArgumentException(
                    "Training inputs and targets must have equal non-zero lengths."
            );
        }

        float totalLoss = 0f;

        for (int i = 0; i < inputs.length; i++) {
            totalLoss += trainSample(
                    inputs[i],
                    targets[i],
                    learningRate
            );
        }

        return totalLoss / inputs.length;
    }

    public float calculateLoss(
            float[][] inputs,
            float[][] targets
    ) {

        if (inputs == null || targets == null ||
                inputs.length == 0 ||
                inputs.length != targets.length) {
            throw new IllegalArgumentException(
                    "Invalid training dataset."
            );
        }

        float totalLoss = 0f;

        for (int i = 0; i < inputs.length; i++) {

            float[] output = forward(inputs[i]);

            if (targets[i] == null ||
                    targets[i].length != output.length) {
                throw new IllegalArgumentException(
                        "Target size does not match output size."
                );
            }

            for (int j = 0; j < output.length; j++) {
                float error = output[j] - targets[i][j];
                totalLoss += 0.5f * error * error;
            }
        }

        return totalLoss / inputs.length;
    }

    // ---------------- NETWORK INFORMATION ----------------

    public int[] getArchitecture() {
        return architecture.clone();
    }

    public Activation getActivation() {
        return activation;
    }

    public float[][] getLayerActivations() {

        if (activations == null) {
            return new float[0][];
        }

        float[][] copy = new float[activations.length][];

        for (int i = 0; i < activations.length; i++) {
            copy[i] = activations[i].clone();
        }

        return copy;
    }

    public float[][][] getWeights() {

        float[][][] copy = new float[weights.length][][];

        for (int layer = 0; layer < weights.length; layer++) {
            copy[layer] = new float[weights[layer].length][];

            for (int i = 0; i < weights[layer].length; i++) {
                copy[layer][i] = weights[layer][i].clone();
            }
        }

        return copy;
    }

    public float[][] getBiases() {

        float[][] copy = new float[biases.length][];

        for (int layer = 0; layer < biases.length; layer++) {
            copy[layer] = biases[layer].clone();
        }

        return copy;
    }

    public void setWeight(
            int layer,
            int sourceNeuron,
            int destinationNeuron,
            float value
    ) {

        checkFinite(value);

        weights[layer][sourceNeuron][destinationNeuron] = value;
    }

    public void setBias(
            int layer,
            int neuron,
            float value
    ) {

        checkFinite(value);

        biases[layer][neuron] = value;
    }

    public void reset() {
        initialiseParameters();
        activations = null;
        zValues = null;
    }

    private void checkFinite(float value) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(
                    "Weight and bias values must be finite."
            );
        }
    }

    public String getArchitectureDescription() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < architecture.length; i++) {

            if (i > 0) {
                result.append(" → ");
            }

            result.append(architecture[i]);
        }

        return result.toString();
    }

    public float[][] getActivations() {

        float[][] copy = new float[activations.length][];

        for (int i = 0; i < activations.length; i++) {
            copy[i] = activations[i].clone();
        }

        return copy;
    }
}
