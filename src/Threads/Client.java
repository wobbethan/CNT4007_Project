package Threads;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client extends Thread {
    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String MESSAGE;

    public void run(){
        try{
            requestSocket = new Socket("localhost", 8000);
            System.out.println("Connected to localhost in port" + requestSocket.getPort());
            //initialize input and output stream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush(); // clears the output stream. 
            in = new ObjectInputStream(requestSocket.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        }

        catch (ConnectException e){
            System.err.println("Connection refused. You need to initiate a server first.");
        }

        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{ // Close connection

        try{
            in.close();
            out.close();
            requestSocket.close();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}

    void sendMessage(String message){
        try{
            out.writeObject(message);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]){
        Client client = new Client();
        client.run();
    }
}