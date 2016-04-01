package com.example.android.wifidirect.discovery;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

class GameServer implements Runnable{
    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(4545);

            byte[] receiveData = new byte[10];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Log.d("SERVER", "Server Started " + serverSocket.getLocalAddress().getHostName() + ":4545");
            while (true) {
                serverSocket.receive(receivePacket);
                Log.d("AudioServer", "Received packet of size " + receivePacket.getLength() + ": " +
                        Arrays.toString(receivePacket.getData()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
