package FileIO;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Logger {
    private int peerId;
    private String fileName;
    private PrintWriter writer;

    public Logger(int peerId) {
        this.peerId = peerId;
        fileName = "./log_peer_" + peerId + ".log";

        try {
            // clear previous logs
            writer = new PrintWriter(fileName);
            writer.close();
            
            // open new writer
            writer = new PrintWriter(fileName);
            writer.println("test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fetchCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return currentTime.format(formatter);
    }

    public void logTCPConnection(int connectingPeerId) {
        // try {
        //     writer.println("[" + fetchCurrentTime() + "]:Peer" + peerId + " makes a connection to Peer " + connectingPeerId);
        // } catch(IOException e) { 
        //     e.printStackTrace();
        // }
    }

}
