import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class PeerProcess extends Thread { // each peer is both a client and a server. Reads config file and creates peer object. 
    private int ID;
    private int port;
    private String host;

    public Peer(int ID, int port, String host){ 
        this.id = id;
        this.port = port;
        this.host = host;
    }

    
}
