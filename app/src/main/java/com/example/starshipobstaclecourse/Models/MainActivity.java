package com.example.starshipobstaclecourse.Models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.starshipobstaclecourse.Logic.GameManager;
import com.example.starshipobstaclecourse.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private MaterialTextView main_LBL_score;
    private ImageView[] main_IMG_meteors;
    private ImageView[] main_IMG_spaceship;
    private ImageView rightArrow;
    private ImageView leftArrow;
    private ShapeableImageView[] main_IMG_hearts;

    // Refresh handler
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameOngoing) {
                // Stop the handler if the game is over
                handler.removeCallbacksAndMessages(null);
                return;
            }
            refreshUI();
            handler.postDelayed(this, 800); // Repeat every 0.8 second
        }
    };
    private GameManager gameManager;
    public Handler handler;
    private boolean gameOngoing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);

        // Set click listeners for arrow buttons
        setArrowsClickListeners();

        // Clear initial state of meteors and spaceship
        clearMeteors();
        clearSpaceship();

        // Start the refresh loop
        handler = new Handler();
        handler.postDelayed(refreshRunnable, 1000);
    }

    // Refresh UI elements
    private void refreshUI() {
        if (gameManager.isLose()) {
            // If the game is over, stop the handler and open the score screen
            handler.removeCallbacksAndMessages(null);
            openScoreScreen("Game Over!", gameManager.getScore());
            gameOngoing = false; // Set the flag to false to stop the handler from running
        } else {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (gameManager.checkCollision(this, v)) {
                Log.d("MainActivity", "SpaceShip exploded"); // add log message
                Toast.makeText(getApplicationContext(), "SpaceShip exploded", Toast.LENGTH_SHORT).show();

                gameManager.resetMeteors();
                gameManager.getSpaceshipIndex();
            } else {
                gameManager.addMeteor();
                showMeteorRefresh(gameManager.getMeteorPlaces());
            }

            if (gameManager.getCollision() != 0) {
                // Hide a heart if the spaceship has collided with a meteor
                main_IMG_hearts[main_IMG_hearts.length - gameManager.getCollision()].setVisibility(View.INVISIBLE);
            }
        }
        setScoreText();
    }

    // Open score screen
    private void openScoreScreen(String status, int score) {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(ScoreActivity.KEY_SCORE, score);
        intent.putExtra(ScoreActivity.KEY_STATUS, status);
        startActivity(intent);
        finish();
    }

    // Move spaceship in the clicked direction
    private void clicked(String tag) {
        int direction = tag.equals("right") ? 1 : -1;
        int layoutDirection = getResources().getConfiguration().getLayoutDirection();
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            direction = -direction; // reverse direction in RTL layout
        }
        gameManager.moveSpaceship(direction);
        showSpaceshipRefresh(gameManager.getSpaceshipIndex());
    }

    // Set click listeners for arrow buttons
    private void setArrowsClickListeners() {
        rightArrow.setOnClickListener(iv -> clicked(iv.getTag().toString()));
        leftArrow.setOnClickListener(iv -> clicked(iv.getTag().toString()));
    }

    // Find UI elements by ID
    private void findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_IMG_meteors = new ImageView[]{
                findViewById(R.id.meteor00),
                findViewById(R.id.meteor01),
                findViewById(R.id.meteor02),
                findViewById(R.id.meteor10),
                findViewById(R.id.meteor11),
                findViewById(R.id.meteor12),
                findViewById(R.id.meteor20),
                findViewById(R.id.meteor21),
                findViewById(R.id.meteor22),
                findViewById(R.id.meteor30),
                findViewById(R.id.meteor31),
                findViewById(R.id.meteor32)};
        main_IMG_hearts = new ShapeableImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)};
        main_IMG_spaceship = new ImageView[]{
                findViewById(R.id.spaceship0),
                findViewById(R.id.spaceship1),
                findViewById(R.id.spaceship2)
        };
        rightArrow = findViewById(R.id.rightArrow);
        leftArrow = findViewById(R.id.leftArrow);
    }

    // Show the spaceship at the given index and hide the others
    public void showSpaceshipRefresh(int spaceShipIndex) {
        for (int i = 0; i < main_IMG_spaceship.length; i++) {
            main_IMG_spaceship[i].setVisibility(View.INVISIBLE);
        }
        main_IMG_spaceship[spaceShipIndex].setVisibility(View.VISIBLE);
    }

    // Show meteors in their respective places
    public void showMeteorRefresh(int[][] meteorPlaces) {
        clearMeteors();
        for (int i = 0; i < meteorPlaces.length; i++) {
            for (int j = 0; j < meteorPlaces[0].length; j++) {
                if (meteorPlaces[i][j] == 1) {
                    int index = i * 3 + j;
                    main_IMG_meteors[index].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Hide all meteors
    private void clearMeteors() {
        for (int i = 0; i < main_IMG_meteors.length; i++) {
            main_IMG_meteors[i].setVisibility(View.INVISIBLE);
        }
    }

    // Set the initial state of the spaceship
    private void clearSpaceship() {
        for (int i = 0; i < main_IMG_spaceship.length; i++) {
            main_IMG_spaceship[i].setVisibility(View.INVISIBLE);
        }
        main_IMG_spaceship[1].setVisibility(View.VISIBLE);
    }

    // Update the score text
    private void setScoreText() {
        main_LBL_score.setText("" + gameManager.getScore());
    }

}