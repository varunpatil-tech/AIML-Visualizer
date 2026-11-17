
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LeastCommitmentActivity extends AppCompatActivity {

    private TextView scenarioText;
    private TextView planText;
    private TextView explanationText;
    private TextView progressText;
    private TextView comparisonText;

    private Button startButton;
    private Button previousButton;
    private Button nextButton;
    private Button resetButton;
    private Button compareButton;

    private int currentStep = -1;
    private boolean comparisonVisible = false;

    private final String[] plans = {
            "Required tasks:\n1. Inspect Room A\n2. Inspect Room B\n3. Return to the charging station\n\nNo order has been chosen for the inspections yet.",

            "Step 1: Identify the goal\n\nThe robot must inspect both rooms and then return to its charging station.",

            "Step 2: Identify dependencies\n\nBoth room inspections must happen before returning to the charging station.\n\nInspect Room A → Return\nInspect Room B → Return",

            "Step 3: Keep independent tasks unordered\n\nThe robot can inspect Room A first or Room B first. Neither inspection depends on the other.",

            "Step 4: Commit only when necessary\n\nIf the robot discovers that Room A is temporarily blocked, it can inspect Room B first without redesigning the entire plan.",

            "Final partial-order plan:\n\nInspect Room A ──┐\n                         ├── Return to charging station\nInspect Room B ──┘\n\nThe order between the two inspections remains flexible."
    };

    private final String[] explanations = {
            "Start with the problem, not an arbitrary action order. The planner records the required tasks and the final goal.",

            "Planning begins by describing what must be achieved. Here, both rooms must be inspected and the robot must eventually return.",

            "The return action depends on both inspections being completed. Therefore, both inspections must precede the return action.",

            "There is no dependency between the two inspections. The least commitment strategy avoids imposing an unnecessary order between them.",

            "Keeping the plan flexible allows the robot to respond to new information. It can choose the next inspection when the decision becomes necessary.",

            "The final plan specifies only the necessary ordering constraints. Both inspections happen before the return, but their relative order is not fixed."
    };

    private final String[] stepTitles = {
            "Initial Problem",
            "Define the Goal",
            "Identify Dependencies",
            "Preserve Flexibility",
            "Respond to New Information",
            "Final Flexible Plan"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_least_commitment);

        scenarioText = findViewById(R.id.lcsScenarioText);
        planText = findViewById(R.id.lcsPlanText);
        explanationText = findViewById(R.id.lcsExplanationText);
        progressText = findViewById(R.id.lcsProgressText);
        comparisonText = findViewById(R.id.lcsComparisonText);

        startButton = findViewById(R.id.lcsStartButton);
        previousButton = findViewById(R.id.lcsPreviousButton);
        nextButton = findViewById(R.id.lcsNextButton);
        resetButton = findViewById(R.id.lcsResetButton);
        compareButton = findViewById(R.id.lcsCompareButton);

        startButton.setOnClickListener(v -> startPlanning());

        nextButton.setOnClickListener(v -> {
            if (currentStep == -1) {
                startPlanning();
            } else if (currentStep < plans.length - 1) {
                currentStep++;
                updateStep();
            }
        });

        previousButton.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                updateStep();
            }
        });

        resetButton.setOnClickListener(v -> resetPlanning());

        compareButton.setOnClickListener(v -> {
            comparisonVisible = !comparisonVisible;
            updateComparison();
        });

        resetPlanning();
    }

    private void startPlanning() {
        currentStep = 0;
        comparisonVisible = false;
        updateStep();
        updateComparison();
    }

    private void updateStep() {
        scenarioText.setText(stepTitles[currentStep]);
        planText.setText(plans[currentStep]);
        explanationText.setText(explanations[currentStep]);

        progressText.setText(
                "Step " + (currentStep + 1) + " of " + plans.length
        );

        previousButton.setEnabled(currentStep > 0);
        nextButton.setEnabled(currentStep < plans.length - 1);
        startButton.setEnabled(false);

        updateComparison();
    }

    private void updateComparison() {
        if (!comparisonVisible) {
            comparisonText.setText(
                    "Tap Compare Strategies to see how the two approaches differ."
            );
            compareButton.setText("Compare Strategies");
            return;
        }

        comparisonText.setText(
                "LEAST COMMITMENT\n" +
                        "• Keeps Room A and Room B unordered.\n" +
                        "• Commits to an inspection order only when needed.\n" +
                        "• Preserves flexibility when circumstances change.\n\n" +
                        "PREMATURE COMMITMENT\n" +
                        "• Immediately decides to inspect Room A before Room B.\n" +
                        "• Adds an ordering constraint that may be unnecessary.\n" +
                        "• May require replanning if Room A is blocked.\n\n" +
                        "KEY DIFFERENCE\n" +
                        "Least commitment delays unnecessary decisions; it does not remove required dependencies."
        );

        compareButton.setText("Hide Comparison");
    }

    private void resetPlanning() {
        currentStep = -1;
        comparisonVisible = false;

        scenarioText.setText("Least Commitment Strategy");
        planText.setText(
                "A robot must inspect Room A and Room B, then return to its charging station.\n\n" +
                        "Question: Must the robot decide which room to inspect first before planning?"
        );
        explanationText.setText(
                "Explore the plan step by step. Notice which action orderings are necessary and which can remain undecided."
        );
        progressText.setText("Ready to begin");

        startButton.setEnabled(true);
        previousButton.setEnabled(false);
        nextButton.setEnabled(true);
        compareButton.setText("Compare Strategies");

        comparisonText.setText(
                "The comparison will appear here when you tap Compare Strategies."
        );
    }
}
