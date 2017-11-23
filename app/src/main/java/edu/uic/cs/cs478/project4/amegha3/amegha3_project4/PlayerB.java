/*
*   Author: Arbaaz Meghani
*   Description: This thread follows a purely win strategy where it attempts to constantly win except with placement.
*                   This doesn't attempt to block.
 */

//package
package edu.uic.cs.cs478.project4.amegha3.amegha3_project4;

//import statements
import android.os.Looper;
import java.util.Random;
import android.os.Handler;
import android.os.Message;

public class PlayerB implements Runnable {

    //instance variables
    private int numPieces = 0;
    private PositionData[] pieces = null;
    public static Handler playerBHandler;

    //keep track of rows
    int[][] personalStateBoard;

    //run method
    public void run() {
        Looper.prepare();
        createHandler();
        //init perosonal state board
        initPersonalStateBoard();
        Game.handlerInit++;
        Looper.loop();
    }

    /*
    *   Function: initialize personal state board to all 0
    *   Parameter: none
    *   Return: none
     */
    private void initPersonalStateBoard() {
        personalStateBoard = new int[Constants.WIDTH][Constants.HEIGHT];
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                personalStateBoard[i][j] = 0;
    }

    /*
    *   Function: create a handler
    *   Parameter: none
    *   Return: none
     */
    private void createHandler() {
        playerBHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case Constants.MADE_MOVE:
                        updatePersonalBoard(new PositionData(msg.arg1, msg.arg2, Constants.PLAYER_A_ID), (PositionData)msg.obj);
                        makeMove();
                        break;
                    default: break;
                }
            }
        };
    }

    /*
    *   Function: make a move
    *   Parameter: none
    *   Return: none
     */
    private void makeMove() {
        if(numPieces < 3) {
            if(pieces == null)
                pieces = new PositionData[Constants.NUM_PIECES];
            placePiece();
            return;
        }
        //get analysis information
        PositionData analysisData = analyzePersonalBoard();
        //find best piece to move based on analysis data
        int pieceNumber = findBestPieceToMove(analysisData);
        //find the best spot to move to based on data
        PositionData newPosition = findBestOpenSpot(analysisData);
        //update the personal board
        updatePersonalBoard(pieces[pieceNumber], newPosition);
        //move piece
        Game.movePiece(pieces[pieceNumber], newPosition);
    }

    /*
    *   Function: place a piece at a random location
    *   Parameter: none
    *   Return: none
     */
    private void placePiece() {
        pieces[numPieces] = getRandomPoint();
        Game.movePiece(new PositionData(-1,-1,Constants.PLAYER_B_ID), pieces[numPieces]);
        updatePersonalBoard(new PositionData(-1,-1,Constants.PLAYER_B_ID), pieces[numPieces]);
        numPieces++;
    }

    /*
    *   Function: update the personal board.  Empty spaces are 0, positions with this id are 1 and
    *                   positions with opponents id are -1
    *   Parameter: none
    *   Return: none
     */
    private void updatePersonalBoard(PositionData oldPos, PositionData newPos) {
        if(newPos == null)
            return;
        int oldX = oldPos.getPosX();
        int oldY = oldPos.getPosY();
        int newX = newPos.getPosX();
        int newY = newPos.getPosY();
        if(oldX != -1)
            personalStateBoard[oldX][oldY] = 0;
        if(newPos.getPlayerId() == Constants.PLAYER_B_ID)
            personalStateBoard[newX][newY] = 1;
        else
            personalStateBoard[newX][newY] = -1;
    }

    /*
    *   Function: get random point
    *   Parameter: none
    *   Return: position data of random point
     */
    private PositionData getRandomPoint() {
        int posX = new Random().nextInt(Constants.WIDTH);
        int posY = new Random().nextInt(Constants.HEIGHT);
        while(!isOpenSpace(posX, posY)) {
            posX = new Random().nextInt(Constants.WIDTH);
            posY = new Random().nextInt(Constants.HEIGHT);
        }
        return new PositionData(posX, posY, Constants.PLAYER_B_ID);
    }


    /*
    *   Function: check if position is open
    *   Parameter: none
    *   Return: true if open; else false
     */
    private boolean isOpenSpace(int posX, int posY) {
        return Game.getGameBoard()[posX][posY] == Constants.EMPTY;
    }

    /*
    *   Function: quit looper and clear queue
    *   Parameter: none
    *   Return: none
     */
    public static void quitLooper() {
        playerBHandler.removeCallbacksAndMessages(null);
        playerBHandler.getLooper().quit();
    }

    /*
    *   Function: find best piece to move by finding a piece in a location that is not
    *               best suited to win
    *   Parameter: the board analysis data
    *   Return: the piece number
     */
    private int findBestPieceToMove(PositionData boardAnalysis) {
        if(numPieces < 3)
            return -1;
        int rowOrCol = boardAnalysis.getPlayerId();
        int locationNum = boardAnalysis.getPosX();
        for(int i = 0; i < Constants.NUM_PIECES; i++) {
            if (rowOrCol == 3 && pieces[i].getPosX() != locationNum)
                return i;
            else if (rowOrCol == 4 && pieces[i].getPosY() != locationNum)
                return i;
        }

        return -1;

    }

    /*
    *   Function: find best open spot by looking at row or column which may have empty spot and good win potential
    *   Parameter: analysis data
    *   Return: the position to move to
     */
    private PositionData findBestOpenSpot(PositionData boardAnalysis) {
        int rowOrCol = boardAnalysis.getPlayerId();
        int locationNum = boardAnalysis.getPosX();
        for(int i = 0; i < Constants.WIDTH; i++) {
            if(rowOrCol == 3 && isOpenSpace(locationNum, i))
                return new PositionData(locationNum, i, Constants.PLAYER_B_ID);
            else if(rowOrCol == 4 && isOpenSpace(i, locationNum))
                return new PositionData(i, locationNum, Constants.PLAYER_B_ID);
        }
        return getRandomPoint();
    }

    /*
    *   Function: analyze the personal board and create analysis data.
    *               sum each row and column and store the max.  The possible max is 2
    *               a 2 depicts that there are 2 pieces of this thread in a row or column and
    *               an empty spot somewhere in that row or column.  a player id of 3 represents
    *               row and 4 represent column. if there isn't a 2; it uses the best it can find.
    *   Parameter: none
    *   Return: the analysis data
     */
    private PositionData analyzePersonalBoard() {
        PositionData maxPoint = new PositionData(-1, -1, -1);
        for(int i = 0; i < Constants.WIDTH; i++) {
            int countHoriz = 0;
            int countVert = 0;
            for (int j = 0; j < Constants.HEIGHT; j++) {
                countHoriz += personalStateBoard[i][j];
                countVert += personalStateBoard[j][i];
            }
            if(maxPoint.getPosY() < countHoriz) {
                maxPoint.setPosX(i);
                maxPoint.setPosY(countHoriz);
                maxPoint.setPlayerId(3);
                if(countHoriz == 2)
                    return maxPoint;
            }
            else if(maxPoint.getPosY() < countVert) {
                maxPoint.setPosX(i);
                maxPoint.setPosY(countVert);
                maxPoint.setPlayerId(4);
                if(countVert == 2)
                    return maxPoint;
            }
        }
        return maxPoint;
    }
}
