package FileIO;

import java.io.*;
import java.util.Properties;

public class CommonParser {
    private int numPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private String configType;

    public CommonParser(String type){
        this.configType = type;
    }

    public int getNumNeighbors(){
        return numPreferredNeighbors;
    }
    public int getUnchokingInterval(){
        return unchokingInterval;
    }
    public int getOptimisticInterval(){
        return optimisticUnchokingInterval;
    }
    public String getFileName(){
        return fileName;
    }
    public int getFileSize(){
        return fileSize;
    }
    public int getPieceSize(){
        return pieceSize;
    }

    

    public void read(){
        Properties commonCfg = new Properties();

        try(FileInputStream fileInput = new FileInputStream("Canvas\\Project\\project_config_file_small\\project_config_file_small\\Common.cfg")) {

            commonCfg.load(fileInput);
            this.numPreferredNeighbors = Integer.parseInt(commonCfg.getProperty("NumberOfPreferredNeighbors"));
            this.unchokingInterval = Integer.parseInt(commonCfg.getProperty("UnchokingInterval"));
            this.optimisticUnchokingInterval = Integer.parseInt(commonCfg.getProperty("OptimisticUnchokingInterval"));
            this.fileName = commonCfg.getProperty("FileName");
            this.fileSize = Integer.parseInt(commonCfg.getProperty("FileSize"));
            this.pieceSize = Integer.parseInt(commonCfg.getProperty("PieceSize"));


        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testParser() {
        System.out.println("-----------------------------------------");
        System.out.println("Testing file parsing:     ");
        System.out.println("Number of preferred neighbors: " + numPreferredNeighbors);
        System.out.println("UnChoking Interval: " + unchokingInterval);
        System.out.println("Optimistic Interval: " + optimisticUnchokingInterval);
        System.out.println("Filename: " + fileName);
        System.out.println("File Size: " + fileSize);
        System.out.println("Piece Size: " + pieceSize);
        System.out.println("-----------------------------------------");

    }

    //public static void main(String[] args){ //Internal Main for testing
    //    CommonParser testParserSmall = new CommonParser("small");
    //    testParserSmall.read();
    //    testParserSmall.testParser();
    //
    //    CommonParser testParserLarge = new CommonParser("large");
    //    testParserLarge.read();
    //    testParserLarge.testParser();
    //
    //}
    
    
}

