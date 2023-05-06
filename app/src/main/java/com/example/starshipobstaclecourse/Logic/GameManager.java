package com.example.starshipobstaclecourse.Logic;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Random;

public class GameManager {

    private final int NO_COLLISION_SCORE = 1;

    private int score;
    private int[][] meteorPlaces=new int[4][5];

    private int[][] bitcoinPlaces = new int[4][5];
    private int spaceshipIndex;
    private int life;
    private int collision;

    private int coinsCollected;


    public GameManager(int life) {
        this.life = life;
        this.score = 0;
        this.collision = 0;
        this.spaceshipIndex=2;
        this.coinsCollected=0;
        resetMeteors();
        resetBitcoins();
    }
    public int getScore() {
        return score;
    }
    public void resetSpaceship(){
        spaceshipIndex=2;
    }
    public void resetMeteors(){
        for(int i=0;i<meteorPlaces.length;i++){
            for(int j=0;j<meteorPlaces[0].length;j++){
                meteorPlaces[i][j]=0;
            }
        }
    }
    public void resetBitcoins(){
        for(int i=0;i<bitcoinPlaces.length;i++){
            for(int j=0;j<bitcoinPlaces[0].length;j++){
                bitcoinPlaces[i][j]=0;
            }
        }
    }
    public void addMeteorAndBitcoin(){
        //find space for next meteor
        Random rand = new Random();
        int meteorIndex = rand.nextInt(meteorPlaces[0].length);

        for(int i=meteorPlaces.length-1;i>=1;i--){
            for(int j=0;j<meteorPlaces[0].length;j++){
                meteorPlaces[i][j] = meteorPlaces[i-1][j];
            }
        }

        for(int i=0;i<meteorPlaces[0].length;i++){
            meteorPlaces[0][i]=0;
        }
        meteorPlaces[0][meteorIndex]=1;

        //find space for new bitcoin which isn't the meteor place
        int bitcoinIndex=-1;
        while(bitcoinIndex<0 || bitcoinIndex==meteorIndex){
            bitcoinIndex=rand.nextInt(bitcoinPlaces[0].length*3);
        }
        for(int i=bitcoinPlaces.length-1;i>=1;i--){
            for(int j=0;j<bitcoinPlaces[0].length;j++){
                bitcoinPlaces[i][j] = bitcoinPlaces[i-1][j];
            }
        }

        for(int i=0;i<bitcoinPlaces[0].length;i++){
            bitcoinPlaces[0][i]=0;
        }
        if(bitcoinPlaces[0].length>bitcoinIndex)
        bitcoinPlaces[0][bitcoinIndex]=1;
    }
    public boolean checkMeteorCollision(Context context, Vibrator v){
        int lastLine = meteorPlaces.length-1;
        if(meteorPlaces[lastLine][spaceshipIndex]>0){
            this.collision++;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
            return true;
        }
        this.score+=NO_COLLISION_SCORE;

        checkBitcoinCollision();
        return false;
    }
    public void checkBitcoinCollision() {
        int lastLine = bitcoinPlaces.length - 1;
        if (bitcoinPlaces[lastLine][spaceshipIndex] > 0) {
            this.coinsCollected++;
        }
    }


    public int getCollision() {
        return collision;
    }


    public boolean isLose() {
        return life == collision;
    }

    public void moveSpaceship(int direction){
        if(spaceshipIndex==0 && direction ==-1)
            spaceshipIndex=0;
        else if(spaceshipIndex== meteorPlaces[0].length-1 && direction ==1)
            spaceshipIndex= meteorPlaces[0].length-1;
        else{
            spaceshipIndex+=direction;
        }

    }
    public int getSpaceshipIndex(){
        return this.spaceshipIndex;
    }
    public int[][] getMeteorPlaces(){
        return meteorPlaces;
    }
    public int[][] getBitcoinPlaces(){return bitcoinPlaces;}
    private void printMatrix(int[][] mat){


        // Loop through all rows
        for (int i = 0; i < mat.length; i++) {

            // Loop through all elements of current row
            for (int j = 0; j < mat[i].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }
    }
    public int getCoinsCollected(){
        return this.coinsCollected;
    }




}
