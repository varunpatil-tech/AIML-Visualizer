
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CSPActivity extends AppCompatActivity {

    private CSPGraphView graphView;
    private TextView statusText;
    private TextView explanationText;
    private TextView assignmentText;

    private final String[] variables = {"A", "B", "C", "D"};
    private final String[] colors = {"Red", "Green", "Blue"};

    // Constraints: A-B, A-C, B-C, B-D, C-D.
    private final int[][] edges = {
            {0, 1}, {0, 2}, {1, 2}, {1, 3}, {2, 3}
    };

    private int[] assignments = {-1, -1, -1, -1};

    private final List<SearchEvent> events = new ArrayList<>();
    private int eventIndex = 0;
    private boolean started = false;
    private boolean finished = false;
    private boolean solutionFound = false;

    private static class SearchEvent {
        int[] state;
        int active;
        int conflict;
        int successful;
        String status;
        String explanation;

        SearchEvent(
                int[] state,
                int active,
                int conflict,
                int successful,
                String status,
                String explanation
        ) {
            this.state = state.clone();
            this.active = active;
            this.conflict = conflict;
            this.successful = successful;
            this.status = status;
            this.explanation = explanation;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cspactivity);

        graphView = findViewById(R.id.cspGraphView);
        statusText = findViewById(R.id.cspStatusText);
        explanationText = findViewById(R.id.cspExplanationText);
        assignmentText = findViewById(R.id.cspAssignmentText);

        Button startButton = findViewById(R.id.startCSPButton);
        Button nextButton = findViewById(R.id.nextCSPButton);
        Button resetButton = findViewById(R.id.resetCSPButton);

        startButton.setOnClickListener(v -> startSearch());
        nextButton.setOnClickListener(v -> nextStep());
        resetButton.setOnClickListener(v -> resetSearch());

        resetSearch();
    }

    private void startSearch() {
        resetSearch();

        events.clear();
        assignments = new int[]{-1, -1, -1, -1};

        addEvent(
                -1, -1, -1,
                "Search initialized",
                "Variables: A, B, C, D. Domain: Red, Green, Blue. " +
                        "Assign one color at a time while checking all adjacency constraints."
        );

        solutionFound = solve(0);

        if (solutionFound) {
            addEvent(
                    -1, -1, -1,
                    "SOLUTION FOUND",
                    "All variables have been assigned colors and every constraint is satisfied."
            );
        } else {
            addEvent(
                    -1, -1, -1,
                    "NO SOLUTION",
                    "All possible assignments were explored, but no valid solution exists."
            );
        }

        assignments = new int[]{-1, -1, -1, -1};
        eventIndex = 0;
        started = true;

        showEvent(events.get(eventIndex));
        eventIndex++;
    }

    private boolean solve(int variableIndex) {
        if (variableIndex == variables.length) {
            return true;
        }

        for (int color = 0; color < colors.length; color++) {
            assignments[variableIndex] = color;

            if (isConsistent(variableIndex)) {
                addEvent(
                        variableIndex, -1, -1,
                        "Assign " + colors[color] + " to " +
                                variables[variableIndex],
                        "The assignment is valid against all currently assigned neighbors. " +
                                "Continue to the next variable."
                );

                if (solve(variableIndex + 1)) {
                    return true;
                }

                // The deeper search failed: undo this assignment.
                assignments[variableIndex] = -1;

                addEvent(
                        variableIndex, -1, -1,
                        "Backtrack from " + variables[variableIndex],
                        "The remaining variables could not be assigned consistently. " +
                                "Undo this choice and try another color."
                );

            } else {
                // Show the rejected color briefly so the conflict is visible.
                addEvent(
                        variableIndex, variableIndex, -1,
                        "Constraint conflict",
                        variables[variableIndex] + " cannot be " + colors[color] +
                                " because an adjacent assigned region already has that color. " +
                                "Reject this value and try the next one."
                );

                assignments[variableIndex] = -1;

                addEvent(
                        variableIndex, -1, -1,
                        "Undo rejected assignment",
                        "The conflicting value has been removed. The variable remains unassigned."
                );
            }
        }

        assignments[variableIndex] = -1;
        return false;
    }

    private boolean isConsistent(int variable) {
        for (int[] edge : edges) {
            int a = edge[0];
            int b = edge[1];

            if (a == variable && assignments[b] != -1
                    && assignments[a] == assignments[b]) {
                return false;
            }

            if (b == variable && assignments[a] != -1
                    && assignments[a] == assignments[b]) {
                return false;
            }
        }

        return true;
    }

    private void addEvent(
            int active,
            int conflict,
            int successful,
            String status,
            String explanation
    ) {
        events.add(new SearchEvent(
                assignments,
                active,
                conflict,
                successful,
                status,
                explanation
        ));
    }

    private void nextStep() {
        if (!started) {
            statusText.setText("Press Start Search first.");
            return;
        }

        if (finished) {
            statusText.setText("Search finished. Press Reset to try again.");
            return;
        }

        if (eventIndex >= events.size()) {
            finished = true;
            return;
        }

        showEvent(events.get(eventIndex));
        eventIndex++;

        if (eventIndex >= events.size()) {
            finished = true;
        }
    }

    private void showEvent(SearchEvent event) {
        assignments = event.state.clone();

        statusText.setText(event.status);
        explanationText.setText(event.explanation);

        graphView.updateState(
                assignments,
                event.active,
                event.conflict,
                event.successful
        );

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < variables.length; i++) {
            if (text.length() > 0) {
                text.append("\n");
            }

            text.append(variables[i]).append(" = ");

            if (assignments[i] == -1) {
                text.append("Unassigned");
            } else {
                text.append(colors[assignments[i]]);
            }
        }

        assignmentText.setText(text.toString());
    }

    private void resetSearch() {
        assignments = new int[]{-1, -1, -1, -1};
        events.clear();
        eventIndex = 0;
        started = false;
        finished = false;
        solutionFound = false;

        graphView.updateState(assignments, -1, -1, -1);

        statusText.setText("Press Start Search to begin.");
        explanationText.setText(
                "Backtracking assigns a value, checks constraints, and undoes choices when necessary."
        );
        assignmentText.setText(
                "A = Unassigned\nB = Unassigned\nC = Unassigned\nD = Unassigned"
        );
    }
}
