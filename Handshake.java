public class Handshake {
    
    public byte[] handshakeBytes = new byte[32];
    
    public Handshake (int peerID){
        String handshake = "P2PFILESHARINGPROJ" + "0000000000" + Integer.toString(peerID);
        handshakeBytes = handshake.getBytes();
    }
}
