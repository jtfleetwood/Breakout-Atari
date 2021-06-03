package com.gamecodeschool.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Not going to provide much comments for the class. Same concept overall as the PongGame. This class is of type view
 * due to inheriting from SurfaceView and implements the Runnable interface so we can use threads for our game loop. Only
 * major change would be the addition of the 2D array list that contains information on every single block within our
 * obstacle container.
 *
 * Also made some minor adjustments to color, score keeping, score counting, and game/user interaction.
 */
public class BreakoutGame extends SurfaceView implements Runnable {
    private final boolean DEBUGGING = false;
    private final SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private final Paint mPaint;
    private long mFPS;
    private final int MILLIS_IN_SECOND = 1000;
    private final int mScreenX;
    private final int mScreenY;
    private final int mFontSize;
    private final int mFontMargin;
    private int mScore;
    private int mLives;
    private Thread mGameThread = null;
    // Volatile makes it safe to access variable from inside and outside of the thread.
    private volatile boolean mPlaying;
    private boolean mPaused = true;
    private Paddle mPaddle;
    private Ball mBall;
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;
    private int r = 10;
    private int b = 30;
    private int g = 50;
    private int highScore = 0;
    private Boolean won = false;
    private Boolean lost = false;
    private Boolean justStarted = true;
    private wallContainer wall;
    private ArrayList<ArrayList<RectF>> wallComponents;
    private static int numGames = 0;

