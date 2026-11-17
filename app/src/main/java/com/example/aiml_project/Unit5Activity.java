
package com.example.aiml_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Unit5Activity extends AppCompatActivity {

    private Spinner topicSpinner;
    private TextView topicTitle, topicCategory, topicExplanation;
    private TextView topicExample, topicTakeaway, progressText;
    private TextView rocResult, perceptronResult, ensembleResult;

    private EditText tpInput, fpInput, fnInput, tnInput;
    private EditText xInput, weightInput, biasInput, targetInput, rateInput;
    private EditText model1Input, model2Input, model3Input;

    private FrameLayout rocPlotContainer;
    private ROCPlotView rocPlotView;

    private int currentTopic = 0;
    private boolean spinnerUpdating = false;

    private final String[] topics = {
            "1. ROC Curves",
            "2. Evaluation Metrics",
            "3. Significance Tests",
            "4. Error Correction in Perceptrons",
            "5. Bagging and Boosting",
            "6. Machine Learning Process in Practice"
    };

    private final String[] explanations = {
            "A Receiver Operating Characteristic (ROC) curve evaluates a binary classifier at different decision thresholds. The y-axis is True Positive Rate (TPR), also called sensitivity. The x-axis is False Positive Rate (FPR). A curve closer to the top-left generally indicates better discrimination.",

            "Evaluation metrics measure how well a machine-learning model performs. Accuracy measures overall correctness. Precision measures how many positive predictions are correct. Recall measures how many actual positives are detected. F1-score balances precision and recall. Choose metrics according to the problem and the cost of mistakes.",

            "Significance tests help determine whether an observed performance difference is likely to reflect a real difference rather than random variation. Examples include paired t-tests and McNemar's test. The null hypothesis usually states that there is no difference. A p-value below a chosen significance level, often 0.05, is evidence against the null hypothesis, not proof that a model is practically better.",

            "The perceptron learning rule corrects a prediction by updating weights using the prediction error. For a binary target and step activation: error = target - prediction. The weight update is new weight = old weight + learning rate × error × input. The bias update is new bias = old bias + learning rate × error.",

            "Ensemble learning combines multiple models to improve prediction. Bagging trains models independently on bootstrap samples and combines their outputs; Random Forest is a bagging-based ensemble of decision trees with random feature selection. Boosting trains models sequentially so later learners focus on earlier errors. AdaBoost reweights examples, while XGBoost is a regularized, optimized gradient-boosting system.",

            "A practical machine-learning workflow starts by defining the problem and collecting data. Then explore and clean the data, split it into training, validation and test sets, build a baseline, train models, tune hyperparameters, evaluate with suitable metrics, inspect errors, and deploy and monitor the chosen model. Avoid data leakage and do not tune repeatedly on the test set."
    };

    private final String[] examples = {
            "Example: A medical classifier can use a lower threshold to detect more actual cases. This may increase TPR, but can also increase FPR. The ROC curve shows this trade-off across thresholds.",

            "Example: In fraud detection, missing a fraudulent transaction may be more costly than investigating a legitimate one. Accuracy alone may hide poor fraud detection, so precision, recall and F1-score can be more informative.",

            "Example: If two classifiers are tested on the same dataset, a suitable paired significance test can help assess whether their observed performance difference is statistically meaningful. The test's assumptions and the experimental design matter.",

            "Example: If input x = 2, weight w = 0.2, bias = 0, target = 1, and learning rate = 0.1, the initial net input is 0.4, so the step prediction is 1. The error is 0 and the weight does not change. Try a negative net input to see a correction.",

            "Example: Bagging combines independently trained trees, often by majority voting. Boosting builds a sequence of learners, where later learners focus on reducing the errors of the ensemble so far.",

            "Example: For a student-performance prediction system, first define the target, inspect missing values, split students into separate datasets, train a baseline model, compare metrics, examine mistakes and only then finalize the model."
    };

    private final String[] takeaways = {
            "Remember: ROC plots TPR against FPR across decision thresholds. AUC summarizes the ROC curve; higher AUC generally means better ranking discrimination.",

            "Remember: Accuracy = correct predictions / all predictions. Precision focuses on predicted positives; recall focuses on actual positives; F1 is their harmonic mean.",

            "Remember: Statistical significance is not the same as practical importance. Select a test that matches the data, design and assumptions.",

            "Remember: Δw = learning rate × (target − prediction) × input. Update the bias using the same error without multiplying by the input.",

            "Remember: Bagging trains in parallel and reduces variance; boosting builds sequentially and focuses on reducing errors. Random Forest is bagging; AdaBoost and XGBoost are boosting methods.",

            "Remember: Define the problem, prepare data, split correctly, train, validate, evaluate, inspect errors, deploy and monitor."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit5);

        bindViews();
        setupTopicSpinner();
        setupNavigation();
        setupCalculators();
        showTopic(0);
    }

    private void bindViews() {
        topicSpinner = findViewById(R.id.unit5TopicSpinner);
        topicTitle = findViewById(R.id.unit5TopicTitle);
        topicCategory = findViewById(R.id.unit5TopicCategory);
        topicExplanation = findViewById(R.id.unit5Explanation);
        topicExample = findViewById(R.id.unit5Example);
        topicTakeaway = findViewById(R.id.unit5Takeaway);
        progressText = findViewById(R.id.unit5ProgressText);

        rocResult = findViewById(R.id.unit5RocResult);
        perceptronResult = findViewById(R.id.unit5PerceptronResult);
        ensembleResult = findViewById(R.id.unit5EnsembleResult);

        tpInput = findViewById(R.id.unit5TpInput);
        fpInput = findViewById(R.id.unit5FpInput);
        fnInput = findViewById(R.id.unit5FnInput);
        tnInput = findViewById(R.id.unit5TnInput);

        xInput = findViewById(R.id.unit5XInput);
        weightInput = findViewById(R.id.unit5WeightInput);
        biasInput = findViewById(R.id.unit5BiasInput);
        targetInput = findViewById(R.id.unit5TargetInput);
        rateInput = findViewById(R.id.unit5RateInput);

        model1Input = findViewById(R.id.unit5Model1Input);
        model2Input = findViewById(R.id.unit5Model2Input);
        model3Input = findViewById(R.id.unit5Model3Input);

        // Connect the ROC graph added to activity_unit5.xml.
        rocPlotContainer = findViewById(R.id.unit5RocPlotContainer);
        rocPlotView = new ROCPlotView(this);

        rocPlotContainer.addView(
                rocPlotView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
    }

    private void setupTopicSpinner() {
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
                        if (!spinnerUpdating) {
                            showTopic(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    private void setupNavigation() {
        Button previous = findViewById(R.id.unit5PreviousButton);
        Button next = findViewById(R.id.unit5NextButton);

        previous.setOnClickListener(v -> {
            if (currentTopic > 0) {
                showTopic(currentTopic - 1);
            }
        });

        next.setOnClickListener(v -> {
            if (currentTopic < topics.length - 1) {
                showTopic(currentTopic + 1);
            }
        });
    }

    private void showTopic(int position) {
        currentTopic = position;

        topicTitle.setText(topics[position]);
        topicCategory.setText("UNIT 5 • EVALUATION AND ENSEMBLES");
        topicExplanation.setText(explanations[position]);
        topicExample.setText(examples[position]);
        topicTakeaway.setText(takeaways[position]);

        progressText.setText(
                "Topic " + (position + 1) + " of " + topics.length
        );

        spinnerUpdating = true;
        topicSpinner.setSelection(position);
        spinnerUpdating = false;

        findViewById(R.id.unit5PreviousButton)
                .setEnabled(position > 0);

        findViewById(R.id.unit5NextButton)
                .setEnabled(position < topics.length - 1);
    }

    private void setupCalculators() {
        findViewById(R.id.unit5RocButton)
                .setOnClickListener(v -> calculateRoc());

        findViewById(R.id.unit5PerceptronButton)
                .setOnClickListener(v -> calculatePerceptron());

        findViewById(R.id.unit5EnsembleButton)
                .setOnClickListener(v -> calculateEnsemble());

        calculateRoc();
        calculatePerceptron();
        calculateEnsemble();
    }

    private int readCount(EditText input) {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Fill in all count fields."
            );
        }

        int number = Integer.parseInt(value);

        if (number < 0) {
            throw new IllegalArgumentException(
                    "Counts cannot be negative."
            );
        }

        return number;
    }

    private double readNumber(EditText input) {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Fill in all numeric fields."
            );
        }

        double number = Double.parseDouble(value);

        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new IllegalArgumentException(
                    "Enter a valid finite number."
            );
        }

        return number;
    }

    private void calculateRoc() {
        try {
            int tp = readCount(tpInput);
            int fp = readCount(fpInput);
            int fn = readCount(fnInput);
            int tn = readCount(tnInput);

            if ((long) tp + fn == 0 || (long) fp + tn == 0) {
                rocResult.setText(
                        "Cannot calculate the ROC point.\n" +
                                "TP + FN must be greater than zero, and " +
                                "FP + TN must be greater than zero."
                );
                return;
            }

            double tpr = (double) tp / ((long) tp + fn);
            double fpr = (double) fp / ((long) fp + tn);

            // Update the plotted ROC point.
            rocPlotView.setPoint(fpr, tpr);

            rocResult.setText(String.format(
                    Locale.US,
                    "ROC point: (FPR, TPR) = (%.4f, %.4f)\n\n" +
                            "True Positive Rate (TPR) = TP / (TP + FN)\n" +
                            "= %d / (%d + %d) = %.2f%%\n\n" +
                            "False Positive Rate (FPR) = FP / (FP + TN)\n" +
                            "= %d / (%d + %d) = %.2f%%\n\n" +
                            "This is one point on the ROC curve for the " +
                            "selected classifier threshold.",
                    fpr, tpr,
                    tp, tp, fn, tpr * 100,
                    fp, fp, tn, fpr * 100
            ));

        } catch (NumberFormatException e) {
            rocResult.setText("Counts must be whole numbers.");
        } catch (IllegalArgumentException e) {
            rocResult.setText(e.getMessage());
        }
    }

    private void calculatePerceptron() {
        try {
            double x = readNumber(xInput);
            double w = readNumber(weightInput);
            double b = readNumber(biasInput);
            double target = readNumber(targetInput);
            double rate = readNumber(rateInput);

            if (target != 0.0 && target != 1.0) {
                perceptronResult.setText("Target must be 0 or 1.");
                return;
            }

            if (rate <= 0.0) {
                perceptronResult.setText(
                        "Learning rate must be greater than zero."
                );
                return;
            }

            double net = w * x + b;
            int prediction = net >= 0 ? 1 : 0;
            double error = target - prediction;

            double newWeight = w + rate * error * x;
            double newBias = b + rate * error;

            perceptronResult.setText(String.format(
                    Locale.US,
                    "1. Net input = w × x + b = %.4f\n" +
                            "2. Prediction = %d (step activation)\n" +
                            "3. Error = target − prediction = %.4f\n" +
                            "4. New weight = w + learning rate × error × x\n" +
                            "= %.4f\n" +
                            "5. New bias = b + learning rate × error\n" +
                            "= %.4f\n\n%s",
                    net,
                    prediction,
                    error,
                    newWeight,
                    newBias,
                    error == 0
                            ? "Correct prediction: no update is needed."
                            : "Incorrect prediction: the weight and bias are updated."
            ));

        } catch (NumberFormatException e) {
            perceptronResult.setText(
                    "Enter valid numeric values."
            );
        } catch (IllegalArgumentException e) {
            perceptronResult.setText(e.getMessage());
        }
    }

    private void calculateEnsemble() {
        try {
            int m1 = readBinary(model1Input);
            int m2 = readBinary(model2Input);
            int m3 = readBinary(model3Input);

            int votesForOne = m1 + m2 + m3;
            int result = votesForOne >= 2 ? 1 : 0;

            ensembleResult.setText(
                    "Model predictions: " + m1 + ", " + m2 + ", " + m3 +
                            "\n\nVotes for class 1: " + votesForOne + " out of 3" +
                            "\nVotes for class 0: " + (3 - votesForOne) + " out of 3" +
                            "\n\nMajority-vote prediction: Class " + result +
                            "\n\nEach model contributes one vote. This illustrates " +
                            "hard voting; it is not a full Random Forest or boosting implementation."
            );

        } catch (IllegalArgumentException e) {
            ensembleResult.setText(e.getMessage());
        }
    }

    private int readBinary(EditText input) {
        String value = input.getText().toString().trim();

        if (!value.equals("0") && !value.equals("1")) {
            throw new IllegalArgumentException(
                    "Each model prediction must be 0 or 1."
            );
        }

        return Integer.parseInt(value);
    }
}
