package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Messages.Handshake;

public class Client extends Thread {
	private int peerID;
	private HashMap<Integer, String[]> neighboringPeers;
	private byte[] bitfield; // TODO: maybe convert this back to be a boolean array

	public Client(int peerID, HashMap<Integer, String[]> neighboringPeers, byte[] bitfield) {
		this.peerID = peerID;
		this.neighboringPeers = neighboringPeers;
		this.bitfield = bitfield;
	}

	// FIXME: maybe... when a client spawns, it'll only connect to the servers that
	// are active.
	// when a new peer joins the network, this client peer will not try to connect
	// to the new one's
	// server even though it probably should
	public void run() {
		// try to connect to every peer in the network
		for (Integer id : neighboringPeers.keySet()) {
			try {
				// don't establish connection with itself
				if (id == peerID)
					continue;

				Socket socket = new Socket(neighboringPeers.get(id)[0], Integer.parseInt(neighboringPeers.get(id)[1]));

				// send handshake to server
				Handshake clientHandshake = new Handshake(peerID);
				sendServerHandshake(socket, clientHandshake.getHandshakeAsByteArray());

				// receive server handshake
				byte[] serverHandshake = receiveServerHandshake(socket);

				String serverTranslated = new String(serverHandshake, "US-ASCII");

				System.out.println("client thread: " + new String(serverHandshake, "US-ASCII"));

				// check handshake validity (correct format)
				if (!serverTranslated.substring(0, 28).equals("P2PFILESHARINGPROJ0000000000")) {
					System.err.println("Invalid handshake format recieved from server");
					continue;
				}

				// check if peer id in handshake is contained within PeerInfo.cfg
				if (!neighboringPeers.containsKey(Integer.parseInt(serverTranslated.substring(28, 32)))) {
					System.err.println("Unknown peerID aborting connection");
					continue;
				}

				// send client's bitfield to server
				sendServerBitfield(socket, bitfield);

				// receive server's bitfield
				byte[] serverBitfield = receiveServerBitfield(socket);
				printByteArrayAsBinary(serverBitfield);

				// TODO: add new client-server connection to peer list I think

				// TODO: maybe find some way to track all peers that have a file

				// TODO: spawn send message thread

				// TODO: spawn request piece thread

				// TODO: spawn receive message thread
			} catch (ConnectException e) {
				// don't log failed connection message
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

	/**
	 * prints a byte array as nicely formatted binary
	 * 
	 * @param array byte array to print as binary
	 */
	private void printByteArrayAsBinary(byte[] array) {
		for (byte b : array) {
			for (int i = 7; i >= 0; i--) {
				System.out.print((b >> i) & 1);
			}

			System.out.print(" ");
		}

		System.out.println();
	}

}