    /**
     * Parametrized constructor.
     * Newly initializing our wallContainer object and its 2D array of blocks using an accessor.
     * @param context - Gives our object of type view context about the current activity, because views live within context (android lifecycle).
     * @param x - x value of pixels.
     * @param y - y value of pixels.
     */
    public BreakoutGame(Context context, int x, int y){
        super(context);
        mScreenX = x;
        mScreenY = y;
        wall = new wallContainer(x, y);
        wallComponents = wall.getWall();
        mFontSize = mScreenY / 20;
        mFontMargin = mScreenX / 75;

        mOurHolder = getHolder();
        mPaint = new Paint();
        mBall = new Ball(mScreenX, mScreenY);
        mPaddle = new Paddle(mScreenX, mScreenY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            mSP = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build();

        }

        else {
            Log.d("Error:", "Did not have updated version.\n");
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {

            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("bop.ogg");
            mBopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("miss.ogg");
            mMissID = mSP.load(descriptor, 0);

        }

        catch (IOException e) {
            Log.d("Error", "Failed to load sound files.");
        }


        startNewGame();

    }

    /**
     * Called whenever a new game is started or when the game is first opened.
     *
     * Only thing that changes below from Pong is the usage of our new objects. Basically just resetting our wallContainer
     * object which resets the 2D arraylist and then passing it back into our 2D object within this class. Could have kept the 2D array
     * within the object solely and used accessors/setters to remove/draw objects but was short on time.
     *
     * Also using some new bool variables as we are displaying feedback messages to the user based off the outcome of their game.
     */


    public void startNewGame() {
        mScore = 0;
        mLives = 3;

        mBall.reset(mScreenX, mScreenY);
        wall.reset();
        wallComponents =  wall.getWall();

        if (numGames == 0) {
            justStarted = true;
        }

        else {
            justStarted = false;
        }

        numGames++;
    }

    /**
     * Draw class is drastically changed from our last PongGame. This time around we are using our mPaused variable
     * which is true whenever the user hasn't touched the screen upon a new game to display our feedback messages. We then
     * also use this bool variable to determine what to draw (feedback vs. objects).
     *
     * Also messed around with alternating colors of the paddle/ball as it moves around the screen.
     */

    private void draw() {
        // Validates that area of memory that we want to manipulate to represent our frame of drawing is available.
        // Drawing/processing takes place asynchronously with the code that detects player input and OS messages.
        // Code executing 60 times a second, and we need to confirm that we have access to the memory before we access.

        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas();

            if (mPaused) {

                if (justStarted) {
                    mPaint.setTextSize((float)(mScreenY / 4.5));
                    mPaint.setColor(Color.argb(255, 0, 255, 0));
                    mCanvas.drawText("Let's get started!" , 0, mScreenY / 2, mPaint);
                }

                if (lost) {
                    mCanvas.drawColor(Color.argb(255, 0, 0, 0));
                    mPaint.setTextSize((float)(mScreenY / 4.5));
                    mPaint.setColor(Color.argb(255, 255, 0, 0));
                    mCanvas.drawText("LOSER!!!" , mScreenX / 4, mScreenY / 2, mPaint);
                }

                if (won) {
                    mCanvas.drawColor(Color.argb(255, 0, 0, 0));
                    mPaint.setTextSize((float)(mScreenY / 4.5));
                    mPaint.setColor(Color.argb(255, 0, 0, 255));
                    mCanvas.drawText("WOW! WINNER!!" , 0, mScreenY / 2, mPaint);
                }

            }

            else {
                mCanvas.drawColor(Color.argb(255, 0, 0, 0));

                mPaint.setTextSize(mFontSize);


                if (mBall.getRect().top < (mScreenX / 2)) {
                    r += 5;
                    g -= 5;
                    b += 7.5;
                }

                if (mBall.getRect().bottom > (mScreenX / 2)) {
                    r -= 5;
                    g += 5;
                    b -= 7.5;
                }

                mPaint.setColor(Color.argb(255, r, g, b));
                mCanvas.drawRect(mBall.getRect(), mPaint);
                mCanvas.drawRect(mPaddle.getRect(), mPaint);


                for (int i = 0; i < wallComponents.size(); i++) {
                    if (i == 0) {
                        mPaint.setColor(Color.argb(255, 127, 0, 255));
                    }

                    if (i == 1) {
                        mPaint.setColor(Color.argb(255, 255, 0, 0));
                    }

                    if (i == 2) {
                        mPaint.setColor(Color.argb(255, 0, 255, 0));
                    }

                    for (int j = 0; j < wallComponents.get(i).size(); j++) {
                        mCanvas.drawRect(wallComponents.get(i).get(j), mPaint);
                    }
                }

                mPaint.setColor(Color.argb(255, 255, 255, 255));

                mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, mFontMargin, mFontSize, mPaint);
                mCanvas.drawText("High Score: " + highScore, mFontMargin, 125, mPaint);

                if (DEBUGGING){
                    printDebuggingText();
                }
            }


            // Lock the canvas (graphics memory) ready to draw.
            // Ensures that while we are accessing the memory here, no other code can access it.

            // Frees up the memory to be accessed again, and posts the new canvas.
            // Happens every single frame of animation.
            mOurHolder.unlockCanvasAndPost(mCanvas);

        }
    }


    /**
     * Same as PongGame. Didn't really use.
     */


    private void printDebuggingText() {
        int debugSize = mFontSize / 2;
        int debugStart = 150;

        mPaint.setTextSize(debugSize);

        mCanvas.drawText("FPS: " + mFPS, 25, debugStart - 50, mPaint);

    }

    /**
     * Unchanged from pong game, same total idea. Runs our game loop through implementing the Runnable interface.
     */

    @Override
    public void run() {
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            if (!mPaused){
                update();
                detectCollisions();
            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;

            if (timeThisFrame > 0) {
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    /**
     * Unchanged. Didn't need to update wallObstacles because they are not moving around across the screen. Could have made
     * an update method within the wallObstacles class with different approaches but it would require transferring some data
     * from either class. Ball -> wallObstacle, wallObstacle->PongGame.
     */

    private void update() {
        mBall.update(mFPS);
        mPaddle.update(mFPS);
    }

    /**
     * Detect collisions just adds in functionality for the blocks existing. Overall, same functionality as the pong game.
     * If one of the blocks intersects with the ball, then we are removing that element within the 2D arraylist.
     *
     * Was curious to see how this process would work with a c-type array. Initial plan was to set whichever object was
     * touched to null, and then use some varying methods to decide when to end the game if player won.
     */

    private void detectCollisions() {

        if(RectF.intersects(mPaddle.getRect(), mBall.getRect())) {
            mBall.batBounce(mPaddle.getRect());
            mBall.increaseVelocity();
            mSP.play(mBeepID, 1, 1, 0, 0, 1);
        }

        if (mBall.getRect().bottom > mScreenY) {
            mBall.reverseYVelocity();
            mLives--;
            mSP.play(mMissID, 1, 1, 0, 0, 1);

            if (mLives == 0) {
                if (mScore > highScore) {
                    highScore = mScore;
                }
                lost = true;
                won = false;
                mPaused = true;
                startNewGame();
            }
        }

        if (mBall.getRect().top < 0) {
            mBall.reverseYVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
        }

        if (mBall.getRect().left < 0) {
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }

        if (mBall.getRect().right > mScreenX) {
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }

        for (int i = 0; i < wallComponents.size(); i++) {
            for (int j = 0; j < wallComponents.get(i).size(); j++) {
                if (RectF.intersects(mBall.getRect(), wallComponents.get(i).get(j))) {
                    mBall.batBounce(wallComponents.get(i).get(j));
                    wallComponents.get(i).remove(j);
                    if (i == 2) {
                        mScore++;
                    }

                    else if (i == 1) {
                        mScore += 2;
                    }

                    else if (i == 0) {
                        mScore += 3;
                    }

                }
            }
        }

        won = isWin(wallComponents);
        if (won) {
            lost = false;
            won = true;
            mPaused = true;
            startNewGame();
        }

    }

    /**
     * Below function just simply loops through the Arraylist holding the arraylists, and checking if each one is empty
     * before deciding to end the game.
     */

    public Boolean isWin(ArrayList<ArrayList<RectF>> temp) {
        for (int i = 0; i < temp.size(); i++) {
            if (!temp.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Same as pongGame.
     */
    public void pause() {
        mPlaying = false;


        try {
            // stopping the thread.
            mGameThread.join();
        }

        catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    /**
     * Same as pongGame.
     */
    public void resume() {
        mPlaying = true;

        mGameThread = new Thread(this);

        mGameThread.start();
    }

    /**
     * Overridden method within the view class that detects user interactions with our surface (current view).
     *
     * This method, based off the location and nature in which the user touches the screen, indicates whether the bat
     * needs to be updated left/right or not moved.
     * @see Paddle#SetMovementState(int)
     * @param motionEvent Is the actual event of the user touching the screen. Contains information about the touch, such
     * as where it happened. We can filter the information in this variable through bitwise comparison to get the
     * information we want.
     * @return We return true to indicate that a touch occurred.
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPaused = false;

                if (motionEvent.getX() > mScreenX / 2) {
                    mPaddle.SetMovementState(mPaddle.RIGHT);
                }

                else {
                    mPaddle.SetMovementState(mPaddle.LEFT);
                }

                break;

            case MotionEvent.ACTION_UP:
                mPaddle.SetMovementState(mPaddle.STOPPED);

                break;
        }

        return true;
    }


}
