package File_Storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import FileIO.Logger;

public class fileManager{
    private int peerID;
    private Logger logger;
    private byte[][] fileArray;
    private boolean hasFullFile;
    private int fileSize;
    private int numPieces;
    private int pieceSize;


    public fileManager(int peerID, int fileSize, int pieceSize, Logger logger, boolean hasFullFile){
        // Setting class member vars
        this.peerID = peerID;
        this.numPieces = (int) Math.ceil(fileSize / pieceSize);
        this.fileArray = new byte[numPieces][pieceSize];
        this.logger = logger;
        this.hasFullFile = hasFullFile;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;


        //If peer has full file, populate fileArray

        if(hasFullFile){
            //Create file object from file path
            String filePath = "./File_Storage/" + peerID + "/thefile";
            File file = new File(filePath);

            if (file.exists()) {
                try {
                    // Open the file for reading
                    FileInputStream fileInputStream = new FileInputStream(file);

                    // Read from the file
        
                    for(int i = 0; i < numPieces; i++){

                        for(int j = 0; j < pieceSize; j++){
                           int byteRead = fileInputStream.read();
                           if(byteRead != -1){
                                fileArray[i][j] = (byte) byteRead;
                           }

                        }

                    }

                    // Close the file stream
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.err.println("File does not exist.");
            }


        }

    }


    /**
     * NOTE: To be used by client only
     * receieves a piece and adds it into 
     * 
     * @param piece  the actual file piece
     * @param pieceNum piece number determines index in which piece sits in fileArray
     * 
     */

    public void addPiece(byte[] piece, int pieceNum){
        fileArray[pieceNum] = piece;
        

    }

    /**
     * NOTE: To be used by server only
     * receieves a piece and adds it into
     * 
     * @param pieceNum  index of requested piece
     * @return the actual file piece
     * 
     */
    
    public byte[] sendPiece(int pieceNum){
        return fileArray[pieceNum];

    }

    /**
     * NOTE: To be used when peer who did not start with full file gets all pieces
     * 
     */

    public void createFile(){
        byte[] finalFile = new byte[fileSize];
        
        for(int i = 0; i < numPieces; i++){

            for(int j = 0; j < pieceSize; i++){
                finalFile[i*pieceSize+j] = fileArray[i][j];

            }

        }

        String filePath = "./File_Storage/" + peerID + "/thefile";

        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            // Write the byte array to the file
            stream.write(finalFile);

            System.out.println("File created and data written successfully.");
            stream.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

    }

    
}
