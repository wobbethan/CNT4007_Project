package utils;
import java.io.Serializable;

public class Handshake implements Serializable{
    
    public byte[] handshakeBytes = new byte[32];
    
    public Handshake (int peerID){
        String handshake = "P2PFILESHARINGPROJ" + "0000000000" + Integer.toString(peerID);
        handshakeBytes = handshake.getBytes();
        //System.out.print(handshake);

    }
}
