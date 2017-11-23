/*
*   Author: Arbaaz Meghani
*   Description: This is Player A thread.  The strategy this thread uses is a random strategy.
*                   It hopes to win by luck.
 */

//package
package edu.uic.cs.cs478.project4.amegha3.amegha3_project4;

//import statement
import android.os.Looper;

import java.util.Random;
import android.os.Handler;
import android.os.Message;


public class PlayerA implements Runnable {

    //instance variables
    private int numPieces = 0;
    private PositionData[] pieces = null;
    public static Handler playerAHandler;

    //run method
    public void run() {
        //prepare loop
        Looper.prepare();
        //crate handler
        createHandler();
        //increment handler variable
        Game.handlerInit++;
        //start looping
        Looper.loop();
    }

    /*
    *   Function: create handler
    *   Parameters: none
    *   Return: none
     */
    private void createHandler() {
        playerAHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case Constants.MADE_MOVE:
                        makeMove();
                        break;
                    default: break;
                }
            }
        };
    }

    /*
    *   Function: make a move
    *   Parameters: none
    *   Return: none
     */
    private void makeMove() {
        //if not all pieces placed, place a new piece
        if(numPieces < 3) {
            if(pieces == null)
                pieces = new PositionData[Constants.NUM_PIECES];
            placePiece();
            return;
        }
        //move a random piece to a random location
        int pieceNumber = getRandomPiece();
        PositionData randomPoint = getRandomPoint();
        //move the piece
        Game.movePiece(pieces[pieceNumber], randomPoint);
    }

    /*
    *   Function: Place a piece at a random point
    *   Parameters: none
    *   Return: none
     */
    private void placePiece() {
        pieces[numPieces] = getRandomPoint();
        Game.movePiece(new PositionData(-1,-1,Constants.PLAYER_A_ID), pieces[numPieces]);
        numPieces++;
    }

    /*
    *   Function: get a random piece
    *   Parameters: none
    *   Return: random piece number
     */
    private int getRandomPiece() {
        return new Random().nextInt(Constants.NUM_PIECES);
    }

    /*
    *   Function: get a random position
    *   Parameters: none
    *   Return: a random point
     */
    private PositionData getRandomPoint() {
        int posX = new Random().nextInt(Constants.WIDTH);
        int posY = new Random().nextInt(Constants.HEIGHT);
        //make sure point is open
        while(!isOpenSpace(posX, posY)) {
            posX = new Random().nextInt(Constants.WIDTH);
            posY = new Random().nextInt(Constants.HEIGHT);
        }
        return new PositionData(posX, posY, Constants.PLAYER_A_ID);
    }


    /*
    *   Function: check if space is open
    *   Parameters: the position of the space
    *   Return: true if open; else false
     */
    private boolean isOpenSpace(int posX, int posY) {
        return Game.getGameBoard()[posX][posY] == Constants.EMPTY;
    }

    /*
    *   Function: quit looper and clear queue
    *   Parameters: none
    *   Return: none
     */
    public static void quitLooper() {
        playerAHandler.removeCallbacksAndMessages(null);
        playerAHandler.getLooper().quit();
    }
}
