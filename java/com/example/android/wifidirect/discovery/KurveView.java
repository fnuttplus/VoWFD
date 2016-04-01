package com.example.android.wifidirect.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class KurveView extends View {
    Kurve redKurve = new Kurve(300, 200, Color.RED, "Red Kurve");
    Kurve blueKurve = new Kurve(200,300, Color.BLUE, "Blue Kurve");
    Kurve[] mPlayers;
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
        private boolean gap = false;
        private int gaptimer = 0;

        public Kurve(float startX, float startY, int color, String name){
            mColor = color;
            mDirection = Math.PI/4;
            mName = name;
            mMovement = new Vector(Math.sin(mDirection)*15, Math.cos(mDirection)*15);
            mPosition = new Vector(startX, startY);
        }

        /**
        @param dt time difference in ms
        @param turn turn direction
         */
        public void update(long dt, int turn) {

            gaptimer += dt;
            if (!gap) {
                if (gaptimer > 1500) {
                    gap = true;
                    gaptimer = 0;
                }
            } else {
                if (gaptimer > 100) {
                    gap = false;
                    gaptimer = 0;
                }
            }

            if (turn == DIR_LEFT) {
                mDirection += Math.PI*dt/1000;
                mMovement.X = Math.sin(mDirection)*15;
                mMovement.Y = Math.cos(mDirection)*15;
            } else if (turn == DIR_RIGHT) {
                mDirection -= Math.PI*dt/1000;
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

            if (gap) return;

            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    if (mMovement.X < 0) {
                        if (mX + x >= mPreviousX) {
                            if (mMovement.Y < 0){
                                if (mY + y >= mPreviousY) continue;
                            } else {
                                if (mY + y < mPreviousY + 10) continue;
                            }
                        }
                    } else {
                        if (mX + x < mPreviousX + 10) {
                            if (mMovement.Y < 0) {
                                if (mY + y >= mPreviousY) continue;
                            } else {
                                if (mY + y < mPreviousY + 10) continue;
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

    public KurveView(Context context, GameInfo gameInfo) {
        super(context);
        prevTime = System.currentTimeMillis();
        invalidate();
        mScreenHeight = gameInfo.ScreenHeight;
        mScreenWidth = gameInfo.ScreenWidth;
        mPlayers = new Kurve[gameInfo.Players];
        int[] colors = {Color.RED, Color.BLUE};
        for (int i = 0; i < gameInfo.Players; i++) {
            mPlayers[i] = new Kurve(0,0,colors[i%colors.length], "Kurve");
        }
        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    }
    @Override
    protected void onDraw(Canvas canvas){

        int dt = (int) (System.currentTimeMillis() - prevTime);
        prevTime = System.currentTimeMillis();

        int turnRed = Kurve.DIR_STRAIGHT;
        if ((pressRegions & 0x03) > 0 && (pressRegions & 0x30)==0) turnRed = Kurve.DIR_LEFT;
        if ((pressRegions & 0x30) > 0 && (pressRegions & 0x03)==0) turnRed = Kurve.DIR_RIGHT;
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
    byte pressRegions;
    NetworkPacket np = new NetworkPacket(3);
    GameClient client = new GameClient(null);

    @Override
    public boolean onTouchEvent (MotionEvent e){
        int pointers = e.getPointerCount();
        float x,y;
        drawQ1 = drawQ2 = drawQ3 = drawQ4  = false;
        pressRegions = 0;
        for (int i = 0; i < pointers; i++) {
            if ((e.getActionMasked() == MotionEvent.ACTION_POINTER_UP && e.getActionIndex() == i)
                || (e.getAction() == MotionEvent.ACTION_UP))
                continue;
            x = e.getX(i);
            y = e.getY(i);

            int h = mScreenHeight/4;
            int w = mScreenWidth/2;
            pressRegions = (byte)(
                    ((y<4*h && y>3*h && x<2*w && x>1*w)?1:0) << 7 |
                    ((y<3*h && y>2*h && x<2*w && x>1*w)?1:0) << 6 |
                    ((y<2*h && y>1*h && x<2*w && x>1*w)?1:0) << 5 |
                    ((y<1*h && y>0*h && x<2*w && x>1*w)?1:0) << 4 |
                    ((y<4*h && y>3*h && x<1*w && x>0*w)?1:0) << 3 |
                    ((y<3*h && y>2*h && x<1*w && x>0*w)?1:0) << 2 |
                    ((y<2*h && y>1*h && x<1*w && x>0*w)?1:0) << 1 |
                    ((y<1*h && y>0*h && x<1*w && x>0*w)?1:0));

            if (y < mScreenHeight / 2 && x < mScreenWidth / 2) {
                drawQ1 = true;
            } else if (y > mScreenHeight / 2 && x < mScreenWidth / 2) {
                drawQ2 = true;
            } else if (y < mScreenHeight / 2 && x > mScreenWidth / 2) {
                drawQ3 = true;
            } else if (y > mScreenHeight / 2 && x > mScreenWidth / 2) {
                drawQ4 = true;
            }
            //client.Send(np.getMarshall(pressRegions));

            Log.d("TOUCH", Arrays.toString(np.getMarshall(pressRegions)));
        }
        return true;
    }

    private class GameClient {
        DatagramSocket socket;
        InetAddress mAddress;

        public GameClient(InetAddress serverAddress) {
            try {
                socket = new DatagramSocket();
                mAddress = serverAddress;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public void Send(byte[] data){
            DatagramPacket packet = new DatagramPacket(data, data.length, mAddress, 4545);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class NetworkPacket {
        private short Counter;
        private byte PlayerID;

        public NetworkPacket(int playerID){
            Counter = 0;
            PlayerID = (byte)playerID;
        }
        public byte[] getMarshall(byte  action) {
            Counter++;
            byte[] marshall = new byte[4];
            marshall[1] = (byte)(Counter & 0xff);
            marshall[0] = (byte)((Counter >> 8) & 0xff);
            marshall[2] = PlayerID;
            marshall[3] = action;

            return marshall;
        }
    }
}
