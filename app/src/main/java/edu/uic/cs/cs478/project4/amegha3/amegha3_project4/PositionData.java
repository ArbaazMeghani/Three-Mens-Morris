/*
*   Author: Arbaaz Meghani
*   Description: This class stores position data.  The x and y position of pieces and the id of the piece.
 */

//package
package edu.uic.cs.cs478.project4.amegha3.amegha3_project4;

public class PositionData {

    //instance variables
    private int posX;
    private int posY;
    private int playerId;

    //constructor
    public PositionData(int x, int y, int playerId) {
        //store variables
        posX = x;
        posY = y;
        this.playerId = playerId;
    }

    /*
    *   Function: get x position
    *   Parameters: none
    *   Return: x position
     */
    public int getPosX() {
        return posX;
    }

    /*
    *   Function: set x position
    *   Parameters: the x position
    *   Return: none
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /*
    *   Function: get y position
    *   Parameters: none
    *   Return: y position
     */
    public int getPosY() {
        return posY;
    }

    /*
    *   Function: set the y position
    *   Parameters: the y position
    *   Return: none
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /*
    *   Function: get the player id
    *   Parameters: none
    *   Return: player id
     */
    public int getPlayerId() {
        return playerId;
    }

    /*
    *   Function: set the player id
    *   Parameters: the player id
    *   Return: none
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
