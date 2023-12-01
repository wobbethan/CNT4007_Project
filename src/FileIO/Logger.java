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

    private void logMessage(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logTCPConnection(int connectingPeerId) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " makes a connection to Peer "
                + connectingPeerId + ".\n";

        logMessage(message);
    }

    public void logChangePreferredNeighbors(int[] preferredNeighbors) {
        if (preferredNeighbors == null || preferredNeighbors.length == 0) {
            System.err.println("ERROR: preferred neighbors list is invalid");
            return;
        }

        String csv = preferredNeighbors[0] + "";
        for (int i = 1; i < preferredNeighbors.length; i++) {
            csv += "," + preferredNeighbors[i];
        }

        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " has the preferred neighbors " +
                csv + ".\n";

        logMessage(message);
    }

    public void logChangeOptimisticallyUnchokedNeighbor(int neighbor) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " has the optimistically unchoked neighbor "
                + neighbor + ".\n";

        logMessage(message);
    }

}
