
package com.example.aiml_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Unit1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit1);

        Button hillClimbingButton = findViewById(R.id.hillClimbingButton);
        hillClimbingButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, HillClimbingActivity.class)));

        Button bestFirstButton = findViewById(R.id.bestFirstButton);
        bestFirstButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, BestFirstActivity.class)));

        Button meansEndsButton = findViewById(R.id.meansEndsButton);
        meansEndsButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, MeansEndsActivity.class)));

        Button cspButton = findViewById(R.id.cspButton);
        cspButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, CSPActivity.class)));

        Button aStarButton = findViewById(R.id.aStarButton);
        aStarButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, AStarActivity.class)));

        Button aoStarButton = findViewById(R.id.aoStarButton);
        aoStarButton.setOnClickListener(v ->
                startActivity(new Intent(Unit1Activity.this, AOStarActivity.class)));
    }
}
