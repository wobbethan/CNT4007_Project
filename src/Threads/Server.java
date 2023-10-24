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

	public Server(int portNum, int peerID) {
		this.portNum = portNum;
		this.peerID = peerID;
	}

	@Override
	public void run() {
		try {
			ServerSocket listenerSocket = new ServerSocket(portNum);

			while (true) {
				Socket socket = listenerSocket.accept();

				// receive client handshake
				byte[] clientHandshake = receiveClientHandshake(socket);
                System.out.println("server thread: " + new String (clientHandshake, "US-ASCII"));

				// send client handshake
				Handshake serverHandshake = new Handshake(peerID);
				sendClientHandshake(socket, serverHandshake.getHandshakeAsByteArray());

				// TODO: receive client handshake

				// TODO: send handshake

				// TODO: check client handshake valid

				// TODO: check client id in handshake is contained within config file

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
