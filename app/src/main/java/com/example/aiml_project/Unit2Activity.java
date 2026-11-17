
package com.example.aiml_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Unit2Activity extends AppCompatActivity {

    private Spinner topicSpinner;
    private TextView categoryText;
    private TextView titleText;
    private TextView explanationText;
    private TextView exampleText;
    private TextView takeawayText;
    private TextView progressText;

    private Button previousButton;
    private Button nextButton;

    private int currentTopic = 0;

    private final String[] topics = {
            "1. What is Learning?",
            "2. Rote Learning",
            "3. Direct Instruction",
            "4. Learning by Analogy",
            "5. Inductive Learning",
            "6. Deductive Learning",
            "7. Blocks World",
            "8. STRIPS",
            "9. Goal Stack Planning",
            "10. Non-linear Planning",
            "11. Hierarchical Planning",
            "12. Least Commitment Strategy"
    };

    private final String[] categories = {
            "LEARNING", "LEARNING", "LEARNING",
            "LEARNING", "LEARNING", "LEARNING",
            "PLANNING", "PLANNING", "PLANNING",
            "PLANNING", "PLANNING", "PLANNING"
    };

    private final String[] titles = {
            "What is Learning?",
            "Rote Learning",
            "Direct Instruction",
            "Learning by Analogy",
            "Inductive Learning",
            "Deductive Learning",
            "Blocks World",
            "STRIPS",
            "Goal Stack Planning",
            "Non-linear Planning",
            "Hierarchical Planning",
            "Least Commitment Strategy"
    };

    private final String[] explanations = {
            "Learning is the process by which an AI system improves its knowledge, behaviour, or performance through experience, examples, instructions, or reasoning. The objective is to perform a task better or make more informed decisions.",

            "Rote learning stores and recalls previously encountered information without necessarily understanding the underlying principles. When the same situation appears again, the stored result can be reused.",

            "In direct instruction, knowledge is explicitly supplied by a teacher, programmer, expert, or labelled dataset. The learner follows the provided information rather than discovering the rule entirely by itself.",

            "Learning by analogy transfers knowledge from a familiar situation to a new situation that shares important similarities. The AI identifies corresponding features and adapts a known solution.",

            "Inductive learning derives a general rule from particular examples. The conclusion is probable rather than guaranteed, because new examples may contradict the inferred rule.",

            "Deductive reasoning applies a general rule or known facts to reach a specific conclusion. If the premises and logical inference are valid, the conclusion follows logically.",

            "Blocks World is a planning problem involving blocks that can be stacked on a table or on other blocks. An agent must transform an initial arrangement into a goal arrangement while obeying rules such as moving only a clear block.",

            "STRIPS is a classical planning representation. Each action specifies preconditions that must hold, an add list of facts made true, and a delete list of facts made false.",

            "Goal Stack Planning solves a problem by placing goals and required actions on a stack. If an action's preconditions are not satisfied, those preconditions are pushed onto the stack and achieved first.",

            "Non-linear, or partial-order, planning allows actions to remain unordered when no dependency requires a particular order. Only necessary ordering constraints are imposed, allowing independent actions to be arranged flexibly.",

            "Hierarchical planning decomposes a high-level task into smaller subtasks until executable actions are obtained. This is often called Hierarchical Task Network (HTN) planning.",

            "The least commitment strategy delays choices, such as action ordering or variable values, until those choices become necessary. This preserves flexibility and reduces the risk of making premature decisions."
    };

    private final String[] examples = {
            "An AI system records that a particular input was previously paired with a particular output and recalls that stored result when the same input appears again.",

            "A program stores the answer to a previously solved arithmetic question and retrieves that answer when the identical question is asked again.",

            "A developer provides an AI system with labelled examples: 'this image is a cat' and 'this image is a dog'. The system learns from the supplied labels.",

            "An AI that knows how a car navigates roads uses similar concepts to reason about how a delivery robot should navigate paths, while adapting to the differences.",

            "After observing many metal objects expand when heated, a learner infers the general rule that metals tend to expand when heated.",

            "Rule: all humans are mortal. Fact: Socrates is human. Conclusion: Socrates is mortal.",

            "Initial state: A is on the table, B is on the table, and C is on B. Goal: place A on B and B on C. The planner finds legal moves to reach the goal.",

            "Action STACK(A,B): preconditions are CLEAR(A), CLEAR(B), and HOLDING(A). Add effects include ON(A,B) and CLEAR(A); delete effects include CLEAR(B) and HOLDING(A).",

            "Goal: place A on B. The planner identifies STACK(A,B), then works backward to achieve its preconditions, such as holding A and ensuring B is clear.",

            "A robot must inspect two independent rooms before returning to its charging station. If neither inspection depends on the other, the planner need not specify which room comes first.",

            "Task: organise a meeting. Subtasks: choose a time, invite participants, reserve a room, and prepare the agenda. Each subtask can be broken down further.",

            "A planner knows that two independent inspections are required but postpones deciding their order until more information is available."
    };

    private final String[] takeaways = {
            "Learning improves future performance or behaviour using experience, information, or reasoning.",
            "Rote = memorise and retrieve.",
            "Direct instruction = knowledge is explicitly provided.",
            "Analogy = transfer knowledge between similar situations.",
            "Induction = specific examples to a general rule.",
            "Deduction = general rule to a specific conclusion.",
            "Blocks World tests planning through legal block movements and goal states.",
            "STRIPS = Preconditions + Add List + Delete List.",
            "Goal Stack Planning decomposes goals and satisfies action preconditions.",
            "Non-linear planning orders only actions that must depend on one another.",
            "Hierarchical planning = high-level task to smaller subtasks.",
            "Least commitment = delay unnecessary decisions to preserve flexibility."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit2);

        topicSpinner = findViewById(R.id.unit2TopicSpinner);
        categoryText = findViewById(R.id.unit2CategoryText);
        titleText = findViewById(R.id.unit2TitleText);
        explanationText = findViewById(R.id.unit2ExplanationText);
        exampleText = findViewById(R.id.unit2ExampleText);
        takeawayText = findViewById(R.id.unit2TakeawayText);
        progressText = findViewById(R.id.unit2ProgressText);

        previousButton = findViewById(R.id.unit2PreviousButton);
        nextButton = findViewById(R.id.unit2NextButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                topics
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        currentTopic = position;
                        updateTopic();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        previousButton.setOnClickListener(v -> {
            if (currentTopic > 0) {
                currentTopic--;
                topicSpinner.setSelection(currentTopic);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentTopic < topics.length - 1) {
                currentTopic++;
                topicSpinner.setSelection(currentTopic);
            }
        });

        // Blocks World Planner
        Button blocksWorldPlannerButton =
                findViewById(R.id.blocksWorldPlannerButton);

        blocksWorldPlannerButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        BlocksWorldActivity.class
                ))
        );

        // STRIPS Planner
        Button stripsPlannerButton =
                findViewById(R.id.stripsPlannerButton);

        stripsPlannerButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        StripsPlannerActivity.class
                ))
        );

        // Goal Stack Planning
        Button goalStackPlannerButton =
                findViewById(R.id.goalStackPlannerButton);

        goalStackPlannerButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        GoalStackActivity.class
                ))
        );

        // Non-linear Planning
        Button nonLinearPlanningButton =
                findViewById(R.id.nonLinearPlanningButton);

        nonLinearPlanningButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        NonLinearPlanningActivity.class
                ))
        );

        // Hierarchical Planning
        Button hierarchicalPlanningButton =
                findViewById(R.id.hierarchicalPlanningButton);

        hierarchicalPlanningButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        HierarchicalPlanningActivity.class
                ))
        );

        // Least Commitment Strategy
        Button leastCommitmentButton =
                findViewById(R.id.leastCommitmentButton);

        leastCommitmentButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        Unit2Activity.this,
                        LeastCommitmentActivity.class
                ))
        );

        updateTopic();
    }

    private void updateTopic() {
        categoryText.setText(categories[currentTopic]);
        titleText.setText(titles[currentTopic]);
        explanationText.setText(explanations[currentTopic]);
        exampleText.setText(examples[currentTopic]);
        takeawayText.setText(takeaways[currentTopic]);

        progressText.setText(
                "Topic " + (currentTopic + 1) + " of " + topics.length
        );

        previousButton.setEnabled(currentTopic > 0);
        nextButton.setEnabled(currentTopic < topics.length - 1);
    }
}
