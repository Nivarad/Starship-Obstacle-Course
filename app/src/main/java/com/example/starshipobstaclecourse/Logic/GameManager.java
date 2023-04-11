package com.example.starshipobstaclecourse.Logic;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Random;

public class GameManager {

    private final int NO_COLLISION_SCORE = 1;

    private int score;
    private int[][] meteorPlaces=new int[4][3];
    private int spaceshipIndex;
    private int life;
    private int collision;


    public GameManager(int life) {
        this.life = life;
        this.score = 0;
        this.collision = 0;
        this.spaceshipIndex=1;
        resetMeteors();
    }
    public int getScore() {
        return score;
    }
    public void resetSpaceship(){
        spaceshipIndex=1;
    }
    public void resetMeteors(){
        for(int i=0;i<meteorPlaces.length;i++){
            for(int j=0;j<meteorPlaces[0].length;j++){
                meteorPlaces[i][j]=0;
            }
        }
    }
    public void addMeteor(){
        Random rand = new Random();
        int meteorIndex = rand.nextInt(3);

        for(int i=meteorPlaces.length-1;i>=1;i--){
            for(int j=0;j<meteorPlaces[0].length;j++){
                meteorPlaces[i][j] = meteorPlaces[i-1][j];
            }
        }

        for(int i=0;i<meteorPlaces[0].length;i++){
            meteorPlaces[0][i]=0;
        }
        meteorPlaces[0][meteorIndex]=1;
//        printMatrix(meteorPlaces);
//        System.out.println("\n\n");
    }
    public boolean checkCollision(Context context, Vibrator v){
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
        return false;
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
        else if(spaceshipIndex==2 && direction ==1)
            spaceshipIndex=2;
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


}
