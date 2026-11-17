
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NonLinearPlanningActivity extends AppCompatActivity {

    private TextView goalsText;
    private TextView planText;
    private TextView actionText;
    private TextView explanationText;
    private TextView progressText;

    private Button startButton;
    private Button nextButton;
    private Button resetButton;

    private final String[] actions = {
            "Boil water",
            "Put bread in toaster",
            "Pour hot water into cup",
            "Toast bread",
            "Add tea bag and steep",
            "Serve tea and toast"
    };

    private final String[] explanations = {
            "Start the tea preparation branch. Boiling water is required before pouring it into the cup.",
            "Start the independent toast branch. This can happen while the water is boiling.",
            "The water is ready. Pour it into the cup before steeping the tea.",
            "The bread is in the toaster. Toast it to complete the toast branch.",
            "The hot water is in the cup. Add the tea bag and allow the tea to steep.",
            "Both tea and toast are ready. The final serving action completes the task."
    };

    private final String[] dependencies = {
            "Boil water → Pour water",
            "Insert bread → Toast bread",
            "Pour water → Steep tea",
            "Toast bread → Serve",
            "Steep tea → Serve"
    };

    private int currentStep = -1;
    private boolean planStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_linear_planning);

        goalsText = findViewById(R.id.nonLinearGoalsText);
        planText = findViewById(R.id.nonLinearPlanText);
        actionText = findViewById(R.id.nonLinearActionText);
        explanationText = findViewById(R.id.nonLinearExplanationText);
        progressText = findViewById(R.id.nonLinearProgressText);

        startButton = findViewById(R.id.startNonLinearButton);
        nextButton = findViewById(R.id.nextNonLinearButton);
        resetButton = findViewById(R.id.resetNonLinearButton);

        startButton.setOnClickListener(v -> startPlanning());
        nextButton.setOnClickListener(v -> nextAction());
        resetButton.setOnClickListener(v -> resetPlanner());

        resetPlanner();
    }

    private void startPlanning() {
        currentStep = -1;
        planStarted = true;

        goalsText.setText(
                "1. Tea ready\n" +
                        "2. Toast ready"
        );

        planText.setText(
                "Tea branch:\n" +
                        "Boil water → Pour water → Steep tea\n\n" +
                        "Toast branch:\n" +
                        "Insert bread → Toast bread\n\n" +
                        "Ordering constraints:\n" +
                        "• Boil water before pouring.\n" +
                        "• Pour water before steeping.\n" +
                        "• Insert bread before toasting.\n" +
                        "• Complete both branches before serving."
        );

        actionText.setText("Plan generated. Press Next Action.");
        explanationText.setText(
                "The tea and toast branches are partially independent. " +
                        "A partial-order plan specifies necessary dependencies " +
                        "without requiring every action to have one fixed order."
        );

        progressText.setText("Step 0 of 6");
        nextButton.setEnabled(true);
    }

    private void nextAction() {
        if (!planStarted) {
            explanationText.setText("Press Start Planning first.");
            return;
        }

        if (currentStep >= actions.length - 1) {
            actionText.setText("Planning complete");
            explanationText.setText(
                    "The simulated plan has finished. Both goals are treated as achieved."
            );
            nextButton.setEnabled(false);
            return;
        }

        currentStep++;

        actionText.setText(actions[currentStep]);
        explanationText.setText(explanations[currentStep]);
        progressText.setText(
                "Step " + (currentStep + 1) + " of " + actions.length
        );

        if (currentStep == actions.length - 1) {
            explanationText.append(
                    "\n\nBoth goals are now achieved in this demonstration."
            );
        }
    }

    private void resetPlanner() {
        currentStep = -1;
        planStarted = false;

        goalsText.setText("Tea ready\nToast ready");
        planText.setText("Press Start to generate the plan.");
        actionText.setText("No action executed yet.");
        explanationText.setText(
                "A partial-order plan allows independent actions to be reordered " +
                        "while respecting required dependencies."
        );
        progressText.setText("Step 0");

        nextButton.setEnabled(false);
    }
}
