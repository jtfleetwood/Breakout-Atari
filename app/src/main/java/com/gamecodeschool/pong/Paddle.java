package com.gamecodeschool.pong;

import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Same as PongGame.
 */

public class Paddle {

    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int mScreenX;

    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    private int mBatMoving = STOPPED;


    public Paddle(int sx, int sy) {
        mScreenX = sx;

        mLength = mScreenX / 8;

        float height = sy / 40;

        mXCoord = mScreenX / 2;

        float mYCoord = sy - height;

        mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + height);

        mBatSpeed = (float)(mScreenX * 1.5);

    }


    public RectF getRect() {
        return mRect;
    }

    void SetMovementState(int state) {
        mBatMoving = state;
    }


    public void update(long fps) {
        if (mBatMoving == LEFT) {
            mXCoord = mXCoord - (mBatSpeed / fps);
        }

        if (mBatMoving == RIGHT) {
            mXCoord = mXCoord + (mBatSpeed / fps);
        }

        if (mXCoord < 0) {
            mXCoord = 0;
        }

        else if (mXCoord + mLength > mScreenX) {
            mXCoord = mScreenX - mLength;
        }

        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
