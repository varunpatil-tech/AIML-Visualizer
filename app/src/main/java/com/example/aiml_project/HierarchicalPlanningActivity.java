
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HierarchicalPlanningActivity extends AppCompatActivity {

    private TextView taskText;
    private TextView treeText;
    private TextView actionText;
    private TextView explanationText;
    private TextView progressText;

    private Button startButton;
    private Button nextButton;
    private Button resetButton;

    private HierarchicalTreeView hierarchicalTreeView;

    private final String[] actions = {
            "Decompose: Prepare Breakfast",
            "Decompose: Prepare Tea",
            "Boil water",
            "Pour water into cup",
            "Add tea bag and steep",
            "Decompose: Prepare Toast",
            "Insert bread into toaster",
            "Toast bread",
            "Serve breakfast"
    };

    private final String[] explanations = {
            "The high-level task is decomposed into two subtasks: Prepare Tea and Prepare Toast.",
            "Prepare Tea is a compound task. Break it down into primitive actions.",
            "Boil water first because hot water is required to prepare tea.",
            "The water has boiled. Pour it into a cup.",
            "Add a tea bag and allow the tea to steep. The tea subtask is complete.",
            "Prepare Toast is another compound task that can be decomposed independently.",
            "Put bread into the toaster before activating the toasting action.",
            "Toast the bread. The toast subtask is now complete.",
            "Both subtasks are complete. Serve the tea and toast to finish breakfast."
    };

    private final String[] nodeNames = {
            "Prepare Breakfast",
            "Prepare Tea",
            "Prepare Toast",
            "Boil Water",
            "Pour Water",
            "Steep Tea",
            "Insert Bread",
            "Toast Bread"
    };

    private final String[] nodeDescriptions = {
            "ROOT TASK\n\nPrepare Breakfast is the overall goal.\n\n" +
                    "Subtasks:\n• Prepare Tea\n• Prepare Toast\n\n" +
                    "Both branches must be completed to achieve the goal.",

            "COMPOUND TASK\n\nPrepare Tea can be decomposed into three actions:\n\n" +
                    "1. Boil water\n2. Pour water into a cup\n3. Steep the tea\n\n" +
                    "These actions have ordering dependencies.",

            "COMPOUND TASK\n\nPrepare Toast can be decomposed into two actions:\n\n" +
                    "1. Insert bread into the toaster\n2. Toast bread\n\n" +
                    "The bread must be inserted before toasting.",

            "PRIMITIVE ACTION\n\nBoil water to prepare hot water for tea.\n\n" +
                    "Role: provides the hot water needed by the next action.",

            "PRIMITIVE ACTION\n\nPour the boiled water into a cup.\n\n" +
                    "Role: prepares the cup for steeping the tea.",

            "PRIMITIVE ACTION\n\nAdd a tea bag to the hot water and allow it to steep.\n\n" +
                    "Role: completes the tea preparation branch.",

            "PRIMITIVE ACTION\n\nInsert bread into the toaster.\n\n" +
                    "Role: establishes the condition required before toasting.",

            "PRIMITIVE ACTION\n\nToast the inserted bread.\n\n" +
                    "Role: completes the toast preparation branch."
    };

    private int currentStep = -1;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hierarchical_planning);

        taskText = findViewById(R.id.htnTaskText);
        treeText = findViewById(R.id.htnTreeText);
        actionText = findViewById(R.id.htnActionText);
        explanationText = findViewById(R.id.htnExplanationText);
        progressText = findViewById(R.id.htnProgressText);

        startButton = findViewById(R.id.htnStartButton);
        nextButton = findViewById(R.id.htnNextButton);
        resetButton = findViewById(R.id.htnResetButton);

        hierarchicalTreeView = findViewById(R.id.hierarchicalTreeView);

        hierarchicalTreeView.setOnNodeSelectedListener(nodeIndex -> {
            actionText.setText("Selected: " + nodeNames[nodeIndex]);
            explanationText.setText(nodeDescriptions[nodeIndex]);
        });

        startButton.setOnClickListener(v -> startPlanning());
        nextButton.setOnClickListener(v -> nextStep());
        resetButton.setOnClickListener(v -> resetPlanner());

        resetPlanner();
    }

    private void startPlanning() {
        currentStep = -1;
        started = true;

        hierarchicalTreeView.setCurrentStep(0);

        taskText.setText("Prepare Breakfast");

        treeText.setText(
                "Prepare Breakfast\n" +
                        "├── Prepare Tea\n" +
                        "│   ├── Boil water\n" +
                        "│   ├── Pour water into cup\n" +
                        "│   └── Add tea bag and steep\n" +
                        "└── Prepare Toast\n" +
                        "    ├── Insert bread into toaster\n" +
                        "    └── Toast bread"
        );

        actionText.setText("Task tree created.");

        explanationText.setText(
                "Hierarchical Task Network (HTN) planning decomposes " +
                        "compound tasks into smaller subtasks until primitive " +
                        "actions can be executed. Tap any node in the tree to explore it."
        );

        progressText.setText("Step 0 of " + actions.length);
        nextButton.setEnabled(true);
    }

    private void nextStep() {
        if (!started) {
            explanationText.setText("Press Start Decomposition first.");
            return;
        }

        if (currentStep >= actions.length - 1) {
            actionText.setText("Planning complete");

            explanationText.setText(
                    "All subtasks in this demonstration have been completed. " +
                            "The overall goal, Prepare Breakfast, is achieved."
            );

            hierarchicalTreeView.setCurrentStep(0);
            nextButton.setEnabled(false);
            return;
        }

        currentStep++;

        int[] highlightedNodes = {0, 1, 3, 4, 5, 2, 6, 7, 0};

        hierarchicalTreeView.setCurrentStep(
                highlightedNodes[currentStep]
        );

        actionText.setText(actions[currentStep]);
        explanationText.setText(explanations[currentStep]);

        progressText.setText(
                "Step " + (currentStep + 1) + " of " + actions.length
        );

        if (currentStep == actions.length - 1) {
            explanationText.append("\n\nThe overall task is complete.");
        }
    }

    private void resetPlanner() {
        currentStep = -1;
        started = false;

        taskText.setText("Prepare Breakfast");
        treeText.setText("Press Start to decompose the task.");
        actionText.setText("Waiting to start.");

        explanationText.setText(
                "A hierarchical planner breaks a complex task into " +
                        "smaller tasks and then into executable actions. " +
                        "Tap Start Decomposition to begin."
        );

        progressText.setText("Step 0");

        nextButton.setEnabled(false);
        hierarchicalTreeView.setCurrentStep(-1);
    }
}
