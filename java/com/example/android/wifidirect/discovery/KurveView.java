package com.example.android.wifidirect.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class KurveView extends View {
    Kurve redKurve = new Kurve(300, 200, Color.RED, "Red Kurve");
    Kurve blueKurve = new Kurve(200,300, Color.BLUE, "Blue Kurve");
    Paint paint = new Paint();
    long prevTime;
    Bitmap mBitmap;
    private class Vector{
        public double X;
        public double Y;
        public Vector(double x, double y){
            X = x;
            Y = y;
        }
    }

    private class Kurve{
        public static final int DIR_LEFT = 1;
        public static final int DIR_RIGHT = 2;
        public static final int DIR_STRAIGHT = 0;
        int mColor;
        double mDirection; //direction in radians
        String mName;
        Vector mMovement;
        Vector mPosition;
        int mX, mY, mPreviousX, mPreviousY;
        public Kurve(float startX, float startY, int color, String name){
            mColor = color;
            mDirection = Math.PI/4;
            mName = name;
            mMovement = new Vector(Math.sin(mDirection)*15, Math.cos(mDirection)*15);
            mPosition = new Vector(startX, startY);
        }

        public void update(long dt, int turn) {
            if (turn == DIR_LEFT) {
                mDirection += Math.PI/10;
                mMovement.X = Math.sin(mDirection)*15;
                mMovement.Y = Math.cos(mDirection)*15;
            } else if (turn == DIR_RIGHT) {
                mDirection -= Math.PI/10;
                mMovement.X = Math.sin(mDirection)*15;
                mMovement.Y = Math.cos(mDirection)*15;
            }
            mPosition.X += mMovement.X*dt/100;
            mPosition.Y += mMovement.Y*dt/100;
            if (mPosition.X+5 > mScreenWidth) mPosition.X = 5;
            if (mPosition.Y+5 > mScreenHeight) mPosition.Y = 5;
            if (mPosition.X < 5) mPosition.X = mScreenWidth-5;
            if (mPosition.Y < 5) mPosition.Y = mScreenHeight-5;
            mPreviousX = mX;
            mPreviousY = mY;
            mX = (int)mPosition.X;
            mY = (int)mPosition.Y;

            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    if (mMovement.X < 0) {
                        if (mX - 5 + x >= mPreviousX - 5) {
                            if (mMovement.Y < 0){
                                if (mY - 5 + y >= mPreviousY - 5) continue;
                            } else {
                                if (mY - 5 + y <= mPreviousY + 5) continue;
                            }
                        }
                    } else {
                        if (mX - 5 + x <= mPreviousX + 5) {
                            if (mMovement.Y < 0) {
                                if (mY - 5 + y >= mPreviousY - 5) continue;
                            } else {
                                if (mY - 5 + y <= mPreviousY + 5) continue;
                            }
                        }
                    }

                    if (mBitmap.getPixel(mX - 5 + x, mY - 5 + y) != Color.BLACK) {
                        mColor = Color.GRAY;
                        return;
                    }

                    mBitmap.setPixel(mX - 5 + x, mY - 5 + y, mColor);
                }
            }
        }

    }

    private int mScreenHeight;
    private int mScreenWidth;

    public KurveView(Context context, int screenHeight, int screenWidth) {
        super(context);
        prevTime = System.currentTimeMillis();
        invalidate();
        mScreenHeight = screenHeight;
        mScreenWidth = screenWidth;
        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    }
    @Override
    protected void onDraw(Canvas canvas){

        int dt = (int) (System.currentTimeMillis() - prevTime);
        prevTime = System.currentTimeMillis();

        int turnRed = Kurve.DIR_STRAIGHT;
        if (drawQ3 && !drawQ1) turnRed = Kurve.DIR_LEFT;
        if (drawQ1 && !drawQ3) turnRed = Kurve.DIR_RIGHT;
        int turnBlue = Kurve.DIR_STRAIGHT;
        if (drawQ2 && !drawQ4) turnBlue = Kurve.DIR_LEFT;
        if (drawQ4 && !drawQ2) turnBlue = Kurve.DIR_RIGHT;

        redKurve.update(dt, turnRed);
        blueKurve.update(dt, turnBlue);

        canvas.drawBitmap(mBitmap, 0, 0, null);
        paint.setColor(Color.argb(20,150,200,200));
        if (drawQ2) canvas.drawRect(0,mScreenHeight/2, mScreenWidth/2, mScreenHeight, paint);
        if (drawQ4) canvas.drawRect(mScreenWidth/2, mScreenHeight/2, mScreenWidth, mScreenHeight, paint);
        paint.setColor(Color.argb(20,200,200,150));
        if (drawQ1) canvas.drawRect(0,0,mScreenWidth/2, mScreenHeight/2, paint);
        if (drawQ3) canvas.drawRect(mScreenWidth/2, 0, mScreenWidth, mScreenHeight/2, paint);

    	paint.setColor(Color.WHITE);
        paint.setTextSize(20.0f);
        canvas.drawText(Integer.toString(1000 / dt) + " FPS", 20, 20, paint);

        invalidate();
    }
boolean drawQ1, drawQ2, drawQ3, drawQ4;
    @Override
    public boolean onTouchEvent (MotionEvent e){
        int pointers = e.getPointerCount();
        float x,y;
        drawQ1 = drawQ2 = drawQ3 = drawQ4  = false;
        for (int i = 0; i < pointers; i++) {
            if (e.getActionMasked() == MotionEvent.ACTION_UP) continue;
            x = e.getX(i);
            y = e.getY(i);
            if (y < mScreenHeight / 2 && x < mScreenWidth / 2) {
                drawQ1 = true;
            } else if (y > mScreenHeight / 2 && x < mScreenWidth / 2) {
                drawQ2 = true;
            } else if (y < mScreenHeight / 2 && x > mScreenWidth / 2) {
                drawQ3 = true;
            } else if (y > mScreenHeight / 2 && x > mScreenWidth / 2) {
                drawQ4 = true;
            }
        }
        return true;
    }

}
