
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AOStarActivity extends AppCompatActivity {

    private AOStarGraphView graphView;
    private TextView stepText;
    private TextView explanationText;
    private TextView costText;

    private int currentStep = 0;

    private final String[] stepTitles = {
            "Ready to begin",
            "Step 1: Start at S",
            "Step 2: Compare alternatives",
            "Step 3: Expand node A",
            "Step 4: Calculate costs",
            "Step 5: Optimal solution found"
    };

    private final String[] explanations = {
            "Press Start to begin the AO* search demonstration.",
            "S is an OR node. AO* can choose A, or choose the AND combination B and C.",
            "The AND branch requires both B and C. Their combined estimated cost is (1 + 2) + (1 + 2) = 6. The A branch is promising.",
            "Expand A, which is also an OR node. It offers two alternatives: D and E.",
            "Cost through D = 1 + 4 = 5. Cost through E = 1 + 2 = 3. Therefore, A selects E.",
            "The optimal solution subgraph is S → A → E. Its total cost is 1 + 1 + 2 = 4, which is less than the AND branch cost of 6."
    };

    private final String[] costs = {
            "Initial estimates: h(S) = 4",
            "S alternatives: A, or the AND combination (B, C)",
            "AND branch cost = (1 + 2) + (1 + 2) = 6",
            "A alternatives: D and E",
            "Through D = 5 | Through E = 3 | Best cost at A = 3",
            "Best solution: S → A → E | Total cost = 4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aostar);

        graphView = findViewById(R.id.aoStarGraphView);
        stepText = findViewById(R.id.aoStarStepText);
        explanationText = findViewById(R.id.aoStarExplanationText);
        costText = findViewById(R.id.aoStarCostText);

        Button startButton = findViewById(R.id.startAOStarButton);
        Button nextButton = findViewById(R.id.nextAOStarButton);
        Button resetButton = findViewById(R.id.resetAOStarButton);

        startButton.setOnClickListener(v -> {
            currentStep = 1;
            updateDisplay();
        });

        nextButton.setOnClickListener(v -> {
            if (currentStep == 0) {
                currentStep = 1;
            } else if (currentStep < 5) {
                currentStep++;
            }
            updateDisplay();
        });

        resetButton.setOnClickListener(v -> {
            currentStep = 0;
            updateDisplay();
        });

        updateDisplay();
    }

    private void updateDisplay() {
        graphView.setStep(currentStep);
        stepText.setText(stepTitles[currentStep]);
        explanationText.setText(explanations[currentStep]);
        costText.setText(costs[currentStep]);
    }
}
