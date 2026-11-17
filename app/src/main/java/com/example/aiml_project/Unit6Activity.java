
package com.example.aiml_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Unit6Activity extends AppCompatActivity {

    private TextView progressText;
    private TextView topicCategory;
    private TextView topicTitle;
    private TextView explanation;
    private TextView example;
    private TextView takeaway;

    private Spinner topicSpinner;
    private Button previousButton;
    private Button nextButton;

    private EditText sampleMeanInput;
    private EditText hypothesizedMeanInput;
    private EditText stdDevInput;
    private EditText sampleSizeInput;
    private EditText alphaInput;
    private TextView hypothesisResult;

    private EditText featureInput;
    private EditText thresholdInput;
    private TextView designResult;

    private ZDistributionPlotView zDistributionPlot;

    private int currentTopic = 0;

    private final String[] topicTitles = {
            "Types of Variables",
            "Types of Measurement Scales",
            "Constructing the Hypothesis",
            "Null and Alternative Hypotheses",
            "Hypothesis Testing",
            "Type I and Type II Errors",
            "Confidence Intervals"
    };

    private final String[] topicCategories = {
            "DATA FUNDAMENTALS",
            "DATA FUNDAMENTALS",
            "HYPOTHESIS DESIGN",
            "HYPOTHESIS DESIGN",
            "STATISTICAL INFERENCE",
            "STATISTICAL INFERENCE",
            "STATISTICAL INFERENCE"
    };

    private final String[] topicExplanations = {
            "A variable is a characteristic that can take different values.\n\n"
                    + "1. Qualitative: categories such as branch or blood group.\n\n"
                    + "2. Quantitative: numerical measurements or counts.\n\n"
                    + "3. Discrete: countable values, such as number of students.\n\n"
                    + "4. Continuous: measurable values, such as height, weight, or temperature.\n\n"
                    + "The variable type determines how data can be represented and analyzed.",

            "Measurement scales describe how values can be interpreted.\n\n"
                    + "1. Nominal: categories without an order, such as blood group.\n\n"
                    + "2. Ordinal: ordered categories, such as low, medium, high.\n\n"
                    + "3. Interval: equal intervals without a meaningful absolute zero, such as Celsius temperature.\n\n"
                    + "4. Ratio: equal intervals with a meaningful zero, such as height and mass.\n\n"
                    + "The measurement scale affects which statistical operations are appropriate.",

            "A hypothesis is a clear, testable statement about a population or relationship.\n\n"
                    + "Steps to construct one:\n"
                    + "1. Identify the research problem.\n"
                    + "2. Define the population and variables.\n"
                    + "3. Specify the expected outcome.\n"
                    + "4. Make the statement measurable and testable.\n"
                    + "5. Choose a suitable statistical test.\n\n"
                    + "A good hypothesis is specific, measurable, and supported by a research question.",

            "Hypothesis testing compares two statements.\n\n"
                    + "Null hypothesis (H0): the baseline claim, often no effect or no difference.\n\n"
                    + "Alternative hypothesis (H1): the effect or difference being investigated.\n\n"
                    + "Example:\n"
                    + "H0: μ = 100\n"
                    + "H1: μ ≠ 100\n\n"
                    + "A two-tailed test checks for differences in either direction. A one-tailed test checks a prespecified direction.",

            "Hypothesis testing uses sample data to evaluate a population claim.\n\n"
                    + "1. State H0 and H1.\n"
                    + "2. Choose significance level α.\n"
                    + "3. Select the statistical test.\n"
                    + "4. Calculate the test statistic and p-value.\n"
                    + "5. Compare the p-value with α.\n\n"
                    + "If p ≤ α, reject H0. Otherwise, fail to reject H0.\n\n"
                    + "The p-value is calculated assuming H0 is true; it is not the probability that H0 itself is true.",

            "Type I error (α): rejecting a true null hypothesis. This is a false positive.\n\n"
                    + "Type II error (β): failing to reject a false null hypothesis. This is a false negative.\n\n"
                    + "Example: incorrectly detecting an effect is Type I; missing a real effect is Type II.\n\n"
                    + "Statistical power is 1 − β, the probability of detecting an effect when a specified alternative is true.",

            "A confidence interval estimates a plausible range for an unknown population parameter.\n\n"
                    + "When population standard deviation σ is known, the two-sided Z confidence interval for a population mean is:\n\n"
                    + "Sample mean ± critical Z × (σ / √n)\n\n"
                    + "Here, n is the sample size and σ is the known population standard deviation.\n\n"
                    + "A 95% confidence procedure captures the true parameter in 95% of repeated samples under its assumptions. A higher confidence level generally produces a wider interval."
    };

    private final String[] topicExamples = {
            "Student age is quantitative. Number of failed subjects is discrete. Branch name is qualitative.",
            "Satisfaction level is ordinal. Student ID is nominal. Celsius temperature is interval. Mass in kilograms is ratio.",
            "Research question: do students using a new study method score higher? Define the groups, outcome, and expected difference before collecting data.",
            "H0: μ = 75\nH1: μ ≠ 75\n\nThis is a two-tailed test.",
            "If α = 0.05 and p = 0.03, reject H0. If p = 0.12, fail to reject H0.",
            "Declaring an ineffective model better is a Type I error. Failing to detect a genuinely better model is a Type II error.",
            "For sample mean 105, σ = 15, n = 36, and 95% confidence, the interval is approximately 100.10 to 109.90."
    };

    private final String[] topicTakeaways = {
            "Identify whether a variable is categorical or numerical before choosing an analysis.",
            "Remember: nominal, ordinal, interval, ratio. Ratio has a meaningful zero.",
            "A hypothesis must be specific, measurable, and testable.",
            "H0 is the baseline; H1 represents the effect or difference being investigated.",
            "Reject H0 when p ≤ α. Otherwise, fail to reject H0.",
            "Type I = false positive. Type II = false negative. Power = 1 − β.",
            "A confidence interval communicates uncertainty around an estimate."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit6);

        bindViews();
        setupTopicSpinner();
        setupNavigation();
        setupStatisticalTesting();
        setupHypothesisDesign();

        displayTopic(0);
    }

    private void bindViews() {
        progressText = findViewById(R.id.unit6ProgressText);
        topicSpinner = findViewById(R.id.unit6TopicSpinner);
        topicCategory = findViewById(R.id.unit6TopicCategory);
        topicTitle = findViewById(R.id.unit6TopicTitle);
        explanation = findViewById(R.id.unit6Explanation);
        example = findViewById(R.id.unit6Example);
        takeaway = findViewById(R.id.unit6Takeaway);

        previousButton = findViewById(R.id.unit6PreviousButton);
        nextButton = findViewById(R.id.unit6NextButton);

        sampleMeanInput = findViewById(R.id.unit6SampleMeanInput);
        hypothesizedMeanInput = findViewById(R.id.unit6HypothesizedMeanInput);
        stdDevInput = findViewById(R.id.unit6StdDevInput);
        sampleSizeInput = findViewById(R.id.unit6SampleSizeInput);
        alphaInput = findViewById(R.id.unit6AlphaInput);
        hypothesisResult = findViewById(R.id.unit6HypothesisResult);

        featureInput = findViewById(R.id.unit6FeatureInput);
        thresholdInput = findViewById(R.id.unit6ThresholdInput);
        designResult = findViewById(R.id.unit6DesignResult);

        zDistributionPlot = findViewById(R.id.unit6ZDistributionPlot);
    }

    private void setupTopicSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                topicTitles
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
                        displayTopic(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action required.
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
            if (currentTopic < topicTitles.length - 1) {
                topicSpinner.setSelection(currentTopic + 1);
            }
        });
    }

    private void displayTopic(int position) {
        if (position < 0 || position >= topicTitles.length) {
            return;
        }

        currentTopic = position;

        progressText.setText(String.format(
                Locale.getDefault(),
                "Topic %d of %d",
                position + 1,
                topicTitles.length
        ));

        topicCategory.setText(topicCategories[position]);
        topicTitle.setText(topicTitles[position]);
        explanation.setText(topicExplanations[position]);
        example.setText(topicExamples[position]);
        takeaway.setText(topicTakeaways[position]);

        previousButton.setEnabled(position > 0);
        nextButton.setEnabled(position < topicTitles.length - 1);
    }

    private void setupStatisticalTesting() {
        Button calculateButton = findViewById(R.id.unit6CalculateButton);
        calculateButton.setOnClickListener(v -> calculateZTest());
    }

    private void calculateZTest() {
        try {
            double sampleMean = readDouble(sampleMeanInput, "Sample mean");
            double hypothesizedMean =
                    readDouble(hypothesizedMeanInput, "Hypothesized mean");
            double sigma = readDouble(stdDevInput, "Population standard deviation");
            int n = readInteger(sampleSizeInput, "Sample size");
            double alpha = readDouble(alphaInput, "Significance level");

            if (!Double.isFinite(sampleMean)
                    || !Double.isFinite(hypothesizedMean)
                    || !Double.isFinite(sigma)
                    || !Double.isFinite(alpha)) {
                throw new IllegalArgumentException(
                        "Enter valid finite numbers in all fields."
                );
            }

            if (sigma <= 0) {
                throw new IllegalArgumentException(
                        "Population standard deviation must be greater than zero."
                );
            }

            if (n <= 0) {
                throw new IllegalArgumentException(
                        "Sample size must be greater than zero."
                );
            }

            if (alpha <= 0 || alpha >= 1) {
                throw new IllegalArgumentException(
                        "Significance level must be between 0 and 1, exclusive."
                );
            }

            double standardError = sigma / Math.sqrt(n);
            double z = (sampleMean - hypothesizedMean) / standardError;

            double pValue = 2.0 * (1.0 - normalCdf(Math.abs(z)));
            pValue = Math.max(0.0, Math.min(1.0, pValue));

            double confidenceLevel = 1.0 - alpha;
            double criticalZ = inverseNormalCdf(1.0 - alpha / 2.0);

            double marginOfError = criticalZ * standardError;
            double lowerBound = sampleMean - marginOfError;
            double upperBound = sampleMean + marginOfError;

            String decision = pValue <= alpha
                    ? "Reject H0"
                    : "Fail to reject H0";

            String interpretation = pValue <= alpha
                    ? "The sample provides statistically significant evidence against H0."
                    : "The sample does not provide sufficient evidence to reject H0.";

            String result = String.format(
                    Locale.getDefault(),
                    "Z-TEST RESULT (TWO-TAILED)\n\n"
                            + "Sample mean: %.4f\n"
                            + "Hypothesized mean: %.4f\n"
                            + "Population standard deviation: %.4f\n"
                            + "Sample size: %d\n"
                            + "Standard error: %.4f\n"
                            + "Z-score: %.4f\n"
                            + "P-value: %.6f\n"
                            + "Significance level: %.4f\n\n"
                            + "Decision: %s\n%s\n\n"
                            + "%.1f%% CONFIDENCE INTERVAL\n"
                            + "Lower bound: %.4f\n"
                            + "Upper bound: %.4f\n"
                            + "Margin of error: %.4f\n\n"
                            + "Assumption: population standard deviation is known.",
                    sampleMean,
                    hypothesizedMean,
                    sigma,
                    n,
                    standardError,
                    z,
                    pValue,
                    alpha,
                    decision,
                    interpretation,
                    confidenceLevel * 100.0,
                    lowerBound,
                    upperBound,
                    marginOfError
            );

            hypothesisResult.setText(result);

            // Update the plot using the same Z-score and alpha.
            zDistributionPlot.setZScore(z, alpha);

        } catch (IllegalArgumentException e) {
            hypothesisResult.setText("Input error: " + e.getMessage());
            zDistributionPlot.setZScore(Double.NaN, 0.05);
        }
    }

    private void setupHypothesisDesign() {
        Button designButton = findViewById(R.id.unit6DesignButton);
        designButton.setOnClickListener(v -> designHypothesis());
    }

    private void designHypothesis() {
        try {
            String feature = featureInput.getText().toString().trim();
            double threshold = readDouble(thresholdInput, "Threshold");

            if (feature.isEmpty()) {
                throw new IllegalArgumentException(
                        "Enter a feature or measurable outcome."
                );
            }

            if (!Double.isFinite(threshold)) {
                throw new IllegalArgumentException(
                        "Enter a valid finite threshold."
                );
            }

            String result = String.format(
                    Locale.getDefault(),
                    "HYPOTHESIS DESIGN\n\n"
                            + "Research variable: %s\n"
                            + "Threshold: %.4f\n\n"
                            + "Research question:\n"
                            + "Is the population mean of %s greater than %.4f?\n\n"
                            + "H0: μ ≤ %.4f\n"
                            + "H1: μ > %.4f\n\n"
                            + "Test direction: Right-tailed\n\n"
                            + "This is an example for testing whether a population mean exceeds a threshold. "
                            + "Choose hypotheses according to the actual research question.",
                    feature,
                    threshold,
                    feature,
                    threshold,
                    threshold,
                    threshold
            );

            designResult.setText(result);

        } catch (IllegalArgumentException e) {
            designResult.setText("Input error: " + e.getMessage());
        }
    }

    private double readDouble(EditText input, String fieldName) {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number."
            );
        }
    }

    private int readInteger(EditText input, String fieldName) {
        String value = input.getText().toString().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a whole number."
            );
        }
    }

    private double normalCdf(double z) {
        if (z == 0.0) {
            return 0.5;
        }

        if (z < 0.0) {
            return 1.0 - normalCdf(-z);
        }

        double t = 1.0 / (1.0 + 0.2316419 * z);

        double polynomial = t * (
                0.319381530
                        + t * (
                        -0.356563782
                                + t * (
                                1.781477937
                                        + t * (
                                        -1.821255978
                                                + t * 1.330274429
                                )
                        )
                )
        );

        double density = Math.exp(-0.5 * z * z)
                / Math.sqrt(2.0 * Math.PI);

        return 1.0 - density * polynomial;
    }

    private double inverseNormalCdf(double p) {
        if (p <= 0.0 || p >= 1.0) {
            throw new IllegalArgumentException(
                    "Probability must be between 0 and 1."
            );
        }

        double[] a = {
                -39.69683028665376,
                220.9460984245205,
                -275.9285104469687,
                138.3577518672690,
                -30.66479806614716,
                2.506628277459239
        };

        double[] b = {
                -54.47609879822406,
                161.5858368580409,
                -155.6989798598866,
                66.80131188771972,
                -13.28068155288572
        };

        double[] c = {
                -0.007784894002430293,
                -0.3223964580411365,
                -2.400758277161838,
                -2.549732539343734,
                4.374664141464968,
                2.938163982698783
        };

        double[] d = {
                0.007784695709041462,
                0.3224671290700398,
                2.445134137142996,
                3.754408661907416
        };

        double low = 0.02425;
        double high = 1.0 - low;

        if (p < low) {
            double q = Math.sqrt(-2.0 * Math.log(p));

            return (((((c[0] * q + c[1]) * q + c[2]) * q
                    + c[3]) * q + c[4]) * q + c[5])
                    / ((((d[0] * q + d[1]) * q + d[2]) * q
                    + d[3]) * q + 1.0);
        }

        if (p > high) {
            double q = Math.sqrt(-2.0 * Math.log(1.0 - p));

            return -(((((c[0] * q + c[1]) * q + c[2]) * q
                    + c[3]) * q + c[4]) * q + c[5])
                    / ((((d[0] * q + d[1]) * q + d[2]) * q
                    + d[3]) * q + 1.0);
        }

        double q = p - 0.5;
        double r = q * q;

        return (((((a[0] * r + a[1]) * r + a[2]) * r
                + a[3]) * r + a[4]) * r + a[5]) * q
                / (((((b[0] * r + b[1]) * r + b[2]) * r
                + b[3]) * r + b[4]) * r + 1.0);
    }
}
