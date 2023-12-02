package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.Logger;

import Messages.Handshake;

public class Client extends Thread {
	private int peerID;
	private HashMap<Integer, String[]> neighboringPeers;
	private byte[] bitfield; // TODO: maybe convert this back to be a boolean array
	private Logger logger;

	public Client(int peerID, HashMap<Integer, String[]> neighboringPeers, byte[] bitfield, Logger logger) {
		this.peerID = peerID;
		this.neighboringPeers = neighboringPeers;
		this.bitfield = bitfield;
		this.logger = logger;
	}

	// FIXME: when a client spawns, it'll only connect to the servers that
	// are active. when a new peer joins the network, this client peer will not try
	// to connect to the new ones
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

				// check handshake validity (correct format)
				if (!serverTranslated.substring(0, 28).equals("P2PFILESHARINGPROJ0000000000")) {
					System.err.println("Invalid handshake format recieved from server");
					continue;
				}

				// check if peer id in handshake is contained within PeerInfo.cfg
				int serverId = Integer.parseInt(serverTranslated.substring(28, 32));
				if (!neighboringPeers.containsKey(serverId)) {
					System.err.println("Unknown peerID aborting connection");
					continue;
				}

				logger.logTCPConnection(serverId);

				// send client's bitfield to server
				sendServerBitfield(socket, bitfield);
				logger.logBitfieldSent(serverId);

				// receive server's bitfield
				byte[] serverBitfieldMessage = receiveServerBitfieldMessage(socket);
				byte[] serverBitfield = extractPayload(serverBitfieldMessage);
				logger.logBitfieldReceived(serverId);

				// loop of sending/receiving messages

				// while client does not have full file

				while(true){
					sendInterestedMessage(socket);
					// Break to prevent infinite loop
					break;
				}

				//When client has full file send not interested 
				sendNotInterestedMessage(socket);


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
	private byte[] receiveServerBitfieldMessage(Socket socket) {
		byte[] bitfieldMessage = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			bitfieldMessage = (byte[]) inStream.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}

		return bitfieldMessage;
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
			byte[] generatedMessage = createMessage(5, bitfield);
			outStream.writeObject(generatedMessage);
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

	/**
     * First variant of function to be used for messages for the first 4 message
     * types
     * 
     * @param type size of file in bytes, grabbed from config file
     * @return byte array representing a message sent by a peer process
     */
    private static byte[] createMessage(int type) {

        int size = 5;

        // create message array
        byte[] message = new byte[5];

        // write size
        message[0] = 0;
        message[1] = 0;
        message[2] = 0;
        message[3] = (byte) size;

        // write message type
        message[4] = (byte) type;

        return message;

    }

    /**
     * Second variant of function to be used for messages for the "have" and
     * "request" message types
     * 
     * @param type  size of file in bytes, grabbed from config file
     * @param index payload for messages of type 4 and 6
     * @return byte array representing a message sent by a peer process
     */
    private static byte[] createMessage(int type, int index) {

        int size = 9;

        // create message array
        byte[] message = new byte[size];

        // write size
        message[0] = 0;
        message[1] = 0;
        message[2] = 0;
        message[3] = (byte) size;

        // write message type
        message[4] = (byte) type;

        message[5] = (byte) (index >> 24);
        message[6] = (byte) (index >> 16);
        message[7] = (byte) (index >> 8);
        message[8] = (byte) index;

        return message;

    }

    /**
     * Third variant of function to be used for messages for the "bitfield" and
     * "piece" message types
     * 
     * @param type    size of file in bytes, grabbed from config file
     * @param payload payload for messages of type 5 (where payload is bitfield) and
     *                7 (where payload is a piece)
     * @return byte array representing a message sent by a peer process
     */

    private static byte[] createMessage(int type, byte[] payload) {

        int size = 5 + payload.length;

        // create message array
        byte[] message = new byte[size];

        // write size
        message[0] = (byte) (size >> 24);
        message[1] = (byte) (size >> 16);
        message[2] = (byte) (size >> 8);
        message[3] = (byte) size;

        // write message type
        message[4] = (byte) type;

        for (int i = 0; i < payload.length; i++) {
            message[i + 5] = payload[i];
        }

        return message;

    }

	/**
     * Function to extract payload from message
     * payload can be bitfield or piece
     * 
     * @param message message byte array containing message header and payload

     * @return byte array representing the payload from the message
     */

    private static byte[] extractPayload(byte[] message) {

		// Copy message array after 5 bytes, first 5 are header
        byte[] payload = Arrays.copyOfRange(message, 5, message.length);

		return payload;
    }

	/**
	 * Constructs interested message and sends to server
	 * 
	 * @param socket   communication socket between client peer and server peer
	 */

	private void sendInterestedMessage(Socket socket) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			byte[] generatedMessage = createMessage(2);
			outStream.writeObject(generatedMessage);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * Constructs not interested message and sends to server
	 * 
	 * @param socket   communication socket between client peer and server peer
	 */

	private void sendNotInterestedMessage(Socket socket) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			byte[] generatedMessage = createMessage(3);
			outStream.writeObject(generatedMessage);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}