package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Messages.Handshake;

public class Server extends Thread {
	private int portNum;
	private int peerID;
	private HashMap<Integer, String[]> neighboringPeers;

	public Server(int portNum, int peerID, HashMap<Integer, String[]> neighboringPeers) {
		this.portNum = portNum;
		this.peerID = peerID;
		this.neighboringPeers = neighboringPeers;
	}

	@Override
	public void run() {
		try {
			ServerSocket listenerSocket = new ServerSocket(portNum);

			while (true) {
				Socket socket = listenerSocket.accept();

				// receive client handshake
				byte[] clientHandshake = receiveClientHandshake(socket);

				// send handshake to client
				Handshake serverHandshake = new Handshake(peerID);
				sendClientHandshake(socket, serverHandshake.getHandshakeAsByteArray());

				String clientTranslated = new String(clientHandshake, "US-ASCII");

				System.out.println("server thread: " + new String(clientHandshake, "US-ASCII"));

				// check handshake validity (correct format)
				if (!clientTranslated.substring(0, 28).equals("P2PFILESHARINGPROJ0000000000")) {
					System.err.println("Invalid handshake format recieved from server");
					continue;
				}

				// check if peer id in handshake is contained within PeerInfo.cfg
				if (!neighboringPeers.containsKey(Integer.parseInt(clientTranslated.substring(28, 32)))) {
					System.err.println("Unknown peerID aborting connection");
					continue;
				}

				// TODO: stop this peer from handshaking with itself

				// TODO: send server bitfield to client

				// TODO: receive bitfield from client

				// TODO: log tcp connection established

				// TODO: maybe find some way to track all peers that have a file

				// TODO: spawn send message thread

				// TODO: spawn request piece thread

				// TODO: spawn receive message thread

			}
		} catch (IOException e) {
			System.err.println(e);
		}

	}

	/**
	 * retrieves the handshake message sent from client peer to server peer socket
	 * 
	 * @param socket communication socket between client peer and server peer
	 * @return handshake message as a byte[]
	 */
	private byte[] receiveClientHandshake(Socket socket) {
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
	 * sends the client peer a handshake message from the server peer over the
	 * socket
	 * 
	 * @param socket    communication socket between client peer and server peer
	 * @param handshake handshake message as a byte[]
	 */
	private void sendClientHandshake(Socket socket, byte[] handshake) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(handshake);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * retrieves the bitfield message sent from client peer to server peer socket
	 * 
	 * @param socket communication socket between client peer and server peer
	 * @return bitfield message as a byte[]
	 */
	private byte[] receiveClientBitfield(Socket socket) {
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
	 * sends the client peer a bitfield message from the server peer over the socket
	 * 
	 * @param socket   communication socket between client peer and server peer
	 * @param bitfield the server's bitfield as a byte[]
	 */
	private void sendClientBitfield(Socket socket, byte[] bitfield) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(bitfield);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
