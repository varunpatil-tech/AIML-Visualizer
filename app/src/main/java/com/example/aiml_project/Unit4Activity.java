
package com.example.aiml_project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class Unit4Activity extends AppCompatActivity {

    private Spinner topicSpinner;
    private TextView categoryText, titleText, foundationText;
    private TextView explanationText, exampleText, takeawayText;
    private TextView progressText, stepsText;

    private EditText knnXEdit, knnYEdit, knnKEdit;
    private EditText logisticZEdit;
    private EditText tpEdit, tnEdit, fpEdit, fnEdit;
    private EditText svmX1Edit, svmX2Edit;
    private EditText svmW1Edit, svmW2Edit, svmBiasEdit;

    private TextView knnResultText, logisticResultText;
    private TextView metricsResultText, svmResultText;

    private FrameLayout knnPlotContainer;
    private KNNPlotView knnPlotView;

    // New sigmoid graph
    private FrameLayout sigmoidPlotContainer;
    private SigmoidPlotView sigmoidPlotView;

    private Button previousButton, nextButton;

    private int currentTopic = 0;

    private static final String[] TOPICS = {
            "1. Introduction to Machine Learning",
            "2. Learning Paradigms",
            "3. PAC Learning",
            "4. Basics of Probability",
            "5. Version Spaces",
            "6. Classification of ML Problems",
            "7. Supervised, Unsupervised and Reinforcement Learning",
            "8. K-Nearest Neighbours (KNN)",
            "9. Logistic Regression",
            "10. Perceptrons and Neural Networks",
            "11. Support Vector Machines (SVMs)"
    };

    private final String[] foundations = {
            "Prerequisites: data, features, examples, patterns, and algorithms.",
            "Prerequisites: training examples, feedback, labels, and rewards.",
            "Prerequisites: hypotheses, sample size, accuracy, and confidence.",
            "Prerequisites: events, probabilities, conditional probability, and averages.",
            "Prerequisites: hypotheses, positive examples, negative examples, and consistency.",
            "Prerequisites: features, labels, regression, classification, and clustering.",
            "Prerequisites: datasets, labels, actions, rewards, and environments.",
            "Prerequisites: coordinates, distance, labelled examples, and majority voting.",
            "Prerequisites: weighted sums, the sigmoid function, and probability.",
            "Prerequisites: inputs, weights, bias, activation functions, and layers.",
            "Prerequisites: feature vectors, hyperplanes, margins, and classification."
    };

    private final String[] explanations = {
            "Machine Learning (ML) allows a computer to learn patterns from data and use them to make predictions or decisions, rather than requiring every rule to be explicitly programmed.",
            "Learning paradigms describe how a model learns: from labelled examples, from unlabelled data, or from rewards and interactions.",
            "PAC means Probably Approximately Correct. It studies whether a learning algorithm can find a sufficiently accurate hypothesis with high probability using a suitable number of examples.",
            "Probability measures uncertainty. For an event A, 0 <= P(A) <= 1. Conditional probability is P(A|B) = P(A intersection B) / P(B), when P(B) > 0.",
            "A version space is the set of hypotheses that remain consistent with all observed training examples.",
            "ML tasks can include classification, regression, clustering, dimensionality reduction, anomaly detection, and reinforcement learning.",
            "Supervised learning uses labelled examples; unsupervised learning finds structure in unlabelled data; reinforcement learning learns actions through rewards and penalties.",
            "KNN classifies a new point using the majority label among its k nearest training points, commonly measured with Euclidean distance.",
            "Logistic regression calculates z = w.x + b and applies sigmoid(z) = 1/(1 + e^(-z)) to estimate a probability for binary classification.",
            "A perceptron uses a weighted sum and a threshold. Multilayer neural networks combine multiple layers and nonlinear activation functions to learn more complex patterns.",
            "An SVM seeks a separating hyperplane with a large margin between classes. Kernel methods can represent nonlinear decision boundaries."
    };

    private final String[] examples = {
            "A spam filter learns patterns from past emails and predicts whether a new email is spam.",
            "Supervised: classify labelled emails. Unsupervised: group customers by behaviour. Reinforcement: learn actions from rewards.",
            "A PAC-style goal might require error at most 0.05 with confidence at least 0.95. These are illustrative targets, not a guarantee for every dataset.",
            "For a fair coin, P(heads) = 1/2. For a fair die, P(rolling a 6) = 1/6.",
            "If a hypothesis predicts all positive and negative training examples correctly, it remains a candidate in the version space.",
            "Predicting house prices is regression; predicting spam/not spam is classification; grouping customers is clustering.",
            "A robot receives a reward for reaching its goal and a penalty for an unsuccessful action.",
            "KNN compares the new point with stored labelled points, chooses the nearest k, and votes on their labels.",
            "For z = 0, sigmoid(0) = 0.5. A classification threshold of 0.5 gives a positive prediction when the probability is at least 0.5.",
            "With x1 = 1, x2 = 1, weights 1 and 1, and bias -1.5, the net value is 0.5.",
            "For a linear SVM, the decision score is f(x) = w.x + b. Its sign indicates the predicted side of the boundary."
    };

    private final String[] takeaways = {
            "ML learns patterns from data to generalise to new examples.",
            "The type of feedback determines the learning paradigm.",
            "PAC learning relates accuracy, confidence, and the amount of training data.",
            "Probability quantifies uncertainty; conditional probability uses additional information.",
            "The version space contains hypotheses consistent with the observed examples.",
            "Choose the ML task according to the desired output and data.",
            "Supervised uses labels, unsupervised discovers structure, and reinforcement uses rewards.",
            "KNN is a distance-based, instance-based learning algorithm.",
            "Logistic regression converts a linear score into a probability.",
            "A single perceptron has a linear decision boundary; multilayer networks can learn nonlinear patterns.",
            "SVMs use margins and can use kernels for nonlinear classification."
    };

    private final double[][] trainingPoints = {
            {1, 1}, {2, 2}, {2, 4}, {3, 2},
            {7, 7}, {8, 8}, {8, 6}, {9, 8}
    };

    private final int[] trainingLabels = {
            0, 0, 0, 0, 1, 1, 1, 1
    };

    private double lastKnnX = 4;
    private double lastKnnY = 4;

    private int lastK = 3;
    private int lastPredictedClass = 0;
    private int[] lastNearestIndices = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit4);

        bindViews();
        setupTopicSpinner();
        setupNavigation();
        setupAlgorithmButtons();

        runKNN();
        runLogisticRegression();
        runClassificationMetrics();
        runSVM();
        updateTopic();
    }

    private void bindViews() {
        topicSpinner = findViewById(R.id.unit4TopicSpinner);
        categoryText = findViewById(R.id.unit4CategoryText);
        titleText = findViewById(R.id.unit4TitleText);
        foundationText = findViewById(R.id.unit4FoundationText);
        explanationText = findViewById(R.id.unit4ExplanationText);
        exampleText = findViewById(R.id.unit4ExampleText);
        takeawayText = findViewById(R.id.unit4TakeawayText);
        progressText = findViewById(R.id.unit4ProgressText);
        stepsText = findViewById(R.id.unit4StepsText);

        previousButton = findViewById(R.id.unit4PreviousButton);
        nextButton = findViewById(R.id.unit4NextButton);

        knnXEdit = findViewById(R.id.unit4KnnXEdit);
        knnYEdit = findViewById(R.id.unit4KnnYEdit);
        knnKEdit = findViewById(R.id.unit4KnnKEdit);
        knnResultText = findViewById(R.id.unit4KnnResultText);
        knnPlotContainer = findViewById(R.id.unit4KnnPlotContainer);

        logisticZEdit = findViewById(R.id.unit4LogisticZEdit);
        logisticResultText = findViewById(R.id.unit4LogisticResultText);

        // Initialize sigmoid visualization.
        sigmoidPlotContainer =
                findViewById(R.id.unit4SigmoidPlotContainer);

        sigmoidPlotView = new SigmoidPlotView(this);

        sigmoidPlotContainer.addView(
                sigmoidPlotView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        tpEdit = findViewById(R.id.unit4TpEdit);
        tnEdit = findViewById(R.id.unit4TnEdit);
        fpEdit = findViewById(R.id.unit4FpEdit);
        fnEdit = findViewById(R.id.unit4FnEdit);
        metricsResultText = findViewById(R.id.unit4MetricsResultText);

        svmX1Edit = findViewById(R.id.unit4SvmX1Edit);
        svmX2Edit = findViewById(R.id.unit4SvmX2Edit);
        svmW1Edit = findViewById(R.id.unit4SvmW1Edit);
        svmW2Edit = findViewById(R.id.unit4SvmW2Edit);
        svmBiasEdit = findViewById(R.id.unit4SvmBiasEdit);
        svmResultText = findViewById(R.id.unit4SvmResultText);

        knnPlotView = new KNNPlotView();

        knnPlotContainer.addView(
                knnPlotView,
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
                TOPICS
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
    }

    private void setupNavigation() {
        previousButton.setOnClickListener(v -> {
            if (currentTopic > 0) {
                topicSpinner.setSelection(currentTopic - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentTopic < TOPICS.length - 1) {
                topicSpinner.setSelection(currentTopic + 1);
            }
        });
    }

    private void setupAlgorithmButtons() {
        findViewById(R.id.unit4KnnButton).setOnClickListener(v -> runKNN());

        findViewById(R.id.unit4LogisticButton)
                .setOnClickListener(v -> runLogisticRegression());

        findViewById(R.id.unit4MetricsButton)
                .setOnClickListener(v -> runClassificationMetrics());

        findViewById(R.id.unit4SvmButton).setOnClickListener(v -> runSVM());
    }

    private void updateTopic() {
        categoryText.setText(
                currentTopic >= 7
                        ? "MACHINE LEARNING ALGORITHMS"
                        : "MACHINE LEARNING FOUNDATIONS"
        );

        titleText.setText(TOPICS[currentTopic]);
        foundationText.setText(foundations[currentTopic]);
        explanationText.setText(explanations[currentTopic]);
        exampleText.setText(examples[currentTopic]);
        takeawayText.setText(takeaways[currentTopic]);

        progressText.setText(
                "Topic " + (currentTopic + 1) + " of " + TOPICS.length
        );

        previousButton.setEnabled(currentTopic > 0);
        nextButton.setEnabled(currentTopic < TOPICS.length - 1);
    }

    private double readNumber(EditText field, String name) {
        String text = field.getText().toString().trim();

        if (text.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty.");
        }

        final double value;

        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a number.");
        }

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite.");
        }

        return value;
    }

    private void setError(TextView result, Exception e) {
        result.setText("Input error: " + e.getMessage());
        stepsText.setText(
                "Correct the input values and run the algorithm again."
        );
    }

    private String fmt(double value) {
        if (Math.abs(value) < 0.0000005) {
            value = 0;
        }

        return String.format(Locale.US, "%.4f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    private void runKNN() {
        try {
            double x = readNumber(knnXEdit, "KNN X");
            double y = readNumber(knnYEdit, "KNN Y");
            double kValue = readNumber(knnKEdit, "K");

            if (x < 0 || x > 10 || y < 0 || y > 10) {
                throw new IllegalArgumentException(
                        "KNN coordinates must be between 0 and 10."
                );
            }

            if (kValue != Math.floor(kValue)
                    || kValue < 1
                    || kValue > trainingPoints.length) {
                throw new IllegalArgumentException(
                        "K must be an integer from 1 to "
                                + trainingPoints.length + "."
                );
            }

            int k = (int) kValue;
            double[][] distances = new double[trainingPoints.length][2];

            for (int i = 0; i < trainingPoints.length; i++) {
                double dx = x - trainingPoints[i][0];
                double dy = y - trainingPoints[i][1];

                distances[i][0] = Math.sqrt(dx * dx + dy * dy);
                distances[i][1] = i;
            }

            Arrays.sort(
                    distances,
                    Comparator.comparingDouble(a -> a[0])
            );

            int votes0 = 0;
            int votes1 = 0;

            StringBuilder detail = new StringBuilder();
            detail.append("STEP 1 — Calculate Euclidean distances\n");

            for (int i = 0; i < k; i++) {
                int index = (int) distances[i][1];
                int label = trainingLabels[index];

                if (label == 0) {
                    votes0++;
                } else {
                    votes1++;
                }

                detail.append(i + 1)
                        .append(". Point (")
                        .append(fmt(trainingPoints[index][0]))
                        .append(", ")
                        .append(fmt(trainingPoints[index][1]))
                        .append("), distance = ")
                        .append(fmt(distances[i][0]))
                        .append(", class = ")
                        .append(label)
                        .append("\n");
            }

            int predictedClass = votes1 > votes0 ? 1 : 0;

            detail.append("\nSTEP 2 — Count neighbour votes\n")
                    .append("Class 0 votes = ").append(votes0).append("\n")
                    .append("Class 1 votes = ").append(votes1).append("\n\n")
                    .append("STEP 3 — Prediction\n")
                    .append("Predicted class = ").append(predictedClass);

            knnResultText.setText(
                    "Prediction: Class " + predictedClass
                            + "\nK = " + k
                            + " | Class 0 votes = " + votes0
                            + " | Class 1 votes = " + votes1
            );

            stepsText.setText(detail.toString());

            lastKnnX = x;
            lastKnnY = y;
            lastK = k;
            lastPredictedClass = predictedClass;

            lastNearestIndices = new int[k];

            for (int i = 0; i < k; i++) {
                lastNearestIndices[i] = (int) distances[i][1];
            }

            knnPlotView.invalidate();

        } catch (IllegalArgumentException e) {
            setError(knnResultText, e);
        }
    }

    private void runLogisticRegression() {
        try {
            double z = readNumber(logisticZEdit, "Model score z");

            // Avoid exponential overflow for very large inputs.
            double safeZ = Math.max(-700, Math.min(700, z));
            double probability = 1.0 / (1.0 + Math.exp(-safeZ));
            int prediction = probability >= 0.5 ? 1 : 0;

            // Update the sigmoid graph with the latest input.
            sigmoidPlotView.setPoint(z, probability);

            logisticResultText.setText(
                    "z = " + fmt(z)
                            + "\nσ(z) = 1 / (1 + e^(-z))"
                            + "\nProbability = " + fmt(probability)
                            + "\nPercentage = " + fmt(probability * 100) + "%"
                            + "\nPrediction at threshold 0.5 = " + prediction
            );

            stepsText.setText(
                    "STEP 1 — Model score\n"
                            + "z = " + fmt(z) + "\n\n"
                            + "STEP 2 — Apply sigmoid\n"
                            + "σ(z) = 1 / (1 + e^(-z))\n"
                            + "σ(z) = " + fmt(probability) + "\n\n"
                            + "STEP 3 — Classify\n"
                            + "If probability >= 0.5, predict class 1; "
                            + "otherwise predict class 0.\n"
                            + "Prediction = " + prediction
            );

        } catch (IllegalArgumentException e) {
            setError(logisticResultText, e);
        }
    }

    private void runClassificationMetrics() {
        try {
            String tpText = tpEdit.getText().toString().trim();
            String tnText = tnEdit.getText().toString().trim();
            String fpText = fpEdit.getText().toString().trim();
            String fnText = fnEdit.getText().toString().trim();

            if (tpText.isEmpty() || tnText.isEmpty()
                    || fpText.isEmpty() || fnText.isEmpty()) {
                throw new IllegalArgumentException(
                        "Enter TP, TN, FP and FN."
                );
            }

            long tp, tn, fp, fn;

            try {
                tp = Long.parseLong(tpText);
                tn = Long.parseLong(tnText);
                fp = Long.parseLong(fpText);
                fn = Long.parseLong(fnText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Enter non-negative whole numbers within the supported range."
                );
            }

            if (tp < 0 || tn < 0 || fp < 0 || fn < 0) {
                throw new IllegalArgumentException(
                        "Counts cannot be negative."
                );
            }

            long total;

            try {
                total = Math.addExact(
                        Math.addExact(tp, tn),
                        Math.addExact(fp, fn)
                );
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(
                        "The counts are too large to process."
                );
            }

            if (total == 0) {
                throw new IllegalArgumentException(
                        "The total count must be greater than zero."
                );
            }

            double accuracy = ((double) tp + tn) / total * 100.0;
            double precisionDenominator = (double) tp + fp;
            double recallDenominator = (double) tp + fn;
            double f1Denominator = 2.0 * tp + fp + fn;

            StringBuilder result = new StringBuilder();

            result.append("CONFUSION MATRIX\n\n")
                    .append("True Positive (TP): ").append(tp).append("\n")
                    .append("True Negative (TN): ").append(tn).append("\n")
                    .append("False Positive (FP): ").append(fp).append("\n")
                    .append("False Negative (FN): ").append(fn).append("\n\n");

            result.append(String.format(
                    Locale.US, "Accuracy: %.2f%%\n", accuracy
            ));

            if (precisionDenominator == 0) {
                result.append(
                        "Precision: Undefined (no positive predictions)\n"
                );
            } else {
                double precision = tp / precisionDenominator * 100.0;

                result.append(String.format(
                        Locale.US, "Precision: %.2f%%\n", precision
                ));
            }

            if (recallDenominator == 0) {
                result.append(
                        "Recall: Undefined (no actual positives)\n"
                );
            } else {
                double recall = tp / recallDenominator * 100.0;

                result.append(String.format(
                        Locale.US, "Recall: %.2f%%\n", recall
                ));
            }

            if (f1Denominator == 0) {
                result.append(
                        "F1-score: Undefined (no positive cases)\n"
                );
            } else {
                double f1 = (2.0 * tp / f1Denominator) * 100.0;

                result.append(String.format(
                        Locale.US, "F1-score: %.2f%%\n", f1
                ));
            }

            metricsResultText.setText(result.toString());

            stepsText.setText(
                    "HOW THE METRICS ARE CALCULATED\n\n"
                            + "1. Accuracy\n"
                            + "Accuracy = (TP + TN) / Total × 100\n\n"
                            + "2. Precision\n"
                            + "Precision = TP / (TP + FP) × 100\n"
                            + "Of all positive predictions, how many were correct?\n\n"
                            + "3. Recall\n"
                            + "Recall = TP / (TP + FN) × 100\n"
                            + "Of all actual positive cases, how many were found?\n\n"
                            + "4. F1-score\n"
                            + "F1 = 2TP / (2TP + FP + FN) × 100\n\n"
                            + "TP = True Positive\nTN = True Negative\n"
                            + "FP = False Positive\nFN = False Negative"
            );

        } catch (IllegalArgumentException e) {
            metricsResultText.setText("Input error: " + e.getMessage());
            stepsText.setText(
                    "Enter valid, non-negative whole-number counts and calculate again."
            );
        }
    }

    private void runSVM() {
        try {
            double x1 = readNumber(svmX1Edit, "SVM x1");
            double x2 = readNumber(svmX2Edit, "SVM x2");
            double w1 = readNumber(svmW1Edit, "SVM w1");
            double w2 = readNumber(svmW2Edit, "SVM w2");
            double bias = readNumber(svmBiasEdit, "SVM bias");

            double score = w1 * x1 + w2 * x2 + bias;

            if (!Double.isFinite(score)) {
                throw new IllegalArgumentException(
                        "Calculation overflow. Use smaller values."
                );
            }

            String predictedSide = score >= 0
                    ? "Positive side (class +1)"
                    : "Negative side (class -1)";

            svmResultText.setText(
                    "f(x) = w1*x1 + w2*x2 + b"
                            + "\nScore = " + fmt(score)
                            + "\nPredicted side = " + predictedSide
            );

            stepsText.setText(
                    "STEP 1 — Calculate the decision score\n"
                            + "f(x) = w1*x1 + w2*x2 + b\n"
                            + "f(x) = (" + fmt(w1) + " × " + fmt(x1)
                            + ") + (" + fmt(w2) + " × " + fmt(x2)
                            + ") + (" + fmt(bias) + ")\n"
                            + "f(x) = " + fmt(score) + "\n\n"
                            + "STEP 2 — Determine the side of the boundary\n"
                            + "Score >= 0: positive side.\n"
                            + "Score < 0: negative side.\n"
                            + "Prediction: " + predictedSide + "\n\n"
                            + "Note: This evaluates a supplied linear decision "
                            + "function. It does not train an SVM or calculate "
                            + "a maximum-margin hyperplane."
            );

        } catch (IllegalArgumentException e) {
            setError(svmResultText, e);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private class KNNPlotView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        KNNPlotView() {
            super(Unit4Activity.this);
        }

        private float px(double x, float left, float width) {
            return left + (float) (x / 10.0) * width;
        }

        private float py(double y, float top, float height) {
            return top + height - (float) (y / 10.0) * height;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float left = dp(38);
            float top = dp(18);
            float right = getWidth() - dp(16);
            float bottom = getHeight() - dp(30);

            float plotWidth = right - left;
            float plotHeight = bottom - top;

            if (plotWidth <= 0 || plotHeight <= 0) {
                return;
            }

            canvas.drawColor(Color.rgb(245, 247, 251));

            // Classification regions for the current K.
            int cells = 40;
            float cellWidth = plotWidth / cells;
            float cellHeight = plotHeight / cells;

            paint.setStyle(Paint.Style.FILL);

            int safeK = Math.max(
                    1,
                    Math.min(lastK, trainingPoints.length)
            );

            for (int row = 0; row < cells; row++) {
                for (int col = 0; col < cells; col++) {
                    double sampleX = (col + 0.5) * 10.0 / cells;
                    double sampleY = (cells - row - 0.5) * 10.0 / cells;

                    double[][] distances =
                            new double[trainingPoints.length][2];

                    for (int i = 0; i < trainingPoints.length; i++) {
                        double dx = sampleX - trainingPoints[i][0];
                        double dy = sampleY - trainingPoints[i][1];

                        distances[i][0] = Math.sqrt(dx * dx + dy * dy);
                        distances[i][1] = i;
                    }

                    Arrays.sort(
                            distances,
                            Comparator.comparingDouble(a -> a[0])
                    );

                    int votes0 = 0;
                    int votes1 = 0;

                    for (int i = 0; i < safeK; i++) {
                        int index = (int) distances[i][1];

                        if (trainingLabels[index] == 0) {
                            votes0++;
                        } else {
                            votes1++;
                        }
                    }

                    int regionClass = votes1 > votes0 ? 1 : 0;

                    paint.setColor(
                            regionClass == 0
                                    ? Color.rgb(222, 234, 251)
                                    : Color.rgb(220, 243, 228)
                    );

                    float x1 = left + col * cellWidth;
                    float y1 = top + row * cellHeight;

                    canvas.drawRect(
                            x1, y1,
                            x1 + cellWidth + 1,
                            y1 + cellHeight + 1,
                            paint
                    );
                }
            }

            // Coordinate grid.
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(Color.rgb(205, 215, 228));

            for (int i = 0; i <= 10; i += 2) {
                float gx = px(i, left, plotWidth);
                float gy = py(i, top, plotHeight);

                canvas.drawLine(gx, top, gx, bottom, paint);
                canvas.drawLine(left, gy, right, gy, paint);
            }

            // Axes and labels.
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(dp(1.5f));

            canvas.drawLine(left, bottom, right, bottom, paint);
            canvas.drawLine(left, top, left, bottom, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(dp(10));

            for (int i = 0; i <= 10; i += 2) {
                canvas.drawText(
                        String.valueOf(i),
                        px(i, left, plotWidth) - dp(4),
                        bottom + dp(14),
                        paint
                );

                canvas.drawText(
                        String.valueOf(i),
                        left - dp(22),
                        py(i, top, plotHeight) + dp(4),
                        paint
                );
            }

            canvas.drawText("X", right - dp(5), bottom + dp(26), paint);
            canvas.drawText("Y", left - dp(22), top - dp(3), paint);

            float qx = px(lastKnnX, left, plotWidth);
            float qy = py(lastKnnY, top, plotHeight);

            // Distances to nearest neighbours.
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.8f));
            paint.setColor(Color.rgb(210, 65, 65));

            for (int index : lastNearestIndices) {
                float nx = px(trainingPoints[index][0], left, plotWidth);
                float ny = py(trainingPoints[index][1], top, plotHeight);

                canvas.drawLine(qx, qy, nx, ny, paint);
            }

            // Training points and nearest-neighbour outlines.
            for (int i = 0; i < trainingPoints.length; i++) {
                float x = px(trainingPoints[i][0], left, plotWidth);
                float y = py(trainingPoints[i][1], top, plotHeight);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(
                        trainingLabels[i] == 0
                                ? Color.rgb(38, 99, 180)
                                : Color.rgb(38, 150, 94)
                );

                canvas.drawCircle(x, y, dp(6), paint);

                boolean isNearest = false;

                for (int index : lastNearestIndices) {
                    if (index == i) {
                        isNearest = true;
                        break;
                    }
                }

                if (isNearest) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(dp(2.5f));
                    paint.setColor(Color.rgb(230, 135, 35));

                    canvas.drawCircle(x, y, dp(9), paint);
                }
            }

            // Query point.
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.5f));
            paint.setColor(Color.rgb(210, 65, 65));

            canvas.drawCircle(qx, qy, dp(10), paint);

            canvas.drawLine(
                    qx - dp(6), qy,
                    qx + dp(6), qy,
                    paint
            );

            canvas.drawLine(
                    qx, qy - dp(6),
                    qx, qy + dp(6),
                    paint
            );

            // Prediction label.
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(dp(11));
            paint.setFakeBoldText(true);

            paint.setColor(
                    lastPredictedClass == 0
                            ? Color.rgb(38, 99, 180)
                            : Color.rgb(38, 150, 94)
            );

            float labelX = Math.min(qx + dp(12), right - dp(65));
            float labelY = Math.max(top + dp(14), qy - dp(12));

            canvas.drawText(
                    "Class " + lastPredictedClass,
                    labelX, labelY, paint
            );

            paint.setFakeBoldText(false);

            // Legend.
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(dp(10));

            paint.setColor(Color.rgb(38, 99, 180));
            canvas.drawCircle(
                    left + dp(7), getHeight() - dp(9), dp(4), paint
            );

            paint.setColor(Color.DKGRAY);
            canvas.drawText(
                    "Class 0", left + dp(14), getHeight() - dp(5), paint
            );

            paint.setColor(Color.rgb(38, 150, 94));
            canvas.drawCircle(
                    left + dp(78), getHeight() - dp(9), dp(4), paint
            );

            paint.setColor(Color.DKGRAY);
            canvas.drawText(
                    "Class 1", left + dp(85), getHeight() - dp(5), paint
            );

            paint.setColor(Color.rgb(230, 135, 35));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));

            canvas.drawCircle(
                    left + dp(153), getHeight() - dp(10), dp(6), paint
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.DKGRAY);

            canvas.drawText(
                    "Nearest", left + dp(163), getHeight() - dp(5), paint
            );
        }
    }
}
