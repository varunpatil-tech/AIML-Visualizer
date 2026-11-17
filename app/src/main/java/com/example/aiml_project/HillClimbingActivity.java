
package com.example.aiml_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HillClimbingActivity extends AppCompatActivity {

    private TextView stateText;
    private TextView explanationText;
    private Spinner startStateSpinner;
    private HillLandscapeView landscapeView;

    private final int[] values = {1, 3, 5, 4, 7, 6, 9, 8, 6};

    private final List<SearchStep> steps = new ArrayList<>();
    private int stepIndex = 0;
    private int currentIndex = -1;
    private int startIndex = -1;
    private boolean searchFinished = false;

    private static class SearchStep {
        int index;
        String heading;
        String explanation;
        boolean finished;

        SearchStep(int index, String heading,
                   String explanation, boolean finished) {
            this.index = index;
            this.heading = heading;
            this.explanation = explanation;
            this.finished = finished;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_climbing);

        stateText = findViewById(R.id.stateText);
        explanationText = findViewById(R.id.explanationText);
        startStateSpinner = findViewById(R.id.startStateSpinner);
        landscapeView = findViewById(R.id.landscapeView);

        Button startButton = findViewById(R.id.startButton);
        Button nextButton = findViewById(R.id.nextStepButton);
        Button resetButton = findViewById(R.id.resetButton);

        String[] states = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            states[i] = "S" + i + " (value = " + values[i] + ")";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                states
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        startStateSpinner.setAdapter(adapter);
        startStateSpinner.setSelection(0);

        startButton.setOnClickListener(v -> startSearch());
        nextButton.setOnClickListener(v -> showNextStep());
        resetButton.setOnClickListener(v -> resetSearch());

        resetSearch();
    }

    private void startSearch() {
        startIndex = startStateSpinner.getSelectedItemPosition();

        if (startIndex < 0 || startIndex >= values.length) {
            Toast.makeText(
                    this,
                    "Please select a starting state.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        steps.clear();
        stepIndex = 0;
        searchFinished = false;

        currentIndex = startIndex;

        landscapeView.setStartIndex(startIndex);
        landscapeView.setCurrentIndex(startIndex);
        landscapeView.setFinished(false);

        steps.add(new SearchStep(
                currentIndex,
                "START: S" + currentIndex,
                "Start at S" + currentIndex +
                        " with value " + values[currentIndex] +
                        ".\n\n" +
                        "Hill Climbing compares neighboring states " +
                        "and moves only when a better neighbor exists.",
                false
        ));

        buildSearchSteps();

        showNextStep();
    }

    private void buildSearchSteps() {
        int index = startIndex;

        while (true) {
            int bestNeighbor = index;

            // Examine the left neighbor.
            if (index > 0 && values[index - 1] > values[bestNeighbor]) {
                bestNeighbor = index - 1;
            }

            // Examine the right neighbor.
            if (index < values.length - 1
                    && values[index + 1] > values[bestNeighbor]) {
                bestNeighbor = index + 1;
            }

            if (bestNeighbor == index) {
                boolean isGlobalMaximum = values[index] == 9;

                String heading = isGlobalMaximum
                        ? "GLOBAL MAXIMUM FOUND"
                        : "LOCAL MAXIMUM REACHED";

                String explanation =
                        "Current state: S" + index +
                                "\nCurrent value: " + values[index] +
                                "\n\n";

                if (isGlobalMaximum) {
                    explanation +=
                            "This is the global maximum in the landscape.\n" +
                                    "No state has a higher value.\n\n" +
                                    "Hill Climbing has reached the best state.";
                } else {
                    explanation +=
                            "Neither immediate neighbor has a higher value.\n\n" +
                                    "The algorithm stops here, even though a " +
                                    "higher state exists elsewhere.\n\n" +
                                    "This demonstrates the local maximum problem.";
                }

                steps.add(new SearchStep(
                        index,
                        heading,
                        explanation,
                        true
                ));

                break;
            }

            int oldIndex = index;
            index = bestNeighbor;

            steps.add(new SearchStep(
                    index,
                    "MOVE: S" + oldIndex + " → S" + index,
                    "Current state: S" + oldIndex +
                            " (value = " + values[oldIndex] + ")" +
                            "\nBetter neighbor: S" + index +
                            " (value = " + values[index] + ")" +
                            "\n\nThe neighbor has a higher value, so " +
                            "Hill Climbing moves uphill.",
                    false
            ));
        }
    }

    private void showNextStep() {
        if (steps.isEmpty()) {
            stateText.setText("Ready to start");
            explanationText.setText(
                    "Choose a starting state and press Start Search."
            );
            return;
        }

        if (stepIndex >= steps.size()) {
            stateText.setText("Search complete");
            explanationText.setText(
                    "Press Start Search to try another starting state."
            );
            return;
        }

        SearchStep step = steps.get(stepIndex++);

        currentIndex = step.index;
        searchFinished = step.finished;

        stateText.setText(step.heading);
        explanationText.setText(step.explanation);

        landscapeView.setCurrentIndex(currentIndex);
        landscapeView.setFinished(searchFinished);

        if (searchFinished) {
            if (values[currentIndex] == 9) {
                landscapeView.setFinished(true);
            }
        }
    }

    private void resetSearch() {
        steps.clear();
        stepIndex = 0;
        currentIndex = -1;
        startIndex = -1;
        searchFinished = false;

        landscapeView.setCurrentIndex(-1);
        landscapeView.setStartIndex(-1);
        landscapeView.setFinished(false);

        stateText.setText("Ready to start");
        explanationText.setText(
                "Choose a starting state and press Start Search. " +
                        "Use Next Step to follow the algorithm."
        );

        if (startStateSpinner.getAdapter() != null) {
            startStateSpinner.setSelection(0);
        }
    }
}
