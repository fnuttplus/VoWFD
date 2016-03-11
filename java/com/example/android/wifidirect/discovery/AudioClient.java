package com.example.android.wifidirect.discovery;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AudioClient implements Runnable {
    private static final String TAG = "AudioClient";

    AudioRecord recorder;
    DatagramSocket socket;

    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

    private InetAddress destination;

    public AudioClient(InetAddress mAddress) {
        destination = mAddress;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            byte[] buffer = new byte[minBufSize];
            DatagramPacket packet;

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
            Log.d(TAG, "Recorder initialized: "+recorder);

            recorder.startRecording();

            while(status) {
                minBufSize = recorder.read(buffer, 0, buffer.length);
                if (minBufSize > 0) {
                    packet = new DatagramPacket(buffer, minBufSize, destination, 4545);
                    socket.send(packet);
                    Log.d(TAG, "Sent packet of size " + minBufSize);
                }
            }

        } catch(UnknownHostException e) {
            Log.e("VS", "UnknownHostException");
        } catch (IOException e) {
            recorder.release();
            e.printStackTrace();
            Log.e("VS", "IOException");
        }
    }
}
