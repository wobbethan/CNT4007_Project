package FileIO;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;

public class Logger {
    private int peerId;
    private String fileName;

    public Logger(int peerId) {
        this.peerId = peerId;
        fileName = "./log_peer_" + peerId + ".log";

        try {
            // clear previous logs
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.close();
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write("[" + fetchCurrentTime() + "]: Peer " + peerId + " makes a connection to Peer "
                    + connectingPeerId + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
