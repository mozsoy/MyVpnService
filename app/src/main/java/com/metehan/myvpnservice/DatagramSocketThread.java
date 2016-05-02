package com.metehan.myvpnservice;

import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by metehan on 4/26/2016.
 */
public class DatagramSocketThread extends Thread{

    DatagramSocket mDatagramSocket;
    DatagramPacket mDatagramPacket;
    boolean socketAlive = false;
    @Override
    public void run() {
        try {
            mDatagramSocket = new DatagramSocket(new InetSocketAddress("127.0.0.1",8087));
            byte[] buffer = new byte[32767];
            mDatagramPacket = new DatagramPacket(buffer, buffer.length);
            socketAlive = true;

            while(socketAlive) {
            mDatagramPacket.setLength(buffer.length);
            mDatagramSocket.receive(mDatagramPacket);
                System.out.println("DatagramSocket at 8087 received packet from VpnService");
                System.out.println("Address: " + mDatagramPacket.getAddress() + " Port: " + mDatagramPacket.getPort());
                System.out.println("Length of data: " + mDatagramPacket.getData().length);

                TCPPacket tcpPacket = new TCPPacket(0, mDatagramPacket.getData());

                System.out.println("Sending from socket -> TCP Source port: " + tcpPacket.getSourcePort());
                System.out.println("Sending from socket -> TCP Destination port: " + tcpPacket.getDestinationPort());
                System.out.println("Sending from socket -> TCP Destination address: " + tcpPacket.getDestinationAddressAsLong());
                System.out.println("Sending from socket -> TCP Length of data: " + tcpPacket.getData().length);

                // Send packet
                mDatagramSocket.send(mDatagramPacket);

                // Receive packet
                mDatagramPacket.setLength(buffer.length);
                mDatagramSocket.receive(mDatagramPacket);
                mDatagramPacket.setSocketAddress(new InetSocketAddress("127.0.0.1",8087));
                mDatagramSocket.send(mDatagramPacket);

                System.out.println("Receiving from socket -> DatagramPacket Source port: " + mDatagramPacket.getPort());
                System.out.println("Receiving from socket -> DatagramPacket Destination port: " + mDatagramPacket.getAddress());
                System.out.println("Receiving from socket -> DatagramPacket Destination address: " + mDatagramPacket.getSocketAddress());
                System.out.println("Receiving from socket -> DatagramPacket Length of data: " + mDatagramPacket.getData().length);

                tcpPacket = new TCPPacket(0, mDatagramPacket.getData());
                System.out.println("Receiving from socket -> TCP Source port: " + tcpPacket.getSourcePort());
                System.out.println("Receiving from socket -> TCP Destination port: " + tcpPacket.getDestinationPort());
                System.out.println("Receiving from socket -> TCP Destination address: " + tcpPacket.getDestinationAddressAsLong());
                System.out.println("Receiving from socket -> TCP Length of data: " + tcpPacket.getData().length);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(DatagramPacket packet) {
       UDPPacket udpPacket; packet.getData();
    }
}
