package com.metehan.myvpnservice;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by metehan on 3/29/2016.
 */
public class MyVpnService extends VpnService {
    private Thread mThread;
    private ParcelFileDescriptor mInterface;
    //a. Configure a builder for the interface.
    Builder builder = new Builder();

    // Services interface
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start a new session by creating a new thread.
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //a. Configure the TUN and get the interface.
                    mInterface = builder.setSession("MyVPNService")
                            .addAddress("192.168.0.1", 24)
                            .addDnsServer("8.8.8.8")
                            .addRoute("0.0.0.0", 0).establish();
                    //b. Packets to be sent are queued in this input stream.
                    FileInputStream in = new FileInputStream(
                            mInterface.getFileDescriptor());
                    //b. Packets received need to be written to this output stream.
                    FileOutputStream out = new FileOutputStream(
                            mInterface.getFileDescriptor());
                    //c. The UDP channel can be used to pass/get ip package to/from server
                    DatagramChannel tunnel = DatagramChannel.open();
                    // Connect to the server, localhost is used for demonstration only.
                    tunnel.connect(new InetSocketAddress("192.168.0.1", 8087));
                    //d. Protect this socket, so package send by it will not be feedback to the vpn service.
                    protect(tunnel.socket());
                    // Allocate the buffer for a single packet.
                    ByteBuffer packet = ByteBuffer.allocate(32767);
                    // We use a timer to determine the status of the tunnel. It
                    // works on both sides. A positive value means sending, and
                    // any other means receiving. We start with receiving.
                    int timer = 0;
                    //e. Use a loop to pass packets.
                    while (true) {
                        // Assume that we did not make any progress in this iteration.
                        boolean idle = true;
                        // Read the outgoing packet from the input stream.
                        int length = in.read(packet.array());
                        if (length > 0) {
                            DatagramPacket readablePacket = new DatagramPacket(packet.array(), packet.array().length);
                            Log.d("UDP-receiver", readablePacket.getLength()
                                    + " bytes of the actual packet received");
                            Log.d("UDP-receiver",  readablePacket.getAddress()
                                    + " is address.");
                            /*Log.d("UDP-receiver",  readablePacket.getSocketAddress()
                                    + " is Socket address"); */
                            Log.d("UDP-receiver",  readablePacket.getPort()
                                    + " is port");
                            // Write the outgoing packet to the tunnel.
                            packet.limit(length);
                            tunnel.write(packet);
                            packet.clear();
                            // There might be more outgoing packets.
                            idle = false;
                            // If we were receiving, switch to sending.
                            if (timer < 1) {
                                timer = 1;
                            }
                        }
                        //get packet with in
                        //put packet to tunnel
                        //get packet form tunnel
                        //return packet with out
                        //sleep is a must
                        Thread.sleep(100);
                    }

                } catch (Exception e) {
                    // Catch any exception
                    e.printStackTrace();
                } finally {
                    try {
                        if (mInterface != null) {
                            mInterface.close();
                            mInterface = null;
                        }
                    } catch (Exception e) {

                    }
                }
            }

        }, "MyVpnRunnable");

        //start the service
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mThread != null) {
            mThread.interrupt();
        }
        super.onDestroy();
    }
}