
import java.io.*;
import java.util.Properties;

public class CommonParser {
    private int numPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;

    public int getNumNeighbors(){
        return numPreferredNeighbors;
    }
    public int getUnchokingInterval(){
        return unchokingInterval;
    }
    public int getOptimisticInterval(){
        return optimisticUnchokingInterval;
    }
    public String fileName(){
        return fileName;
    }
    public int fileSize(){
        return fileSize;
    }
    public int pieceSize(){
        return pieceSize;
    }

    //May need setter functions ;)
    
    public void read(){
        Properties commonCfg = new Properties();
        
        
        try (FileInputStream fileInput = new FileInputStream("/Canvas/Project/project_config_file_small/project_config_file_small/Common.cfg")) { //maybe wrong file path
                commonCfg.load(fileInput);

                numPreferredNeighbors = Integer.parseInt(commonCfg.getProperty("NumberOfPreferredNeighbors")); //extract preferred Neighbors and assign
                unchokingInterval = Integer.parseInt(commonCfg.getProperty("UnchokingInterval"));
                optimisticUnchokingInterval = Integer.parseInt(commonCfg.getProperty("OptimisticUnchokingInterval"));
                fileName = commonCfg.getProperty("FileName");
                fileSize = Integer.parseInt(commonCfg.getProperty("FileSize"));
                pieceSize = Integer.parseInt(commonCfg.getProperty("PieceSize"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testParser() {
        System.out.println("-----------------------------------------");
        System.out.println("Testing file parsing:     ");
        System.out.println("Number of preferred neighbors: " + numPreferredNeighbors);
        System.out.println("Unchoking Interval: " + unchokingInterval);
        System.out.println("Optimistic Interval: " + optimisticUnchokingInterval);
        System.out.println("Filename: " + fileName);
        System.out.println("File Size: " + fileSize);
        System.out.println("Piece Size: " + pieceSize);
        System.out.println("-----------------------------------------");

    }
    
    
}

