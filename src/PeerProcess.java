import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.*;
import Threads.Client;
import Threads.Server;

public class PeerProcess extends Thread {
    private int peerID;

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
    private boolean hasFullFile;
    private static boolean[] bitfield;

    public PeerProcess(int peerID) {
        this.peerID = peerID;
    }

    public static void main(String[] args) {
        PeerProcess peer = new PeerProcess(Integer.parseInt(args[0]));

        String configType = "small";

        // parse configs
        CommonParser commonParser = new CommonParser(configType);
        PeerInfoParser peerParser = new PeerInfoParser(configType);

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
        peer.hasFullFile = neighboringPeers.get(peer.peerID)[2].equals("1") ? true : false;

        // TODO: spin up logger

        bitfield = new boolean[(int) Math.ceil(peer.fileSize / peer.pieceSize)];

        if (peer.hasFullFile) {
            // set entire bitfield to 1s
            for (int i = 0; i < bitfield.length; i++) {
                bitfield[i] = true;
            }

            // populate neighboringPeers with every piece

            // create and run server thread only
            // Server serverThread = new Server();
            // serverThread.run();
        } else {
            // only spawn client thread if peer doesn't have full file
            neighboringPeers = new HashMap<>();

            // create and run server thread
            // Server serverThread = new Server();
            // serverThread.run();

            // create and run client thread
            // Server clientThread = new Server();
            // clientThread.run();
        }

        byte[] test = convertBitfieldToByteArray(peer.fileSize, peer.pieceSize);
        for (byte b : test) {
            System.out.print(Integer.toBinaryString(b & 0xFF) + " ");
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
     * FIXME: this does not work LMAO
     * converts the PeerProcess's boolean array bitfield to a byte array
     * 
     * @param fileSize  size of file in bytes, grabbed from config file
     * @param pieceSize size of piece in bytes, grabbed from config file
     * @return byte array representing the PeerProcess's bitfield
     */
    public static byte[] convertBitfieldToByteArray(int fileSize, int pieceSize) {
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
}
