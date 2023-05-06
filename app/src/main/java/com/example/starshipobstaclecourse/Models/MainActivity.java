package com.example.starshipobstaclecourse.Models;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.starshipobstaclecourse.Logic.GameManager;
import com.example.starshipobstaclecourse.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    // UI elements
    private MaterialTextView main_LBL_score;
    private MaterialTextView main_LBL_coins;
    private ImageView[] main_IMG_meteors;
    private ImageView[] main_IMG_bitcoins;
    private ImageView[] main_IMG_spaceship;
    private ImageView rightArrow;
    private ImageView leftArrow;
    private ShapeableImageView[] main_IMG_hearts;

    private SensorManager sensorManager;

    private Sensor acceleroSensor;

    private double speed=2.0;

    private boolean screenIdle =true;

    private MediaPlayer crashSound;

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
            int delay= (int) (speed*1000);
            handler.postDelayed(this, delay); // Repeat every 0.8 second
        }
    };
    private GameManager gameManager;
    public Handler handler;
    private boolean gameOngoing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        // Retrieve the values from the Intent
        speed = getIntent().getDoubleExtra("speed", 2.0);
        boolean useSensors = getIntent().getBooleanExtra("useSensors", false);


        // Initialize UI elements
        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);

        // Set click listeners for arrow buttons
        setArrowsClickListeners();

        // Clear initial state of meteors and spaceship
        clearMeteors();
        clearBitcoins();
        clearSpaceship();

        // Start the refresh loop
        handler = new Handler();
        handler.postDelayed(refreshRunnable, 3000);


        //sensors

        if(useSensors){
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            if(sensorManager !=null){
                acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                if(acceleroSensor!=null){
                    sensorManager.registerListener(this,acceleroSensor,SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }

        crashSound = MediaPlayer.create(MainActivity.this,R.raw.explosion);
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
            if (gameManager.checkMeteorCollision(this, v)) {
                Log.d("MainActivity", "SpaceShip exploded"); // add log message
                Toast.makeText(getApplicationContext(), "SpaceShip exploded", Toast.LENGTH_SHORT).show();
                crashSound.start();
                crashDelay();


            } else {
                gameManager.addMeteorAndBitcoin();
                showMeteorRefresh(gameManager.getMeteorPlaces());

                showBitcoinRefresh(gameManager.getBitcoinPlaces());
            }

            if (gameManager.getCollision() != 0) {
                // Hide a heart if the spaceship has collided with a meteor
                main_IMG_hearts[main_IMG_hearts.length - gameManager.getCollision()].setVisibility(View.INVISIBLE);
            }

        }
        setScoreText();
        setCoinsCollected();
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
        main_LBL_coins=findViewById(R.id.main_LBL_coins);
        main_IMG_meteors = new ImageView[]{
                findViewById(R.id.meteor00),
                findViewById(R.id.meteor01),
                findViewById(R.id.meteor02),
                findViewById(R.id.meteor03),
                findViewById(R.id.meteor04),
                findViewById(R.id.meteor10),
                findViewById(R.id.meteor11),
                findViewById(R.id.meteor12),
                findViewById(R.id.meteor13),
                findViewById(R.id.meteor14),
                findViewById(R.id.meteor20),
                findViewById(R.id.meteor21),
                findViewById(R.id.meteor22),
                findViewById(R.id.meteor23),
                findViewById(R.id.meteor24),
                findViewById(R.id.meteor30),
                findViewById(R.id.meteor31),
                findViewById(R.id.meteor32),
                findViewById(R.id.meteor33),
                findViewById(R.id.meteor34)};
        main_IMG_hearts = new ShapeableImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)};
        main_IMG_spaceship = new ImageView[]{
                findViewById(R.id.spaceship0),
                findViewById(R.id.spaceship1),
                findViewById(R.id.spaceship2),
                findViewById(R.id.spaceship3),
                findViewById(R.id.spaceship4)
        };

        main_IMG_bitcoins = new ImageView[]{
                findViewById(R.id.bitcoin00),
                findViewById(R.id.bitcoin01),
                findViewById(R.id.bitcoin02),
                findViewById(R.id.bitcoin03),
                findViewById(R.id.bitcoin04),
                findViewById(R.id.bitcoin10),
                findViewById(R.id.bitcoin11),
                findViewById(R.id.bitcoin12),
                findViewById(R.id.bitcoin13),
                findViewById(R.id.bitcoin14),
                findViewById(R.id.bitcoin20),
                findViewById(R.id.bitcoin21),
                findViewById(R.id.bitcoin22),
                findViewById(R.id.bitcoin23),
                findViewById(R.id.bitcoin24),
                findViewById(R.id.bitcoin30),
                findViewById(R.id.bitcoin31),
                findViewById(R.id.bitcoin32),
                findViewById(R.id.bitcoin33),
                findViewById(R.id.bitcoin34)};
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
                    int index = i * 5 + j;
                    main_IMG_meteors[index].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void showBitcoinRefresh(int [][] bitcoinPlaces){
        clearBitcoins();
        for (int i = 0; i < bitcoinPlaces.length; i++) {
            for (int j = 0; j < bitcoinPlaces[0].length; j++) {
                if (bitcoinPlaces[i][j] == 1) {
                    int index = i * 5 + j;
                    main_IMG_bitcoins[index].setVisibility(View.VISIBLE);
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

    private void clearBitcoins(){
        for(int i=0;i<main_IMG_bitcoins.length;i++){
            main_IMG_bitcoins[i].setVisibility(View.INVISIBLE);
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

    private void setCoinsCollected(){main_LBL_coins.setText("" + gameManager.getCoinsCollected());}
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("meganometer", "X: " + sensorEvent.values[0] + ", Y: " + sensorEvent.values[1] + ", Z: " + sensorEvent.values[2]); // add log message
        if(sensorEvent.values[0] > 10 && screenIdle ){
            clicked("right");
            screenIdle=false;
        }
        else if(sensorEvent.values[0]<-10 && screenIdle) {
            clicked("");
            screenIdle=false;
        }
        else if(abs(sensorEvent.values[0])<5 && !screenIdle)
            screenIdle=true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void crashDelay(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameManager.resetMeteors();
                gameManager.getSpaceshipIndex();
            }
        }, 2000); // Delay in milliseconds
    }
}