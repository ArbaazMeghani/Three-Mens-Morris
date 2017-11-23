/*
*   Author: Arbaaz Meghani
*   Description: This class handles much of the game logic of the application.  The shared methods are listed here
*                   and keeps track of some shared variables.
 */

//package
package edu.uic.cs.cs478.project4.amegha3.amegha3_project4;

//import statements
import android.os.Handler;
import android.os.Message;

public class Game {

    //instance variables
    private static int[][] gameBoard;
    public static int handlerInit;
    private static Handler mainHandler;

    //constructor
    public Game(Handler mainHandler) {
        //set handler variable to 0
        handlerInit = 0;
        //init the game board
        initGameBoard();
        //store main handler
        this.mainHandler = mainHandler;
    }

    /*
    *   Function: set all points on gameboard to empty
    *   Parameters: none
    *   Return: none
     */
    private void initGameBoard() {
        gameBoard = new int[Constants.WIDTH][Constants.HEIGHT];
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                gameBoard[i][j] = Constants.EMPTY;
    }

    /*
    *   Function: move a piece
    *   Parameters: old position of piece and new position
    *   Return: none
     */
    public synchronized static void movePiece(PositionData oldPosition, PositionData newPosition) {
        //pause thread so user can view move
        pauseThread();

        //get position values
        int oldX = oldPosition.getPosX();
        int oldY = oldPosition.getPosY();

        int newX = newPosition.getPosX();
        int newY = newPosition.getPosY();

        //check if new placement or not
        if(oldX != -1)
            gameBoard[oldX][oldY] = Constants.EMPTY;

        //set playerid at new location
        gameBoard[newX][newY] = newPosition.getPlayerId();

        //store the position into the point of the piece
        oldPosition.setPosX(newX);
        oldPosition.setPosY(newY);

        //post move
        Message msg = mainHandler.obtainMessage(Constants.MADE_MOVE, oldX, oldY, oldPosition);
        mainHandler.sendMessage(msg);
    }

    /*
    *   Function: return the game board
    *   Parameters: none
    *   Return: gameboard
     */
    public synchronized static int[][] getGameBoard() {
        return gameBoard;
    }

    /*
    *   Function: pause the thread for 1s
    *   Parameters: none
    *   Return: none
     */
    public static void pauseThread() {
        try{
            Thread.sleep(1000);
        }catch(InterruptedException i) {

        }
    }
}
