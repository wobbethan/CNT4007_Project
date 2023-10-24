package Threads;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server extends Thread {
	private int portNum;
	private int peerID;

	public Server(int portNum, int peerID) {
		this.portNum = portNum;
		this.peerID = peerID;
	}

	@Override
	public void run() {
		try {
			ServerSocket listenerSocket = new ServerSocket(portNum);

			while (true) {
				Socket clientSocket = listenerSocket.accept();

				System.out.println("client connected!");

				// TODO: receive client handshake

				// TODO: send handshake

				// TODO: check client handshake valid
				
				// TODO: check client id in handshake is contained within config file

				// TODO: send server bitfield to client

				// TODO: receive bitfield from client

				// TODO: log tcp connection established

				// TODO: maybe find some way to track all peers that have a file				
				
				// TODO: spawn send message thread
				
				// TODO: spawn request piece thread
				
				// TODO: spawn receive message thread

			}
		} catch (IOException e) {
			System.err.println(e);
		}

	}
}
