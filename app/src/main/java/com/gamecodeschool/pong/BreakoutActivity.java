package com.gamecodeschool.pong;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;

/**
 * Same as PongGame.
 */

public class BreakoutActivity extends Activity {

    private BreakoutGame mBreakoutGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);

        mBreakoutGame = new BreakoutGame(this, size.x, size.y);

        setContentView(mBreakoutGame);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Starting threads.
        mBreakoutGame.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pausing threads.
        mBreakoutGame.pause();
    }

}
