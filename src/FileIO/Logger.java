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

    // NOTE: only the server thread should call this since it's the one determining
    // preferred neighbors
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

    // NOTE: only the server thread should call this since it's the one doing the
    // unchoking
    public void logUnchoking(int neighbor) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + neighbor + " is unchoked by "
                + peerId + ".\n";

        logMessage(message);
    }

    // NOTE: only the server thread should call this since it's the one doing the
    // choking
    public void logChoking(int neighbor) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + neighbor + " is choked by "
                + peerId + ".\n";

        logMessage(message);
    }

    // NOTE: only the client thread should call this since it's the one receiving
    // the 'have' message
    public void logReceivingHaveMessage(int neighbor, int pieceIndex) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " received the 'have' message from "
                + neighbor + " for the piece " + pieceIndex + ".\n";

        logMessage(message);
    }

    // NOTE: only the server thread should call this since it's the one receiving
    // the 'interested' message
    public void logReceivingInterestedMessage(int neighbor) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " received the 'interested' message from "
                + neighbor + ".\n";

        logMessage(message);
    }

    // NOTE: only the server thread should call this since it's the one receiving
    // the 'not interested' message
    public void logReceivingNotInterestedMessage(int neighbor) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " received the 'not interested' message from "
                + neighbor + ".\n";

        logMessage(message);
    }

    // NOTE: only the client thread should call this since it's the one downloading
    // the piece
    public void logDownloadingPiece(int downloadedFrom, int pieceIndex) {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " has downloaded the piece "
                + pieceIndex + " from " + downloadedFrom + ".\n";

        logMessage(message);
    }

    // NOTE: only the client thread should call this since it's the one downloading
    public void logDownloadCompletion() {
        String message = "[" + fetchCurrentTime() + "]: Peer " + peerId + " has downloaded the complete file.\n";

        logMessage(message);
    }

}
