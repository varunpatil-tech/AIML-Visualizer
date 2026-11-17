
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BlocksWorldActivity extends AppCompatActivity {

    private BlocksWorldView blocksWorldView;
    private TextView stepText;
    private TextView actionText;
    private TextView stateText;

    private Button startButton;
    private Button nextButton;
    private Button resetButton;

    private int currentStep = -1;

    // A = 0, B = 1, C = 2.
    // -1 means on the table; otherwise, the value is the supporting block.
    private final int[][] states = {
            {-1, -1, 1},   // Initial: A and B on table, C on B.
            {-1, -1, -1}, // Move C to table.
            {-1, 2, -1},  // Move B onto C.
            {1, 2, -1}    // Move A onto B.
    };

    private final String[] stepTitles = {
            "Initial State",
            "Step 1 of 3: Move C to the table",
            "Step 2 of 3: Move B onto C",
            "Step 3 of 3: Move A onto B"
    };

    private final String[] actions = {
            "No action executed yet.\n\nInitial arrangement:\nA is on the table, B is on the table, and C is on B.",

            "ACTION: MOVE(C, TABLE)\n\nPreconditions:\n• C is clear (no block is on C).\n• C is on B.\n\nEffects:\n• Remove ON(C, B).\n• Add ONTABLE(C).\n• B becomes clear.",

            "ACTION: MOVE(B, C)\n\nPreconditions:\n• B is clear.\n• C is clear.\n• B is on the table.\n\nEffects:\n• Remove ONTABLE(B).\n• Add ON(B, C).",

            "ACTION: MOVE(A, B)\n\nPreconditions:\n• A is clear.\n• B is clear.\n• A is on the table.\n\nEffects:\n• Remove ONTABLE(A).\n• Add ON(A, B).\n\nAll goal conditions are now satisfied."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks_world);

        blocksWorldView = findViewById(R.id.blocksWorldView);
        stepText = findViewById(R.id.blocksWorldStepText);
        actionText = findViewById(R.id.blocksWorldActionText);
        stateText = findViewById(R.id.blocksWorldStateText);

        startButton = findViewById(R.id.blocksWorldStartButton);
        nextButton = findViewById(R.id.blocksWorldNextButton);
        resetButton = findViewById(R.id.blocksWorldResetButton);

        startButton.setOnClickListener(v -> {
            currentStep = 0;
            updateDisplay();
        });

        nextButton.setOnClickListener(v -> {
            if (currentStep == -1) {
                currentStep = 0;
            } else if (currentStep < states.length - 1) {
                currentStep++;
            }
            updateDisplay();
        });

        resetButton.setOnClickListener(v -> {
            currentStep = -1;
            updateDisplay();
        });

        updateDisplay();
    }

    private void updateDisplay() {
        if (currentStep == -1) {
            blocksWorldView.setState(states[0]);
            stepText.setText("Press Start to inspect the initial state.");
            actionText.setText("The planner is ready. Start the simulation to view the initial arrangement and its action sequence.");
            stateText.setText("Initial: A on table; B on table; C on B.\nGoal: A on B; B on C; C on table.");
            nextButton.setEnabled(true);
            return;
        }

        blocksWorldView.setState(states[currentStep]);
        stepText.setText(stepTitles[currentStep]);
        actionText.setText(actions[currentStep]);
        stateText.setText(describeState(states[currentStep]));

        nextButton.setEnabled(currentStep < states.length - 1);

        if (currentStep == states.length - 1) {
            stepText.setText("GOAL REACHED!");
            stateText.append("\n\nAll goal conditions are satisfied.");
        }
    }

    private String describeState(int[] state) {
        StringBuilder result = new StringBuilder();

        for (int block = 0; block < 3; block++) {
            if (state[block] == -1) {
                result.append((char) ('A' + block))
                        .append(" is on the table.\n");
            } else {
                result.append((char) ('A' + block))
                        .append(" is on ")
                        .append((char) ('A' + state[block]))
                        .append(".\n");
            }
        }

        return result.toString().trim();
    }
}
