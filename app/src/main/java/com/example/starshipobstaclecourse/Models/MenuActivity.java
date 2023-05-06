package com.example.starshipobstaclecourse.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.starshipobstaclecourse.R;


public class MenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton menu_rb_normal;
    private RadioButton menu_rb_fast;
    private CheckBox sensors;
    private Button startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();

        startGame.setOnClickListener(v -> {
            double speed = 0.0;
            int checkedId = radioGroup.getCheckedRadioButtonId();

            if (checkedId == menu_rb_normal.getId()) {
                speed = 2;
            } else if (checkedId == menu_rb_fast.getId()) {
                speed = 1.2;
            }

            boolean useSensors = sensors.isChecked();

            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("speed", speed);
            intent.putExtra("useSensors", useSensors);
            startActivity(intent);
        });
    }

    private void findViews() {
        radioGroup = findViewById(R.id.radioGroup);
        menu_rb_normal = findViewById(R.id.normalRB);
        menu_rb_fast = findViewById(R.id.fastRB);
        sensors = findViewById(R.id.sensorCB);
        startGame = findViewById(R.id.startgameButton);
    }
}