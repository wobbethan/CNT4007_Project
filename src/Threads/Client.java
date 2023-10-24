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
                Socket socket = new Socket(neighboringPeers.get(id)[0], Integer.parseInt(neighboringPeers.get(id)[1]));    

                // send server handshake
                Handshake clientHandshake = new Handshake(peerID);
                sendServerHandshake(socket, clientHandshake.getHandshakeAsByteArray());

                // receive server handshake
                byte[] serverHandshake = receiveServerHandshake(socket);
                System.out.println("client thread: " + new String (serverHandshake, "US-ASCII"));

            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
	 * retrieves the handshake message sent from server peer to client peer socket
	 * 
	 * @param socket communication socket between client peer and server peer
	 * @return handshake message as a byte[]
	 */
	private byte[] receiveServerHandshake(Socket socket) {
		byte[] handshake = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			handshake = (byte[]) inStream.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}

		return handshake;
	}

	/**
	 * sends the server peer a handshake message from the client peer over the
	 * socket
	 * 
	 * @param socket    communication socket between client peer and server peer
	 * @param handshake handshake message as a byte[]
	 */
	private void sendServerHandshake(Socket socket, byte[] handshake) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(handshake);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * retrieves the bitfield message sent from server peer to client peer socket
	 * 
	 * @param socket communication socket between client peer and server peer
	 * @return bitfield message as a byte[]
	 */
	private byte[] receiveServerBitfield(Socket socket) {
		byte[] bitfield = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			bitfield = (byte[]) inStream.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}

		return bitfield;
	}

	/**
	 * sends the server peer a bitfield message from the client peer over the socket
	 * 
	 * @param socket   communication socket between client peer and server peer
	 * @param bitfield the server's bitfield as a byte[]
	 */
	private void sendServerBitfield(Socket socket, byte[] bitfield) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(bitfield);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}