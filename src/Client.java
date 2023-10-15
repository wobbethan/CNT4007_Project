import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import utils.Handshake;
public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server

	public void Client() {}

	void run()
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Enter ID: "); //Enter ID
			String peerID = bufferedReader.readLine();
			Handshake clientSide = new Handshake(Integer.valueOf(peerID)); //Create Handshake
			sendHandshake(clientSide); //Send Handshake

			Handshake serverSide = (Handshake)in.readObject(); //Receive Handshake back from Server
			if(clientSide.handshakeBytes[0] == 0){ //Validate Handshake by checking header, zero bits, and ID == 0000
				//throw exeception if invalid
			}
			ArrayList<String> connectionsList = (ArrayList<String>)in.readObject();//Receive List of connections to server
			//Print Connections
			System.out.println("Successfully Connected to Server");
			System.out.println("Clients connected to Server: "); //Enter ID
			for (int i = 0; i < connectionsList.size(); i++) { 		      
				System.out.println( (i+1) +": "+ connectionsList.get(i)); 		
			} 
			//Messages

		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        } 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
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
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//send a handshake
	void sendHandshake(Handshake hs)
	{
		try{
			out.writeObject(hs);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//main method
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}

}
