package com.example.starshipobstaclecourse.Models;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.starshipobstaclecourse.R;
import com.google.android.material.textview.MaterialTextView;

public class ScoreActivity extends AppCompatActivity {

    public static final String KEY_SCORE = "KEY_SCORE";
    public static final String KEY_STATUS = "KEY_STATUS";

    private MaterialTextView score_LBL_score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score_LBL_score = findViewById(R.id.score_LBL_score);
        Intent previousIntent = getIntent();
        String status = previousIntent.getStringExtra(KEY_STATUS);
        int score = previousIntent.getIntExtra(KEY_SCORE,0);
        score_LBL_score.setText(status + "\n" + score);
    }
}