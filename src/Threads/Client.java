package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Messages.Handshake;

public class Client extends Thread {
    private int portNum;
    private int peerID;
    private String hostIP;
    private HashMap<Integer, String[]> neighboringPeers;

    public Client(int portNum, int peerID, String hostIP, HashMap<Integer, String[]> neighboringPeers) {
        this.portNum = portNum;
        this.peerID = peerID;
        this.hostIP = hostIP;
        this.neighboringPeers = neighboringPeers;
    }

    public void run() {
        
            
        // try to connect to every peer in the network
        for (Integer id : neighboringPeers.keySet()) {
            try {
                // FIXME: neighboringPeers is blank for some reason, perhaps not getting
                // assigne correctly in PeerProcess
                System.out.println(neighboringPeers.get(id)[0] + " " +  Integer.parseInt(neighboringPeers.get(id)[1]));

                Socket serverSocket = new Socket(neighboringPeers.get(id)[0], Integer.parseInt(neighboringPeers.get(id)[1]));    
                
                // send handshake
                Handshake handshake = new Handshake(peerID);
                ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
                out.writeObject(handshake);
                out.flush();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

}