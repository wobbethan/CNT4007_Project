package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.Logger;

import Messages.Handshake;

public class Server extends Thread {
	private int portNum;
	private int peerId;
	private HashMap<Integer, String[]> neighboringPeers;
	private byte[] bitfield; // TODO: maybe convert this back to be a boolean array
	private Logger logger;
	private boolean[] convertedBitField;

	public Server(int portNum, int peerId, HashMap<Integer, String[]> neighboringPeers, byte[] bitfield, Logger logger) {
		this.portNum = portNum;
		this.peerId = peerId;
		this.neighboringPeers = neighboringPeers;
		this.bitfield = bitfield;
		this.logger = logger;
		this.convertedBitField = byteArrayToBooleanArray(bitfield);
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

				logger.logTCPConnection(clientId);

				// send server's bitfield to client
				sendClientBitfield(socket, bitfield);
				logger.logBitfieldSent(clientId);

				// receive bitfield from client
				byte[] clientBitfieldMessage = receiveClientBitfield(socket);
				byte[] clientBitfield = extractPayload(clientBitfieldMessage);
				logger.logBitfieldReceived(clientId);

				// loop of sending pieces / receiving request messages

				// Receive first message (should be interested or not interested)
				byte[] clientMessage = receiveClientMessage(socket);
				int messageType = extractType(clientMessage);

				// If the client is interested
				if(messageType == 2){
					// Log reception of interested message
					logger.logReceivingInterestedMessage(clientId);

					// Until client sends not interested 
					while (messageType != 3){

						// Get next message
						clientMessage = receiveClientMessage(socket);
						messageType = extractType(clientMessage);

						if(messageType == 6){
							// Get index for requested piece
							byte[] payload = extractPayload(clientMessage);
							int index = byteArrayToInt(payload);
							logger.logRequestReceived(clientId, index);

							// Return have message if server has piece
							if(convertedBitField[index]){
								sendHaveMessage(socket, index);
							}
						}

						// Break to prevent infinite loop
						break;
						
					}
					// Log reception of not interested message 
					logger.logReceivingNotInterestedMessage(clientId);

					
				}
				// If the client is not interested
				else if(messageType == 3){
					// Log reception of not interested message 
					logger.logReceivingNotInterestedMessage(clientId);
				}


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

	private void sendHaveMessage(Socket socket, int index) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			byte[] message = createMessage(MIN_PRIORITY, index);
			outStream.writeObject(4);
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
			byte[] generatedMessage = createMessage(5, bitfield);
			outStream.writeObject(generatedMessage);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * sends the client peer a bitfield message from the server peer over the socket
	 * 
	 * @param socket   communication socket between client peer and server peer
	 * @param bitfield the server's bitfield as a byte[]
	 */
	private void sendClientPiece(Socket socket, byte[] bitfield) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			byte[] generatedMessage = createMessage(5, bitfield);
			outStream.writeObject(generatedMessage);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * sends the client peer a bitfield message from the server peer over the socket
	 * 
	 * @param socket   communication socket between client peer and server peer
	 */
	private byte[] receiveClientMessage(Socket socket) {
		byte[] message = null;
		try {
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			message = (byte[]) inStream.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}

		return message;
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
     * @param type  4 for confirming having a piece, 6 for requesting piece
     * @param index index of piece
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

        byte[] payload = Arrays.copyOfRange(message, 5, message.length);

		return payload;
    }

	/**
     * Function to extract message type
     * 
     * @param message message byte array containing message header

     * @return int representing type of message received 
     */

	 private static int extractType(byte[] message) {

		// message type resides in 5th index
		int type = message[4] & 0xFF; 
		return type;
    }

	/**
     * Function to convert byteArray to int
     * 
     * @param byteArray byte array representing an int as 4 bytes

     * @return int representation
     */

	public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != 4) {
            throw new IllegalArgumentException("Byte array must have length 4");
        }

        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (byteArray[i] & 0xFF) << ((3 - i) * 8);
        }

        return result;
    }

	/**
     * Converts byte[] bitfield to boolean representation
     * 
     * @return boolean representation of bitfield
     */

	 private static boolean[] byteArrayToBooleanArray(byte[] bitfield) {
        boolean[] booleanBitField = new boolean[bitfield.length * 8];

        for (int i = 0; i < bitfield.length; i++) {
            for (int j = 0; j < 8; j++) {
                booleanBitField[i * 8 + j] = (bitfield[i] & (1 << (7 - j))) != 0;
            }
        }

        return booleanBitField;
    }

	// /**
    //  * check if Server has requested piece
    //  * 
    //  * @return boolean representation of if server has piece
    //  */

	// private static boolean[] checkHasPiece(int index) {

    //     return convertedBitField[index];
    // }

}
