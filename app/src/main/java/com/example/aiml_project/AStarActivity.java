
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class AStarActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView explanationText;
    private TextView openListText;
    private TextView closedListText;

    private EditText heuristicSInput;
    private EditText heuristicAInput;
    private EditText heuristicBInput;
    private EditText heuristicCInput;
    private EditText heuristicGInput;

    private AStarGraphView graphView;

    private final Map<String, Map<String, Integer>> graph =
            new HashMap<>();

    private final Map<String, Integer> heuristic =
            new HashMap<>();

    private final List<Step> steps = new ArrayList<>();
    private final List<String> finalPath = new ArrayList<>();

    private int stepIndex = 0;

    private static class NodeEntry {
        String node;
        int g;

        NodeEntry(String node, int g) {
            this.node = node;
            this.g = g;
        }
    }

    private static class Step {
        String heading;
        String details;
        String openList;
        String closedList;
        String activeNode;

        Step(String heading, String details,
             String openList, String closedList,
             String activeNode) {
            this.heading = heading;
            this.details = details;
            this.openList = openList;
            this.closedList = closedList;
            this.activeNode = activeNode;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astar);

        statusText = findViewById(R.id.statusText);
        explanationText = findViewById(R.id.explanationText);
        openListText = findViewById(R.id.openListText);
        closedListText = findViewById(R.id.closedListText);

        heuristicSInput = findViewById(R.id.heuristicSInput);
        heuristicAInput = findViewById(R.id.heuristicAInput);
        heuristicBInput = findViewById(R.id.heuristicBInput);
        heuristicCInput = findViewById(R.id.heuristicCInput);
        heuristicGInput = findViewById(R.id.heuristicGInput);

        graphView = findViewById(R.id.graphView);

        Button runButton = findViewById(R.id.runAStarButton);
        Button nextButton = findViewById(R.id.nextStepButton);
        Button resetButton = findViewById(R.id.resetButton);

        setupGraph();
        readHeuristics();
        prepareSearch();

        runButton.setOnClickListener(v -> runWithCustomHeuristics());
        nextButton.setOnClickListener(v -> showNextStep());
        resetButton.setOnClickListener(v -> resetDefaults());
    }

    private void setupGraph() {
        graph.clear();

        addEdge("S", "A", 2);
        addEdge("S", "B", 4);
        addEdge("A", "G", 3);
        addEdge("A", "C", 2);
        addEdge("B", "C", 1);
        addEdge("C", "G", 5);
    }

    private void addEdge(String from, String to, int cost) {
        graph.putIfAbsent(from, new HashMap<>());
        graph.putIfAbsent(to, new HashMap<>());

        graph.get(from).put(to, cost);
        graph.get(to).put(from, cost);
    }

    private boolean readHeuristics() {
        Map<String, EditText> inputs = new LinkedHashMap<>();

        inputs.put("S", heuristicSInput);
        inputs.put("A", heuristicAInput);
        inputs.put("B", heuristicBInput);
        inputs.put("C", heuristicCInput);
        inputs.put("G", heuristicGInput);

        Map<String, Integer> newValues = new HashMap<>();

        for (Map.Entry<String, EditText> entry : inputs.entrySet()) {
            String node = entry.getKey();
            EditText input = entry.getValue();

            String value = input.getText().toString().trim();

            if (value.isEmpty()) {
                input.setError("Enter a non-negative integer");
                input.requestFocus();
                return false;
            }

            try {
                int number = Integer.parseInt(value);

                if (number < 0) {
                    input.setError("Use a non-negative value");
                    input.requestFocus();
                    return false;
                }

                newValues.put(node, number);

            } catch (NumberFormatException exception) {
                input.setError("Enter a valid integer");
                input.requestFocus();
                return false;
            }
        }

        heuristic.clear();
        heuristic.putAll(newValues);

        return true;
    }

    private void runWithCustomHeuristics() {
        if (!readHeuristics()) {
            Toast.makeText(
                    this,
                    "Please enter valid heuristic values.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        prepareSearch();

        Toast.makeText(
                this,
                "A* ready with your heuristic values. Press Next Step.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void resetDefaults() {
        heuristicSInput.setText("5");
        heuristicAInput.setText("3");
        heuristicBInput.setText("5");
        heuristicCInput.setText("4");
        heuristicGInput.setText("0");

        readHeuristics();
        prepareSearch();
    }

    private void prepareSearch() {
        steps.clear();
        finalPath.clear();
        stepIndex = 0;

        graphView.setFinalPath(Collections.emptyList());
        graphView.setActiveNode("");

        openListText.setText("OPEN: { empty }");
        closedListText.setText("CLOSED: { empty }");

        runAStar();

        statusText.setText("Ready to begin");
        explanationText.setText(
                "Your heuristic values are loaded.\n\n" +
                        "Press Next Step to watch A* explore the graph. " +
                        "To try different values, edit the heuristics " +
                        "and press Run A* with These Values."
        );
    }

    private void runAStar() {
        Comparator<NodeEntry> comparator =
                Comparator
                        .comparingInt((NodeEntry entry) ->
                                entry.g + heuristic.get(entry.node))
                        .thenComparing(entry -> entry.node);

        PriorityQueue<NodeEntry> open =
                new PriorityQueue<>(comparator);

        Map<String, Integer> gScore = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> closed = new HashSet<>();

        gScore.put("S", 0);
        open.add(new NodeEntry("S", 0));

        addStep(
                "START",
                "Start at S.\n\n" +
                        "g(S) = 0\n" +
                        "h(S) = " + heuristic.get("S") + "\n" +
                        "f(S) = g(S) + h(S) = " +
                        heuristic.get("S") +
                        "\n\nS enters the OPEN list.",
                open, gScore, closed, "S"
        );

        while (!open.isEmpty()) {
            NodeEntry entry = open.poll();
            String current = entry.node;

            if (entry.g != gScore.getOrDefault(
                    current, Integer.MAX_VALUE)) {
                continue;
            }

            if (closed.contains(current)) {
                continue;
            }

            int g = gScore.get(current);
            int h = heuristic.get(current);
            int f = g + h;

            closed.add(current);

            addStep(
                    "EXPLORE " + current,
                    "A* selects node " + current + ".\n\n" +
                            "g(" + current + ") = " + g +
                            "\nh(" + current + ") = " + h +
                            "\nf(" + current + ") = " + f +
                            "\n\n" +
                            "The node moves from OPEN to CLOSED.",
                    open, gScore, closed, current
            );

            if (current.equals("G")) {
                buildFinalPath(parent);

                addStep(
                        "GOAL FOUND!",
                        "The goal G has been reached!\n\n" +
                                "Final path: " +
                                String.join(" → ", finalPath) +
                                "\nTotal path cost: " + g +
                                "\n\nThe green path shows the route found by A*.",
                        open, gScore, closed, "G"
                );

                return;
            }

            for (Map.Entry<String, Integer> edge :
                    graph.get(current).entrySet()) {

                String neighbor = edge.getKey();
                int edgeCost = edge.getValue();

                int tentativeG = g + edgeCost;
                int oldG = gScore.getOrDefault(
                        neighbor, Integer.MAX_VALUE);

                if (tentativeG < oldG) {
                    gScore.put(neighbor, tentativeG);
                    parent.put(neighbor, current);

                    // Allow a node to be reconsidered if a better
                    // route to it is discovered.
                    closed.remove(neighbor);

                    int neighborH = heuristic.get(neighbor);
                    int neighborF = tentativeG + neighborH;

                    open.add(new NodeEntry(neighbor, tentativeG));

                    addStep(
                            "UPDATE " + neighbor,
                            "Consider " + neighbor +
                                    " through " + current + ".\n\n" +
                                    "Edge cost = " + edgeCost +
                                    "\ng(" + neighbor + ") = " + tentativeG +
                                    "\nh(" + neighbor + ") = " + neighborH +
                                    "\nf(" + neighbor + ") = " + neighborF +
                                    "\n\nThe best known route to " + neighbor +
                                    " has been updated in OPEN.",
                            open, gScore, closed, neighbor
                    );
                }
            }
        }

        addStep(
                "NO PATH",
                "The OPEN list is empty. No path to the goal was found.",
                open, gScore, closed, ""
        );
    }

    private void addStep(
            String heading,
            String details,
            PriorityQueue<NodeEntry> open,
            Map<String, Integer> gScore,
            Set<String> closed,
            String activeNode) {

        steps.add(new Step(
                heading,
                details,
                formatOpenList(open, gScore, closed),
                formatClosedList(closed, gScore),
                activeNode
        ));
    }

    private String formatOpenList(
            PriorityQueue<NodeEntry> open,
            Map<String, Integer> gScore,
            Set<String> closed) {

        Map<String, Integer> bestEntries = new HashMap<>();

        for (NodeEntry entry : open) {
            if (closed.contains(entry.node)) {
                continue;
            }

            int bestG = gScore.getOrDefault(
                    entry.node, Integer.MAX_VALUE);

            if (entry.g == bestG) {
                bestEntries.put(entry.node, entry.g);
            }
        }

        List<String> nodes = new ArrayList<>(bestEntries.keySet());

        nodes.sort(
                Comparator
                        .comparingInt((String node) ->
                                bestEntries.get(node) + heuristic.get(node))
                        .thenComparing(node -> node)
        );

        if (nodes.isEmpty()) {
            return "OPEN: { empty }";
        }

        StringBuilder result = new StringBuilder("OPEN:\n");

        for (String node : nodes) {
            int g = bestEntries.get(node);
            int h = heuristic.get(node);
            int f = g + h;

            result.append(node)
                    .append("  (g=").append(g)
                    .append(", h=").append(h)
                    .append(", f=").append(f)
                    .append(")\n");
        }

        return result.toString().trim();
    }

    private String formatClosedList(
            Set<String> closed,
            Map<String, Integer> gScore) {

        if (closed.isEmpty()) {
            return "CLOSED: { empty }";
        }

        List<String> nodes = new ArrayList<>(closed);
        Collections.sort(nodes);

        StringBuilder result = new StringBuilder("CLOSED:\n");

        for (String node : nodes) {
            int g = gScore.getOrDefault(node, 0);
            int h = heuristic.get(node);
            int f = g + h;

            result.append(node)
                    .append("  (g=").append(g)
                    .append(", h=").append(h)
                    .append(", f=").append(f)
                    .append(")\n");
        }

        return result.toString().trim();
    }

    private void buildFinalPath(Map<String, String> parent) {
        finalPath.clear();

        String node = "G";
        finalPath.add(node);

        while (parent.containsKey(node)) {
            node = parent.get(node);
            finalPath.add(0, node);
        }
    }

    private void showNextStep() {
        if (stepIndex >= steps.size()) {
            statusText.setText("Demonstration complete");
            explanationText.setText(
                    "Press Reset Defaults to restore the original " +
                            "heuristics, or change the values and run A* again."
            );
            return;
        }

        Step step = steps.get(stepIndex++);

        statusText.setText(step.heading);
        explanationText.setText(step.details);
        openListText.setText(step.openList);
        closedListText.setText(step.closedList);

        if (step.heading.equals("GOAL FOUND!")) {
            graphView.setFinalPath(finalPath);
            graphView.setActiveNode("G");
        } else {
            graphView.setActiveNode(step.activeNode);
        }

        if (stepIndex >= steps.size()) {
            explanationText.append(
                    "\n\nPress Reset Defaults or run A* again to restart."
            );
        }
    }
}
