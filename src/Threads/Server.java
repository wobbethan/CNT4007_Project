package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Messages.Handshake;

public class Server extends Thread {
	private int portNum;
	private int peerId;
	private HashMap<Integer, String[]> neighboringPeers;
	private byte[] bitfield; // TODO: maybe convert this back to be a boolean array

	public Server(int portNum, int peerId, HashMap<Integer, String[]> neighboringPeers, byte[] bitfield) {
		this.portNum = portNum;
		this.peerId = peerId;
		this.neighboringPeers = neighboringPeers;
		this.bitfield = bitfield;
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
				Handshake serverHandshake = new Handshake(peerId);
				sendClientHandshake(socket, serverHandshake.getHandshakeAsByteArray());

				String clientTranslated = new String(clientHandshake, "US-ASCII");

				System.out.println("server thread: " + new String(clientHandshake, "US-ASCII"));

				// check handshake validity (correct format)
				if (!clientTranslated.substring(0, 28).equals("P2PFILESHARINGPROJ0000000000")) {
					System.err.println("Invalid handshake format recieved from server");
					continue;
				}

				// check if peer id in handshake is contained within PeerInfo.cfg
				int clientId = Integer.parseInt(clientTranslated.substring(28, 32));
				if (!neighboringPeers.containsKey(clientId)) {
					System.err.println("Unknown peerId aborting connection");
					continue;
				}

				// stop this peer from handshaking with itself (peer client connects to same peer server thread)
				if (peerId == clientId) {
					continue;
				}

				// send server's bitfield to client
				sendClientBitfield(socket, bitfield);

				// receive bitfield from client
				byte[] clientBitfield = receiveClientBitfield(socket);
				printByteArrayAsBinary(clientBitfield);

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
