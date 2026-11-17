
package com.example.aiml_project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Unit3Activity extends AppCompatActivity {

    private Spinner topicSpinner;

    private TextView categoryText, titleText, foundationText;
    private TextView explanationText, exampleText, takeawayText, progressText;

    private LinearLayout inputRowsContainer;
    private LinearLayout visualizationContainer;

    private EditText biasEdit, thresholdEdit;

    private TextView perceptronResultText, perceptronStepsText;

    private Button previousButton, nextButton;
    private Button calculateButton, addInputButton, removeInputButton;
    private Button networkLabButton;

    private PerceptronGraphView graphView;

    private final ArrayList<EditText> inputFields = new ArrayList<>();
    private final ArrayList<EditText> weightFields = new ArrayList<>();

    private int currentTopic = 0;

    private static final int MAX_INPUTS = 100;
    private static final int MIN_INPUTS = 1;

    private final String[] topics = {
            "1. Biological Neurons",
            "2. Linear Threshold Units",
            "3. Perceptrons",
            "4. Perceptron Limitations",
            "5. Gradient Descent",
            "6. Multilayer Networks and Backpropagation",
            "7. Hidden and Distributed Representations",
            "8. Overfitting",
            "9. Learning Network Structure",
            "10. Expert System Case Study 1",
            "11. Expert System Case Study 2"
    };

    private final String[] foundations = {
            "Prerequisites: inputs, outputs, signals, and biological neurons.",
            "Prerequisites: numerical inputs, weights, addition, and thresholds.",
            "Prerequisites: weighted sums, bias, binary outputs, and classification.",
            "Prerequisites: binary inputs, Boolean logic, and decision boundaries.",
            "Prerequisites: weights, prediction error, derivatives, and loss.",
            "Prerequisites: layers, activation functions, weighted sums, and error.",
            "Prerequisites: input layers, hidden layers, and learned features.",
            "Prerequisites: training data, test data, model complexity, and error.",
            "Prerequisites: layers, neurons, connections, and architecture.",
            "Prerequisites: facts, IF-THEN rules, inference engines, and conclusions.",
            "Prerequisites: knowledge bases, rules, facts, and explainable conclusions."
    };

    private final String[] explanations = {
            "A biological neuron receives signals, processes them, and transmits a signal. Artificial neurons use numerical inputs, weights, a bias, and an activation function.",
            "A linear threshold unit calculates a weighted sum and compares it with a threshold to produce an output.",
            "A perceptron is a simple binary classifier. It combines inputs and weights, adds a bias, and applies a step activation function.",
            "A single perceptron can learn linearly separable patterns such as AND and OR, but cannot represent XOR with one linear decision boundary.",
            "Gradient descent adjusts model parameters to reduce a loss function. The learning rate controls the update size.",
            "Multilayer networks contain input, hidden, and output layers. Backpropagation calculates gradients used to update network weights.",
            "Hidden layers transform inputs into intermediate features. Distributed representations encode information across multiple neurons.",
            "Overfitting occurs when a model fits training data too closely and performs poorly on unseen data.",
            "Network structure includes the number of layers, neurons, and connections. Different architectures have different capabilities and costs.",
            "Illustrative case study: a rule-based medical information system applies supplied facts and rules to suggest what a clinician should investigate. It is not a medical diagnosis.",
            "Illustrative case study: a troubleshooting system applies IF-THEN rules to observed machine symptoms and explains its recommendations."
    };

    private final String[] examples = {
            "Inputs enter an artificial neuron. Each input is multiplied by its weight, the products are added, and an activation function produces an output.",
            "A unit calculates a weighted sum and applies a decision rule to determine its output.",
            "For x1 = 1, w1 = 2, and bias = -1, z = (1 x 2) - 1 = 1. The output is 1.",
            "XOR produces 1 when two binary inputs differ. One linear decision boundary cannot classify all four cases correctly.",
            "A weight of 0.5, gradient 0.2, and learning rate 0.1 give a new weight of 0.5 - (0.1 x 0.2) = 0.48.",
            "A network predicts during forward propagation and uses backpropagation to calculate gradients for its weights.",
            "A network may transform pixels into edges, shapes, and more complex features.",
            "Low training error and high validation error can indicate overfitting.",
            "Compare networks with different numbers of layers or neurons to explore architectural differences.",
            "An educational rule might say: IF symptom A and symptom B are present, THEN recommend further clinical investigation.",
            "An educational rule might say: IF a machine has no power, THEN check its power supply."
    };

    private final String[] takeaways = {
            "Artificial neurons are mathematical models inspired by biological neurons.",
            "An LTU combines inputs, weights, and a decision rule.",
            "Perceptron: weighted sum + bias + activation.",
            "One perceptron cannot learn XOR.",
            "Gradient descent adjusts parameters to reduce loss.",
            "Backpropagation calculates gradients for training multilayer networks.",
            "Hidden layers learn intermediate representations.",
            "Overfitting harms generalization to unseen data.",
            "Architecture affects capability and complexity.",
            "Expert systems use facts and rules to produce explainable conclusions.",
            "Rule-based troubleshooting can trace conclusions to matching rules."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit3);

        topicSpinner = findViewById(R.id.unit3TopicSpinner);
        categoryText = findViewById(R.id.unit3CategoryText);
        titleText = findViewById(R.id.unit3TitleText);
        foundationText = findViewById(R.id.unit3FoundationText);
        explanationText = findViewById(R.id.unit3ExplanationText);
        exampleText = findViewById(R.id.unit3ExampleText);
        takeawayText = findViewById(R.id.unit3TakeawayText);
        progressText = findViewById(R.id.unit3ProgressText);

        inputRowsContainer = findViewById(R.id.unit3InputRowsContainer);
        biasEdit = findViewById(R.id.unit3BiasEdit);
        thresholdEdit = findViewById(R.id.unit3ThresholdEdit);

        perceptronResultText =
                findViewById(R.id.unit3PerceptronResultText);
        perceptronStepsText =
                findViewById(R.id.unit3PerceptronStepsText);

        previousButton = findViewById(R.id.unit3PreviousButton);
        nextButton = findViewById(R.id.unit3NextButton);
        calculateButton = findViewById(R.id.unit3CalculateButton);
        addInputButton = findViewById(R.id.unit3AddInputButton);
        removeInputButton = findViewById(R.id.unit3RemoveInputButton);

        // Connect the new training lab button if it exists in the layout.
        networkLabButton = findViewById(R.id.unit3NetworkLabButton);

        if (networkLabButton != null) {
            networkLabButton.setOnClickListener(v -> {
                Intent intent = new Intent(
                        Unit3Activity.this,
                        NeuralNetworkLabActivity.class
                );
                startActivity(intent);
            });
        }

        biasEdit.setText("0");
        thresholdEdit.setText("0");

        createVisualizationContainer();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                topics
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        currentTopic = position;
                        updateTopic();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        previousButton.setOnClickListener(v -> {
            if (currentTopic > 0) {
                topicSpinner.setSelection(currentTopic - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentTopic < topics.length - 1) {
                topicSpinner.setSelection(currentTopic + 1);
            }
        });

        addInputButton.setOnClickListener(v -> addInputRow());
        removeInputButton.setOnClickListener(v -> removeInputRow());
        calculateButton.setOnClickListener(v -> calculatePerceptron());

        addInputRow();
        addInputRow();

        updateTopic();
        calculatePerceptron();
    }

    private void createVisualizationContainer() {
        visualizationContainer = new LinearLayout(this);
        visualizationContainer.setOrientation(LinearLayout.VERTICAL);
        visualizationContainer.setPadding(12, 12, 12, 12);
        visualizationContainer.setBackgroundColor(Color.rgb(242, 246, 252));

        View stepsView = perceptronStepsText;

        if (stepsView.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) stepsView.getParent();
            int position = parent.indexOfChild(stepsView);
            parent.addView(visualizationContainer, position);
        }
    }

    private void updateTopic() {
        categoryText.setText(
                currentTopic <= 8 ? "NEURAL NETWORKS" : "EXPERT SYSTEMS"
        );

        titleText.setText(topics[currentTopic]);
        foundationText.setText(foundations[currentTopic]);
        explanationText.setText(explanations[currentTopic]);
        exampleText.setText(examples[currentTopic]);
        takeawayText.setText(takeaways[currentTopic]);

        progressText.setText(
                "Topic " + (currentTopic + 1) + " of " + topics.length
        );

        previousButton.setEnabled(currentTopic > 0);
        nextButton.setEnabled(currentTopic < topics.length - 1);
    }

    private TextView createRowLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(Color.rgb(38, 57, 87));
        label.setTextSize(14);
        label.setPadding(0, 8, 0, 2);
        return label;
    }

    private EditText createNumberField(String hint, String value) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value);
        field.setSingleLine(true);
        field.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
        );
        field.setTextColor(Color.rgb(23, 43, 77));
        field.setPadding(12, 8, 12, 8);
        field.setMinHeight(48);
        return field;
    }

    private void addInputRow() {
        if (inputFields.size() >= MAX_INPUTS) {
            Toast.makeText(
                    this,
                    "Maximum of " + MAX_INPUTS + " inputs reached.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int index = inputFields.size() + 1;

        inputRowsContainer.addView(createRowLabel("Input x" + index));

        EditText input = createNumberField("Enter x" + index, "0");
        inputRowsContainer.addView(input);
        inputFields.add(input);

        inputRowsContainer.addView(createRowLabel("Weight w" + index));

        EditText weight = createNumberField("Enter w" + index, "1");
        inputRowsContainer.addView(weight);
        weightFields.add(weight);

        updateInputCount();
    }

    private void removeInputRow() {
        if (inputFields.size() <= MIN_INPUTS) {
            Toast.makeText(
                    this,
                    "At least one input is required.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int last = inputFields.size() - 1;

        inputFields.remove(last);
        weightFields.remove(last);

        int childCount = inputRowsContainer.getChildCount();

        if (childCount >= 4) {
            inputRowsContainer.removeViews(childCount - 4, 4);
        }

        updateInputLabels();
        updateInputCount();
    }

    private void updateInputLabels() {
        int inputIndex = 0;

        for (int i = 0; i < inputRowsContainer.getChildCount(); i++) {
            View child = inputRowsContainer.getChildAt(i);

            if (child instanceof TextView && !(child instanceof EditText)) {
                TextView label = (TextView) child;
                String text = label.getText().toString();

                if (text.startsWith("Input x")) {
                    label.setText("Input x" + (inputIndex + 1));
                } else if (text.startsWith("Weight w")) {
                    label.setText("Weight w" + (inputIndex + 1));
                    inputIndex++;
                }
            }
        }
    }

    private void updateInputCount() {
        addInputButton.setEnabled(inputFields.size() < MAX_INPUTS);
        removeInputButton.setEnabled(inputFields.size() > MIN_INPUTS);
    }

    private double parseFiniteNumber(EditText field, String fieldName) {
        String value = field.getText().toString().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }

        final double number;

        try {
            number = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number."
            );
        }

        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new IllegalArgumentException(
                    fieldName + " must be finite."
            );
        }

        return number;
    }

    private double checkedResult(double value, String operation) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    "Calculation overflow during " + operation
                            + ". Try smaller values."
            );
        }

        return value;
    }

    private TextView createVisualText(
            String text,
            int size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(6, 8, 6, 8);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private int dpToPx(float dp) {
        return (int) (
                dp * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    private void showVisualization(
            ArrayList<Double> inputs,
            ArrayList<Double> weights,
            ArrayList<Double> products,
            double sum,
            double bias,
            double z,
            double threshold,
            int output
    ) {
        visualizationContainer.removeAllViews();
        graphView = null;

        visualizationContainer.addView(createVisualText(
                "PERCEPTRON VISUALISATION",
                16, Color.rgb(38, 57, 87), true
        ));

        visualizationContainer.addView(createVisualText(
                "Each input is multiplied by its corresponding weight.",
                13, Color.DKGRAY, false
        ));

        for (int i = 0; i < inputs.size(); i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(4, 2, 4, 2);

            row.addView(createVisualText(
                    format(inputs.get(i)), 14,
                    Color.rgb(38, 99, 180), true
            ));

            row.addView(createVisualText(
                    " x ", 14, Color.DKGRAY, false
            ));

            row.addView(createVisualText(
                    format(weights.get(i)), 14,
                    Color.rgb(38, 99, 180), true
            ));

            row.addView(createVisualText(
                    " = ", 14, Color.DKGRAY, false
            ));

            row.addView(createVisualText(
                    format(products.get(i)), 14,
                    Color.rgb(38, 115, 77), true
            ));

            visualizationContainer.addView(row);
        }

        visualizationContainer.addView(createVisualText(
                "Weighted sum = " + format(sum),
                15, Color.rgb(38, 57, 87), true
        ));

        visualizationContainer.addView(createVisualText(
                "z = weighted sum + bias",
                14, Color.DKGRAY, false
        ));

        visualizationContainer.addView(createVisualText(
                "z = " + format(sum) + " + " + format(bias)
                        + " = " + format(z),
                15, Color.rgb(38, 57, 87), true
        ));

        visualizationContainer.addView(createVisualText(
                "Threshold = " + format(threshold),
                14, Color.rgb(38, 57, 87), true
        ));

        visualizationContainer.addView(createVisualText(
                "Decision: " + format(z) + " >= "
                        + format(threshold) + " ?",
                14, Color.DKGRAY, false
        ));

        TextView result = createVisualText(
                "OUTPUT: " + output
                        + (z >= threshold
                        ? "\nWeighted value meets threshold."
                        : "\nWeighted value is below threshold."),
                16, Color.WHITE, true
        );

        result.setGravity(Gravity.CENTER);
        result.setPadding(12, 14, 12, 14);
        result.setBackgroundColor(
                output == 1
                        ? Color.rgb(38, 115, 77)
                        : Color.rgb(181, 71, 71)
        );

        visualizationContainer.addView(result);

        if (inputs.size() >= 2) {
            visualizationContainer.addView(createVisualText(
                    "TWO-INPUT DECISION-BOUNDARY GRAPH",
                    16, Color.rgb(38, 57, 87), true
            ));

            visualizationContainer.addView(createVisualText(
                    "This graph uses x1, x2, w1 and w2. "
                            + "If more inputs exist, they still affect "
                            + "the calculator's output but are not plotted.",
                    12, Color.DKGRAY, false
            ));

            visualizationContainer.addView(createVisualText(
                    "Blue line: decision boundary. "
                            + "Green region: output 1. "
                            + "Red region: output 0. "
                            + "Black point: current input pair.",
                    12, Color.DKGRAY, false
            ));

            graphView = new PerceptronGraphView(this);

            LinearLayout.LayoutParams graphParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(320)
                    );

            graphParams.setMargins(0, dpToPx(8), 0, dpToPx(8));

            visualizationContainer.addView(graphView, graphParams);

            graphView.setPerceptronValues(
                    inputs.get(0),
                    inputs.get(1),
                    weights.get(0),
                    weights.get(1),
                    bias,
                    threshold
            );
        } else {
            visualizationContainer.addView(createVisualText(
                    "The decision-boundary graph requires at least "
                            + "two input-weight pairs. Add a second input "
                            + "to display it.",
                    13, Color.DKGRAY, false
            ));
        }
    }

    private void calculatePerceptron() {
        if (inputFields.isEmpty()) {
            perceptronResultText.setText("No inputs available.");
            perceptronStepsText.setText(
                    "Add at least one input before calculating."
            );
            visualizationContainer.removeAllViews();
            return;
        }

        try {
            double bias = parseFiniteNumber(biasEdit, "Bias");
            double threshold = parseFiniteNumber(
                    thresholdEdit, "Threshold"
            );

            double sum = 0.0;

            ArrayList<Double> inputs = new ArrayList<>();
            ArrayList<Double> weights = new ArrayList<>();
            ArrayList<Double> products = new ArrayList<>();

            StringBuilder steps = new StringBuilder();

            steps.append("STEP 1 — INPUTS AND WEIGHTS\n");
            steps.append("Number of inputs: ")
                    .append(inputFields.size())
                    .append("\n\n");

            for (int i = 0; i < inputFields.size(); i++) {
                double x = parseFiniteNumber(
                        inputFields.get(i), "Input x" + (i + 1)
                );

                double w = parseFiniteNumber(
                        weightFields.get(i), "Weight w" + (i + 1)
                );

                double product = checkedResult(
                        x * w, "multiplication for input " + (i + 1)
                );

                sum = checkedResult(
                        sum + product, "weighted-sum addition"
                );

                inputs.add(x);
                weights.add(w);
                products.add(product);

                steps.append("x").append(i + 1)
                        .append(" x w").append(i + 1)
                        .append(" = ")
                        .append(format(x)).append(" x ")
                        .append(format(w)).append(" = ")
                        .append(format(product)).append("\n");
            }

            double z = checkedResult(sum + bias, "bias addition");
            int output = z >= threshold ? 1 : 0;

            steps.append("\nSTEP 2 — WEIGHTED SUM\n");
            steps.append("Σ(xi x wi) = ")
                    .append(format(sum)).append("\n\n");

            steps.append("STEP 3 — ADD BIAS\n");
            steps.append("z = weighted sum + bias\n");
            steps.append("z = ").append(format(sum))
                    .append(" + ").append(format(bias))
                    .append(" = ").append(format(z)).append("\n\n");

            steps.append("STEP 4 — APPLY THRESHOLD\n");
            steps.append("Threshold = ")
                    .append(format(threshold)).append("\n");
            steps.append("If z >= threshold, output = 1; ")
                    .append("otherwise output = 0.\n");
            steps.append(format(z)).append(" >= ")
                    .append(format(threshold))
                    .append(" is ")
                    .append(z >= threshold)
                    .append(".\n");
            steps.append("Output = ").append(output).append("\n\n");

            steps.append("FINAL RESULT\n");
            steps.append("Weighted sum = ").append(format(sum))
                    .append("\nBias = ").append(format(bias))
                    .append("\nNet value z = ").append(format(z))
                    .append("\nThreshold = ").append(format(threshold))
                    .append("\nOutput = ").append(output);

            perceptronResultText.setText(
                    "Output: " + output
                            + "  |  z = " + format(z)
                            + "  |  Threshold = " + format(threshold)
            );

            perceptronStepsText.setText(steps.toString());

            showVisualization(
                    inputs, weights, products, sum, bias,
                    z, threshold, output
            );

        } catch (IllegalArgumentException e) {
            perceptronResultText.setText("Input error");
            perceptronStepsText.setText(e.getMessage());

            visualizationContainer.removeAllViews();
            graphView = null;

            visualizationContainer.addView(createVisualText(
                    "Please correct the input values and calculate again.",
                    14, Color.rgb(181, 71, 71), true
            ));
        }
    }

    private String format(double value) {
        return String.format(Locale.US, "%.6f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}
