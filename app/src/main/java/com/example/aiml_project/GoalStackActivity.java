
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoalStackActivity extends AppCompatActivity {

    private TextView stackText;
    private TextView worldStateText;
    private TextView explanationText;
    private TextView progressText;

    private Button startButton;
    private Button nextButton;
    private Button resetButton;

    private final List<String> steps = new ArrayList<>();
    private final List<String> explanations = new ArrayList<>();
    private int currentStep = -1;

    // support[i] = -1 means block i is on the table.
    // 0 = A, 1 = B, 2 = C.
    private final int[] initialState = {-1, -1, 1};
    private int[] worldState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_stack);

        stackText = findViewById(R.id.goalStackText);
        worldStateText = findViewById(R.id.goalWorldStateText);
        explanationText = findViewById(R.id.goalStackExplanationText);
        progressText = findViewById(R.id.goalStackProgressText);

        startButton = findViewById(R.id.startGoalStackButton);
        nextButton = findViewById(R.id.nextGoalStackButton);
        resetButton = findViewById(R.id.resetGoalStackButton);

        startButton.setOnClickListener(v -> startPlanning());
        nextButton.setOnClickListener(v -> nextStep());
        resetButton.setOnClickListener(v -> resetPlanner());

        resetPlanner();
    }

    private void startPlanning() {
        steps.clear();
        explanations.clear();

        // Goal-stack planning trace.
        addStep(
                "GOAL: ON(A,B)\nGOAL: ON(B,C)",
                "The planner places the required goals on its stack. " +
                        "It must achieve ON(B,C) and ON(A,B)."
        );

        addStep(
                "CLEAR(A)\nON(A,B)\nON(B,C)",
                "To place A on B, A must be clear. A is already clear, " +
                        "so the planner can proceed toward the goal."
        );

        addStep(
                "MOVE(C, TABLE)",
                "C is on B, preventing B from being moved onto C. " +
                        "C is clear, so move C to the table first."
        );

        addStep(
                "MOVE(B, C)",
                "B is now clear and C is on the table. " +
                        "The preconditions are satisfied, so place B on C."
        );

        addStep(
                "MOVE(A, B)",
                "A and B are clear. Move A onto B to achieve the " +
                        "remaining goal."
        );

        addStep(
                "GOALS ACHIEVED",
                "Both goals are true: ON(B,C) and ON(A,B). " +
                        "The final arrangement is A on B, B on C, C on the table."
        );

        worldState = Arrays.copyOf(initialState, initialState.length);
        currentStep = 0;
        updateScreen();
    }

    private void addStep(String stack, String explanation) {
        steps.add(stack);
        explanations.add(explanation);
    }

    private void nextStep() {
        if (currentStep < 0) {
            explanationText.setText("Press Start Planning first.");
            return;
        }

        if (currentStep >= steps.size() - 1) {
            explanationText.setText("Planning is complete. Press Reset to try again.");
            return;
        }

        currentStep++;

        // Execute the simulated world-state changes at the relevant steps.
        if (currentStep == 2) {
            worldState[2] = -1; // C onto table
        } else if (currentStep == 3) {
            worldState[1] = 2;  // B onto C
        } else if (currentStep == 4) {
            worldState[0] = 1;  // A onto B
        }

        updateScreen();
    }

    private void updateScreen() {
        stackText.setText(steps.get(currentStep));
        explanationText.setText(explanations.get(currentStep));
        progressText.setText(
                "Step " + (currentStep + 1) + " of " + steps.size()
        );

        worldStateText.setText(
                "A: " + locationOf(0) + "\n" +
                        "B: " + locationOf(1) + "\n" +
                        "C: " + locationOf(2)
        );

        nextButton.setEnabled(currentStep < steps.size() - 1);
    }

    private String locationOf(int block) {
        if (worldState[block] == -1) {
            return "On Table";
        }

        return "On " + (char) ('A' + worldState[block]);
    }

    private void resetPlanner() {
        steps.clear();
        explanations.clear();

        worldState = Arrays.copyOf(initialState, initialState.length);
        currentStep = -1;

        stackText.setText("Press Start Planning to initialize the goal stack.");
        worldStateText.setText(
                "A: On Table\nB: On Table\nC: On B"
        );
        explanationText.setText(
                "The initial state has A and B on the table, with C on B."
        );
        progressText.setText("Step 0");

        nextButton.setEnabled(false);
    }
}
