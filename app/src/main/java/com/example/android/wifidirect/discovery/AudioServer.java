package com.example.android.wifidirect.discovery;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by clindest on 2016-03-10.
 */
public class AudioServer implements Runnable {
    DatagramSocket serverSocket;
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(4545);
            byte[] receiveData = new byte[minBufSize];

            AudioTrack audioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, minBufSize, AudioTrack.MODE_STREAM);
            audioTrack.play();
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);
                Log.d("AudioServer","Received packet of size "+receivePacket.getLength());
                audioTrack.write(receivePacket.getData(), 0, receivePacket.getLength());
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (serverSocket!=null && !serverSocket.isClosed())
                serverSocket.close();
        }
    }
}
