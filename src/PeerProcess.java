import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class PeerProcess extends Thread { // each peer is both a client and a server. Reads config file and creates peer object. 
    private String ID;
    private int port;
    private String host;

    public PeerProcess(String ID, int port, String host){ // peer object constructor
        this.ID = ID;
        this.port = port;
        this.host = host;
    }



}
