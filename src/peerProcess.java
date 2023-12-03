import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.*;
import Threads.Client;
import Threads.Server;

//Used to create a shared boolean variable between child and parent threads
import java.util.concurrent.atomic.AtomicBoolean;

public class peerProcess extends Thread {
    private int peerID;
    private Logger logger;

    // retrieved from Common config
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private int unchokingInterval;
    private int OptimisticUnchokingInterval;
    private int numPreferredNeighbors;

    // retrieved from PeerInfo config
    private int numPeers;
    private static HashMap<Integer, String[]> neighboringPeers; // key = peerID, value = peerInfo config string tokens
    private String hostName;
    private int listeningPort; 
    private AtomicBoolean hasFullFile = new AtomicBoolean(false);
    private static boolean[] bitfield;

    public peerProcess(int peerID) {
        this.peerID = peerID;
    }

    public static void main(String[] args) {
        peerProcess peer = new peerProcess(Integer.parseInt(args[0]));
        String configType = "small";
        String configEnv = "-local-testing";

        // parse configs
        CommonParser commonParser = new CommonParser(configType);
        PeerInfoParser peerParser = new PeerInfoParser(configType, configEnv);

        // common config processing
        commonParser.read();
        peer.fileName = commonParser.getFileName();
        peer.fileSize = commonParser.getFileSize();
        peer.pieceSize = commonParser.getPieceSize();
        peer.OptimisticUnchokingInterval = commonParser.getOptimisticInterval();
        peer.unchokingInterval = commonParser.getUnchokingInterval();
        peer.numPreferredNeighbors = commonParser.getNumNeighbors();

        // peerInfo processing
        neighboringPeers = peerParser.readFile();
        peer.numPeers = neighboringPeers.size();
        peer.hostName = neighboringPeers.get(peer.peerID)[0];
        peer.listeningPort = Integer.parseInt(neighboringPeers.get(peer.peerID)[1]);

        // Modified statement to work with Atomic boolean
        peer.hasFullFile.set(neighboringPeers.get(peer.peerID)[2].equals("1") ? true : false);

        // spin up logger
        peer.logger = new Logger(peer.peerID);

        bitfield = new boolean[(int) Math.ceil(peer.fileSize / peer.pieceSize)];

        //.get() extracts boolean value from atomic
        if (peer.hasFullFile.get()) {
            // set entire bitfield to 1s
            for (int i = 0; i < bitfield.length; i++) {
                bitfield[i] = true;
            }

            // TODO: populate piece hashmap to have all pieces

            // create and run server thread only
            Server serverThread = new Server(peer.listeningPort, peer.peerID, neighboringPeers,
                    convertBitfieldToByteArray(bitfield), peer.logger);
            serverThread.start();
        } else {
            // TODO: create new empty piece hashmap

            // create and run server thread
            Server serverThread = new Server(peer.listeningPort, peer.peerID, neighboringPeers,
                    convertBitfieldToByteArray(bitfield), peer.logger);
            serverThread.start();

            // create and run client thread
            Client clientThread = new Client(peer.peerID, neighboringPeers, convertBitfieldToByteArray(bitfield), peer.logger, peer.hasFullFile, (int)(Math.ceil((peer.fileSize/peer.pieceSize))));
            clientThread.start();
        }
    }

    /**
     * sets piece inside bitfield to be true, meaning peer has that piece
     * 
     * @param pieceIndex the index of the piece in the bitfield
     */

    public static void addPieceToBitfield(int pieceIndex) {
        bitfield[pieceIndex] = true;
    }

    /**
     * checks bitfield for all 1s (full file) if not exit and return index of first
     * missing piece
     * 
     * @return index of first missing piece, -1 if has full file
     */

    public int checkHasFullFile() {
        if (hasFullFile.get()) {
            return -1;
        }

        // return index of first missing piece
        for (int i = 0; i < bitfield.length; i++) {
            if (!bitfield[i]) {
                return i;
            }
        }

        // peer process proven to have full file
        hasFullFile.set(true);
        return -1;
    }

    /**
     * converts the PeerProcess's boolean array bitfield to a byte array
     * 
     * @param fileSize  size of file in bytes, grabbed from config file
     * @param pieceSize size of piece in bytes, grabbed from config file
     * @return byte array representing the PeerProcess's bitfield
     */
    public static byte[] booleanArrayToByteArray(int fileSize, int pieceSize) {
        byte[] byteArray = new byte[(int) Math.ceil(fileSize / pieceSize / 8)];

        for (int i = 0; i < bitfield.length; i += 8) {
            byte elem = 0;
            for (int j = 0; j < 8; j++) {
                if (i + j >= bitfield.length) {
                    return byteArray;
                }

                elem |= (bitfield[i + j] ? 1 : 0) << Math.abs(j - 7);
            }

            byteArray[i / 8] = elem;
        }

        return byteArray;
    }


    private static byte[] convertBitfieldToByteArray(boolean[] booleanArray) {
        int numBytes = (int) Math.ceil(booleanArray.length / 8.0);
        byte[] byteArray = new byte[numBytes];

        for (int i = 0; i < booleanArray.length; i++) {
            if (booleanArray[i]) {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                byteArray[byteIndex] |= (1 << bitIndex);
            }
        }

        return byteArray;
    }

    // TODO Function to determine type of message needed
    


}
