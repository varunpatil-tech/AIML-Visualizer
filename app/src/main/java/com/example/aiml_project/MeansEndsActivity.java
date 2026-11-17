
package com.example.aiml_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MeansEndsActivity extends AppCompatActivity {

    private MeansEndsView meansEndsView;
    private TextView currentStateText;
    private TextView differenceText;
    private TextView actionText;

    // Disk numbers: 1 = smallest, 3 = largest.
    // Peg numbers: 0 = A, 1 = B, 2 = C.
    private int[] diskPeg;

    private final List<Move> moves = new ArrayList<>();
    private int moveIndex = 0;
    private boolean started = false;
    private boolean finished = false;

    private static class Move {
        int disk;
        int from;
        int to;

        Move(int disk, int from, int to) {
            this.disk = disk;
            this.from = from;
            this.to = to;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_means_ends);

        meansEndsView = findViewById(R.id.meansEndsView);
        currentStateText = findViewById(R.id.currentStateText);
        differenceText = findViewById(R.id.differenceText);
        actionText = findViewById(R.id.actionText);

        Button startButton = findViewById(R.id.startMeansEndsButton);
        Button nextButton = findViewById(R.id.nextMeansEndsButton);
        Button resetButton = findViewById(R.id.resetMeansEndsButton);

        startButton.setOnClickListener(v -> startSearch());
        nextButton.setOnClickListener(v -> nextStep());
        resetButton.setOnClickListener(v -> resetSearch());

        resetSearch();
    }

    private void startSearch() {
        resetSearch();

        started = true;

        statusMessage(
                "Analysis started. The current state differs from the goal."
        );

        differenceText.setText(
                "Difference: all three disks are on peg A, but the goal " +
                        "requires all three disks on peg C."
        );

        actionText.setText(
                "Plan: move the smaller disks temporarily to peg B so the " +
                        "largest disk can be moved to peg C."
        );

        updateCurrentState();
    }

    private void nextStep() {
        if (!started) {
            actionText.setText("Press Start before advancing.");
            return;
        }

        if (finished) {
            actionText.setText("Goal reached. Press Reset to try again.");
            return;
        }

        if (moveIndex >= moves.size()) {
            finished = true;
            showGoalReached();
            return;
        }

        Move move = moves.get(moveIndex);

        // Move the selected disk to the destination peg.
        diskPeg[move.disk] = move.to;
        moveIndex++;

        meansEndsView.setDiskPositions(diskPeg);
        updateCurrentState();

        String[] pegNames = {"A", "B", "C"};

        actionText.setText(
                "Action " + moveIndex + ": move disk " + move.disk +
                        " from peg " + pegNames[move.from] +
                        " to peg " + pegNames[move.to] + "."
        );

        differenceText.setText(
                "Remaining moves: " + (moves.size() - moveIndex) +
                        ". The selected legal move advances the planned sequence " +
                        "toward the goal state."
        );

        if (moveIndex >= moves.size()) {
            finished = true;
            showGoalReached();
        }
    }

    private void resetSearch() {
        diskPeg = new int[]{0, 0, 0, 0};

        moves.clear();
        moveIndex = 0;
        started = false;
        finished = false;

        // Generate the legal Tower of Hanoi plan: 3 disks, A to C via B.
        generateMoves(3, 0, 2, 1);

        meansEndsView.setDiskPositions(diskPeg);

        statusMessage("Press Start to begin the analysis.");

        differenceText.setText(
                "Difference: the disks are on A, while the goal requires " +
                        "them to be on C."
        );

        actionText.setText(
                "The system will choose actions that reduce the difference."
        );

        updateCurrentState();
    }

    private void generateMoves(
            int diskCount,
            int from,
            int to,
            int auxiliary
    ) {
        if (diskCount == 0) {
            return;
        }

        generateMoves(diskCount - 1, from, auxiliary, to);
        moves.add(new Move(diskCount, from, to));
        generateMoves(diskCount - 1, auxiliary, to, from);
    }

    private void updateCurrentState() {
        String[] pegNames = {"A", "B", "C"};
        StringBuilder state = new StringBuilder();

        for (int peg = 0; peg < 3; peg++) {
            state.append(pegNames[peg]).append(": ");

            boolean hasDisk = false;

            // Show largest to smallest for readability.
            for (int disk = 3; disk >= 1; disk--) {
                if (diskPeg[disk] == peg) {
                    if (hasDisk) {
                        state.append(", ");
                    }

                    state.append("disk ").append(disk);
                    hasDisk = true;
                }
            }

            if (!hasDisk) {
                state.append("empty");
            }

            if (peg < 2) {
                state.append("\n");
            }
        }

        currentStateText.setText(state.toString());
    }

    private void showGoalReached() {
        finished = true;

        statusMessage("GOAL REACHED");

        differenceText.setText(
                "Difference: none. All three disks are now on peg C."
        );

        actionText.setText(
                "The goal state has been achieved in 7 legal moves. " +
                        "The planned sequence is complete."
        );
    }

    private void statusMessage(String message) {
        TextView statusText = findViewById(R.id.currentStateText);

        // The main current-state panel is kept for the actual peg state.
        // Show the message in the action panel until a move is selected.
        if (!started && moveIndex == 0) {
            actionText.setText(message);
        }
    }
}
