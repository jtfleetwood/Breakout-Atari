package com.gamecodeschool.pong;

import android.graphics.RectF;

/**
 * Same as PongGame.
 */

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;
    private float mScreenY;
    private float mScreenX;


    public Ball(int screenX, int screenY) {
        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;
        mScreenY = screenY;
        mScreenX = screenX;
        mRect = new RectF();
    }


    public RectF getRect() {
        return mRect;
    }


    void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }


    void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }


    void update(long fps) {
        if (mRect.left < 0) {
            mRect.left = 0;
        }

        else if (mRect.right > mScreenX) {
            mRect.right = mScreenX;
        }

        else if (mRect.bottom > mScreenY) {
            mRect.bottom = mScreenY;
        }

        else if (mRect.top < 0) {
            mRect.top = 0;
        }

        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;


    }

    /**
     * Updated the reset method to start the ball position somewhere a little above the bottom of the screen, so it does
     * not interfere with the paddle or blocks.
     * @param x - x pixels.
     * @param y - y pixels.
     */

    void reset(int x, int y) {
        mRect.left = x / 2;
        mRect.bottom = y - 150;
        mRect.top = mRect.bottom - mBallHeight;
        mRect.right = (x / 2) + mBallWidth;


        mYVelocity = -(y / 3);
        mXVelocity = (x / 2);
    }


    void increaseVelocity(){
        mXVelocity = mXVelocity * 1.1f;

        mYVelocity = mYVelocity * 1.1f;

    }


    void batBounce(RectF batPosition) {
        float batCenter = batPosition.left + (batPosition.width() / 2);
        float ballCenter = mRect.left + (mBallWidth / 2);

        float relativeIntersect = (batCenter - ballCenter);

        if (relativeIntersect < 0) {
            mXVelocity = Math.abs(mXVelocity);
        }

        else {
            mXVelocity = -Math.abs(mXVelocity);
        }

        reverseYVelocity();
    }

}
