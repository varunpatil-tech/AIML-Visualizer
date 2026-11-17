
package com.example.aiml_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button unit1Button = findViewById(R.id.unit1Button);
        unit1Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit1Activity.class
                )));

        Button unit2Button = findViewById(R.id.unit2Button);
        unit2Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit2Activity.class
                )));

        Button unit3Button = findViewById(R.id.unit3Button);
        unit3Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit3Activity.class
                )));

        Button unit4Button = findViewById(R.id.unit4Button);
        unit4Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit4Activity.class
                )));

        // Unit 5 navigation
        Button unit5Button = findViewById(R.id.unit5Button);
        unit5Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit5Activity.class
                )));
        Button unit6Button = findViewById(R.id.unit6Button);
        unit6Button.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        Unit6Activity.class
                )));
    }
}
