package FileIO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class PeerInfoParser {

    private String configType;

    public PeerInfoParser(String type){
        this.configType = type;
    }

    public HashMap<Integer, String[]> readFile(){
        HashMap<Integer, String[]> parsedFile = new HashMap<>();

        try(BufferedReader fileInput = new BufferedReader(new FileReader("Canvas\\Project\\project_config_file_large\\project_config_file_large\\PeerInfo.cfg"))) {
            String line = null;
            while((line = fileInput.readLine()) != null){
                String[] args = line.split(" ");
                parsedFile.put(Integer.parseInt(args[0]), Arrays.copyOfRange(args, 1, args.length));
            }
            fileInput.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parsedFile;
    }
    
}
