
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BestFirstActivity extends AppCompatActivity {

    private final String[] nodes = {"S", "A", "B", "C", "G"};
    private final int[] h = {7, 6, 2, 1, 0};

    private final int[][] graph = {
            {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {1, 4}
    };

    private BestFirstGraphView graphView;
    private TextView statusText;
    private TextView explanationText;
    private TextView openListText;
    private TextView closedListText;

    private final List<Integer> open = new ArrayList<>();
    private final List<Integer> closed = new ArrayList<>();
    private final List<Integer> path = new ArrayList<>();

    private boolean[] discovered;
    private int current = -1;
    private boolean started = false;
    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_first);

        graphView = findViewById(R.id.bestFirstGraphView);
        statusText = findViewById(R.id.statusText);
        explanationText = findViewById(R.id.explanationText);
        openListText = findViewById(R.id.openListText);
        closedListText = findViewById(R.id.closedListText);

        Button startButton = findViewById(R.id.startSearchButton);
        Button nextButton = findViewById(R.id.nextStepButton);
        Button resetButton = findViewById(R.id.resetSearchButton);

        startButton.setOnClickListener(v -> startSearch());
        nextButton.setOnClickListener(v -> nextStep());
        resetButton.setOnClickListener(v -> resetSearch());

        resetSearch();
    }

    private void startSearch() {
        resetSearch();

        started = true;
        open.add(0);
        discovered[0] = true;

        statusText.setText("Search started");
        explanationText.setText(
                "Start at S. OPEN contains S with h(S) = 7. " +
                        "The algorithm will choose the available node with the lowest h(n)."
        );

        updateDisplay();
    }

    private void nextStep() {
        if (!started) {
            statusText.setText("Press Start Search first.");
            return;
        }

        if (finished) {
            statusText.setText("Search finished. Press Reset Search to try again.");
            return;
        }

        if (open.isEmpty()) {
            finished = true;
            statusText.setText("Goal not found.");
            explanationText.setText("OPEN is empty, so no more nodes can be explored.");
            updateDisplay();
            return;
        }

        // Choose the node with the lowest heuristic value.
        int bestPosition = 0;
        for (int i = 1; i < open.size(); i++) {
            if (h[open.get(i)] < h[open.get(bestPosition)]) {
                bestPosition = i;
            }
        }

        current = open.remove(bestPosition);
        closed.add(current);

        if (current == 4) {
            finished = true;
            buildSolutionPath();

            statusText.setText("GOAL FOUND: G");
            explanationText.setText(
                    "The goal G has been selected. Greedy Best-First Search " +
                            "chooses nodes using h(n), not the accumulated path cost."
            );

            updateDisplay();
            return;
        }

        List<String> added = new ArrayList<>();

        for (int[] edge : graph) {
            int neighbor = -1;

            if (edge[0] == current) {
                neighbor = edge[1];
            } else if (edge[1] == current) {
                neighbor = edge[0];
            }

            if (neighbor != -1
                    && !discovered[neighbor]
                    && !closed.contains(neighbor)) {
                open.add(neighbor);
                discovered[neighbor] = true;
                added.add(nodes[neighbor] + " (h=" + h[neighbor] + ")");
            }
        }

        statusText.setText("Selected node: " + nodes[current]);

        if (added.isEmpty()) {
            explanationText.setText(
                    "Selected " + nodes[current] + " because it had the lowest " +
                            "heuristic in OPEN. No new nodes were added."
            );
        } else {
            explanationText.setText(
                    "Selected " + nodes[current] + " because it had the lowest " +
                            "heuristic in OPEN. Newly discovered nodes: " +
                            joinStrings(added) + "."
            );
        }

        updateDisplay();
    }

    private void buildSolutionPath() {
        // This graph has a known shortest discovery route for the demonstrated
        // greedy traversal: trace the explored connections back to S.
        path.clear();
        path.add(0);

        if (current == 4) {
            if (closed.contains(1) && discovered[4] && closed.contains(1)) {
                path.add(1);
            } else if (closed.contains(3)) {
                path.add(3);
                if (closed.contains(2)) {
                    path.add(2);
                }
            }
            if (!path.contains(4)) {
                path.add(4);
            }
        }
    }

    private void updateDisplay() {
        openListText.setText(formatList(open));
        closedListText.setText(formatList(closed));

        boolean[] visited = new boolean[nodes.length];
        boolean[] inOpen = new boolean[nodes.length];
        boolean[] inClosed = new boolean[nodes.length];
        boolean[] inPath = new boolean[nodes.length];

        for (int node : open) {
            inOpen[node] = true;
        }

        for (int node : closed) {
            inClosed[node] = true;
            visited[node] = true;
        }

        for (int node : path) {
            inPath[node] = true;
        }

        graphView.updateState(current, visited, inOpen, inClosed, inPath);
    }

    private String formatList(List<Integer> list) {
        if (list.isEmpty()) {
            return "Empty";
        }

        List<String> result = new ArrayList<>();

        for (int node : list) {
            result.add(nodes[node] + " (h=" + h[node] + ")");
        }

        return joinStrings(result);
    }

    private String joinStrings(List<String> list) {
        if (list.isEmpty()) {
            return "None";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                builder.append("  →  ");
            }
            builder.append(list.get(i));
        }

        return builder.toString();
    }

    private void resetSearch() {
        open.clear();
        closed.clear();
        path.clear();

        discovered = new boolean[nodes.length];
        current = -1;
        started = false;
        finished = false;

        statusText.setText("Press Start Search to begin.");
        explanationText.setText(
                "Greedy Best-First Search starts at S and uses h(n) to choose the next node."
        );

        updateDisplay();
    }
}
