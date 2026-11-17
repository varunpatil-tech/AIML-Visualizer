
package com.example.aiml_project;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StripsPlannerActivity extends AppCompatActivity {

    private TextView stateText;
    private TextView actionText;
    private TextView preconditionsText;
    private TextView addListText;
    private TextView deleteListText;
    private TextView explanationText;
    private TextView progressText;
    private TextView transitionText;
    private TextView goalStatusText;

    private Spinner actionSpinner;

    private Button startButton;
    private Button previousButton;
    private Button nextButton;
    private Button resetButton;
    private Button inspectButton;
    private Button backButton;

    private final List<StripsAction> actions = new ArrayList<>();
    private final List<Set<String>> history = new ArrayList<>();

    private Set<String> currentState = new HashSet<>();
    private int executedActions = 0;

    private static class StripsAction {
        final String name;
        final List<String> preconditions;
        final List<String> addList;
        final List<String> deleteList;
        final String explanation;

        StripsAction(
                String name,
                String preconditions,
                String addList,
                String deleteList,
                String explanation
        ) {
            this.name = name;
            this.preconditions = splitFacts(preconditions);
            this.addList = splitFacts(addList);
            this.deleteList = splitFacts(deleteList);
            this.explanation = explanation;
        }

        private static List<String> splitFacts(String facts) {
            return Arrays.asList(facts.split("\\s*,\\s*"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strips_planner);

        // Back button
        backButton = findViewById(R.id.stripsBackButton);
        backButton.setOnClickListener(v -> finish());

        // Connect views
        stateText = findViewById(R.id.stripsStateText);
        actionText = findViewById(R.id.stripsActionText);
        preconditionsText = findViewById(R.id.stripsPreconditionsText);
        addListText = findViewById(R.id.stripsAddListText);
        deleteListText = findViewById(R.id.stripsDeleteListText);
        explanationText = findViewById(R.id.stripsExplanationText);
        progressText = findViewById(R.id.stripsProgressText);
        transitionText = findViewById(R.id.stripsTransitionText);
        goalStatusText = findViewById(R.id.stripsGoalStatusText);

        actionSpinner = findViewById(R.id.stripsActionSpinner);

        startButton = findViewById(R.id.stripsStartButton);
        previousButton = findViewById(R.id.stripsPreviousButton);
        nextButton = findViewById(R.id.stripsNextButton);
        resetButton = findViewById(R.id.stripsResetButton);
        inspectButton = findViewById(R.id.stripsInspectButton);

        createActions();
        setupActionSpinner();
        resetSimulation();

        actionSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        inspectSelectedAction();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        inspectButton.setOnClickListener(
                v -> inspectSelectedAction()
        );

        nextButton.setOnClickListener(v -> executeSelectedAction());

        previousButton.setOnClickListener(
                v -> undoLastAction()
        );

        resetButton.setOnClickListener(
                v -> resetSimulation()
        );

        startButton.setOnClickListener(
                v -> resetSimulation()
        );
    }

    private void createActions() {
        actions.clear();

        addAction(
                "UNSTACK(C, B)",
                "ON(C,B), CLEAR(C), ARMEMPTY",
                "HOLDING(C), CLEAR(B)",
                "ON(C,B), CLEAR(C), ARMEMPTY",
                "Remove C from B and hold C."
        );

        addAction(
                "PUTDOWN(A)",
                "HOLDING(A)",
                "ONTABLE(A), CLEAR(A), ARMEMPTY",
                "HOLDING(A)",
                "Put block A onto the table."
        );

        addAction(
                "PUTDOWN(B)",
                "HOLDING(B)",
                "ONTABLE(B), CLEAR(B), ARMEMPTY",
                "HOLDING(B)",
                "Put block B onto the table."
        );

        addAction(
                "PUTDOWN(C)",
                "HOLDING(C)",
                "ONTABLE(C), CLEAR(C), ARMEMPTY",
                "HOLDING(C)",
                "Put block C onto the table."
        );

        addAction(
                "PICKUP(A)",
                "ONTABLE(A), CLEAR(A), ARMEMPTY",
                "HOLDING(A)",
                "ONTABLE(A), CLEAR(A), ARMEMPTY",
                "Pick up block A from the table."
        );

        addAction(
                "PICKUP(B)",
                "ONTABLE(B), CLEAR(B), ARMEMPTY",
                "HOLDING(B)",
                "ONTABLE(B), CLEAR(B), ARMEMPTY",
                "Pick up block B from the table."
        );

        addAction(
                "PICKUP(C)",
                "ONTABLE(C), CLEAR(C), ARMEMPTY",
                "HOLDING(C)",
                "ONTABLE(C), CLEAR(C), ARMEMPTY",
                "Pick up block C from the table."
        );

        addAction(
                "STACK(A, B)",
                "HOLDING(A), CLEAR(B)",
                "ON(A,B), CLEAR(A), ARMEMPTY",
                "HOLDING(A), CLEAR(B)",
                "Place A on top of B."
        );

        addAction(
                "STACK(A, C)",
                "HOLDING(A), CLEAR(C)",
                "ON(A,C), CLEAR(A), ARMEMPTY",
                "HOLDING(A), CLEAR(C)",
                "Place A on top of C."
        );

        addAction(
                "STACK(B, A)",
                "HOLDING(B), CLEAR(A)",
                "ON(B,A), CLEAR(B), ARMEMPTY",
                "HOLDING(B), CLEAR(A)",
                "Place B on top of A."
        );

        addAction(
                "STACK(B, C)",
                "HOLDING(B), CLEAR(C)",
                "ON(B,C), CLEAR(B), ARMEMPTY",
                "HOLDING(B), CLEAR(C)",
                "Place B on top of C."
        );

        addAction(
                "STACK(C, A)",
                "HOLDING(C), CLEAR(A)",
                "ON(C,A), CLEAR(C), ARMEMPTY",
                "HOLDING(C), CLEAR(A)",
                "Place C on top of A."
        );

        addAction(
                "STACK(C, B)",
                "HOLDING(C), CLEAR(B)",
                "ON(C,B), CLEAR(C), ARMEMPTY",
                "HOLDING(C), CLEAR(B)",
                "Place C on top of B."
        );
    }

    private void addAction(
            String name,
            String preconditions,
            String addList,
            String deleteList,
            String explanation
    ) {
        actions.add(new StripsAction(
                name,
                preconditions,
                addList,
                deleteList,
                explanation
        ));
    }

    private void setupActionSpinner() {
        List<String> actionNames = new ArrayList<>();

        for (StripsAction action : actions) {
            actionNames.add(action.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                actionNames
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        actionSpinner.setAdapter(adapter);
    }

    private Set<String> createInitialState() {
        Set<String> state = new HashSet<>();

        state.add("ONTABLE(A)");
        state.add("ONTABLE(B)");
        state.add("ON(C,B)");
        state.add("CLEAR(A)");
        state.add("CLEAR(C)");
        state.add("ARMEMPTY");

        return state;
    }

    private void resetSimulation() {
        currentState = createInitialState();

        history.clear();
        history.add(new HashSet<>(currentState));

        executedActions = 0;

        transitionText.setText(
                "Simulation reset. Select an action to begin."
        );

        updateScreen();
        inspectSelectedAction();
    }

    private StripsAction getSelectedAction() {
        int position = actionSpinner.getSelectedItemPosition();

        if (position < 0 || position >= actions.size()) {
            return null;
        }

        return actions.get(position);
    }

    private List<String> getMissingPreconditions(StripsAction action) {
        List<String> missing = new ArrayList<>();

        if (action == null) {
            return missing;
        }

        for (String fact : action.preconditions) {
            if (!currentState.contains(fact)) {
                missing.add(fact);
            }
        }

        return missing;
    }

    private void inspectSelectedAction() {
        StripsAction action = getSelectedAction();

        if (action == null) {
            return;
        }

        actionText.setText("Selected Action: " + action.name);

        preconditionsText.setText(
                "PRECONDITIONS\n" + formatFacts(action.preconditions)
        );

        addListText.setText(
                "ADD LIST\n" + formatFacts(action.addList)
        );

        deleteListText.setText(
                "DELETE LIST\n" + formatFacts(action.deleteList)
        );

        List<String> missing = getMissingPreconditions(action);

        if (missing.isEmpty()) {
            explanationText.setText(
                    "ACTION IS VALID\n\n"
                            + action.explanation
                            + "\n\nAll preconditions are satisfied. "
                            + "You can execute this action."
            );
        } else {
            explanationText.setText(
                    "ACTION IS BLOCKED\n\n"
                            + action.explanation
                            + "\n\nMissing preconditions:\n"
                            + formatFacts(missing)
                            + "\n\nChoose another action or execute "
                            + "actions that establish these facts."
            );
        }

        updateProgress();
    }

    private void executeSelectedAction() {
        StripsAction action = getSelectedAction();

        if (action == null) {
            transitionText.setText("Please select an action first.");
            return;
        }

        List<String> missing = getMissingPreconditions(action);

        if (!missing.isEmpty()) {
            transitionText.setText(
                    "ACTION NOT EXECUTED: " + action.name
                            + "\n\nMissing preconditions:\n"
                            + formatFacts(missing)
            );

            inspectSelectedAction();
            return;
        }

        Set<String> before = new HashSet<>(currentState);

        // STRIPS state transition: delete facts, then add facts.
        for (String fact : action.deleteList) {
            currentState.remove(fact);
        }

        for (String fact : action.addList) {
            currentState.add(fact);
        }

        executedActions++;
        history.add(new HashSet<>(currentState));

        transitionText.setText(
                "ACTION EXECUTED: " + action.name
                        + "\n\nBEFORE:\n" + formatState(before)
                        + "\n\nAFTER:\n" + formatState(currentState)
        );

        updateScreen();
        inspectSelectedAction();
    }

    private void undoLastAction() {
        if (history.size() <= 1) {
            transitionText.setText(
                    "Nothing to undo. The initial state is active."
            );
            return;
        }

        history.remove(history.size() - 1);

        currentState = new HashSet<>(
                history.get(history.size() - 1)
        );

        executedActions = history.size() - 1;

        transitionText.setText(
                "Last action undone. Previous state restored."
        );

        updateScreen();
        inspectSelectedAction();
    }

    private void updateScreen() {
        stateText.setText(
                "CURRENT WORLD STATE\n\n" + formatState(currentState)
        );

        boolean goalAchieved =
                currentState.contains("ON(A,B)")
                        && currentState.contains("ON(B,C)");

        if (goalAchieved) {
            goalStatusText.setText("GOAL ACHIEVED");
            goalStatusText.setTextColor(Color.rgb(20, 120, 60));
        } else {
            goalStatusText.setText("GOAL NOT ACHIEVED");
            goalStatusText.setTextColor(Color.rgb(180, 60, 45));
        }

        previousButton.setEnabled(history.size() > 1);

        updateProgress();
    }

    private void updateProgress() {
        progressText.setText(
                "Actions executed: " + executedActions
                        + "\nAvailable actions: " + actions.size()
                        + "\nSaved states: " + history.size()
        );
    }

    private String formatState(Set<String> state) {
        if (state.isEmpty()) {
            return "(No facts in the current state)";
        }

        List<String> facts = new ArrayList<>(state);
        Collections.sort(facts);

        StringBuilder result = new StringBuilder();

        for (String fact : facts) {
            result.append("• ").append(fact).append("\n");
        }

        return result.toString().trim();
    }

    private String formatFacts(List<String> facts) {
        if (facts == null || facts.isEmpty()) {
            return "(None)";
        }

        StringBuilder result = new StringBuilder();

        for (String fact : facts) {
            result.append("• ").append(fact).append("\n");
        }

        return result.toString().trim();
    }
}
