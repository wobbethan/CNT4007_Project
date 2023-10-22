import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import FileIO.*;

public class PeerProcess extends Thread { // each peer is both a client and a server. Reads config file and creates peer object. 
    private int ID;

    //To be retrieved from Common config
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private int unchokingInterval;
    private int OptimisticUnchokingInterval;
    private int numPreferredNeighbors;

    //To be retrieved from PeerInfo config
    private int numPeers;
    private static HashMap<Integer, String[]> network;
    private String hostName;
    private int listeningPort;
    private Boolean fullFile;


    public PeerProcess(int ID){ // peer object constructor
        this.ID = ID;
    }

    public static void main(String [] args){

        PeerProcess peer = new PeerProcess(1005); //Define ID of process

        String configType = "small"; //Determines which config to read

        CommonParser commonParser = new CommonParser(configType); //Parse config files
        PeerInfoParser peerParser = new PeerInfoParser(configType);

        peer.fileName = commonParser.getFileName(); //Common config processing
        peer.fileSize = commonParser.getFileSize();
        peer.pieceSize = commonParser.getPieceSize();
        peer.OptimisticUnchokingInterval = commonParser.getOptimisticInterval();
        peer.unchokingInterval = commonParser.getUnchokingInterval();
        peer.numPreferredNeighbors = commonParser.getNumNeighbors();

        peer.network = peerParser.readFile(); //PeerInfo processing
        peer.numPeers = peer.network.size();
        peer.hostName = network.get(peer.ID)[0];
        peer.listeningPort = Integer.parseInt(network.get(peer.ID)[1]);
        peer.fullFile = network.get(peer.ID)[2].equals("1") ? true : false;

        //Print Testing
        System.out.println(peer.numPeers);
        System.out.println(peer.hostName);
        System.out.println(peer.listeningPort);
        System.out.println(peer.fullFile);



    }


}
