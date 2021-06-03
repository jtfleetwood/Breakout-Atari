package com.gamecodeschool.pong;

import android.graphics.RectF;

import java.util.ArrayList;

/**
 * New class to add functionality for grouping of blocks near top of screen. Contains attributes that assist with sizing
 * blocks based off screen resolution, and a 2D ArrayList that contains the actual blocks of RectF's. Had preference to
 * stick with 2D data structure.
 */
public class wallContainer {
    private ArrayList<ArrayList<RectF>> obstacleWall;
    private int nRowsObstacles = 3;
    private int nColsObstacles =9;
    private float oBrickWidth;
    private float oBrickHeight;
    private int mScreenX;
    private int mScreenY;

    /**
     * Parametrized constructor
     * @param sx - # of horizontal pixels
     * @param sy - # of vertical pixels
     *
     * In the constructor we are initializing attributes that set our block size, and then initializing our 2D ArrayList.
     * Using x, y position variables while looping/initialization to keep track of where we put the last block. The
     * initialization process of the 2D array consists of reading in an individual ArrayList and then adding that to our
     * overall ArrayList of ArrayLists. We will primarily use the 2D array for this class object in the actual game.
     */
    public wallContainer(int sx, int sy) {
        mScreenX = sx;
        mScreenY = sy;

        obstacleWall = new ArrayList<ArrayList<RectF>>();
        oBrickWidth = (float) (mScreenX / nColsObstacles);
        oBrickHeight = (float) (mScreenY / 3) / nRowsObstacles;
        int i = 0;
        int j = 0;
        // stores current position of next x pixel value to be used in assigning coordinates for each block.
        float xPos;
        // stores current position of next y pixel value to be used in assigning coordinates for each block.
        float yPos = 0;

        for (i = 0; i < nRowsObstacles; i++) {
            // Resetting x position to 0, since we start a new row.
            xPos = 0;
            ArrayList<RectF> a1 = new ArrayList<RectF>();
            for (j = 0; j < nColsObstacles; j++) {
                RectF temp = new RectF();
                temp.left = xPos;
                temp.right = xPos + oBrickWidth;
                temp.top = yPos;
                temp.bottom = yPos + oBrickHeight;
                // adding new RectFs to each ArrayList within our other ArrayList.
                a1.add(new RectF(temp));
                xPos += oBrickWidth + 15;
            }
            // Adding rows to our top ArrayList within 2D ArrayList.
            obstacleWall.add(new ArrayList<RectF>(a1));
            yPos += oBrickHeight + 15;
        }

    }

    /**
     * Uses parametrized constructor to create a new wallContainer object which has a newly initialized 2D ArrayList. Passed
     * to our 2D arraylist of RectF's in the game itself.
     */
    public void reset () {
        this.obstacleWall = new wallContainer(mScreenX, mScreenY).obstacleWall;

    }

    /**
     * Accessor for our 2D array list. Contains pretty substantial data for the game overall, so want the data and implementation
     * hidden.
     * @return returns the 2D array list within the current wallContainer object.
     */
    public ArrayList<ArrayList<RectF>> getWall() {
        return obstacleWall;
    }

}
