import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.*;

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
    // key = peerID, value = peerInfo config string tokens
    private static HashMap<Integer, String[]> neighboringPeers; 
    private String hostName;
    private int listeningPort;
    private Boolean fullFile;

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
        peer.fullFile = neighboringPeers.get(peer.peerID)[2].equals("1") ? true : false;

        // Print Testing
        System.out.println(peer.numPeers);
        System.out.println(peer.hostName);
        System.out.println(peer.listeningPort);
        System.out.println(peer.fullFile);

    }

}
