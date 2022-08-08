package com.neu.multiThreading.server;

import java.io.IOException;
import java.net.Socket;

/**
 * A multiple threads server use TCP connection.
 */
public class TCPServer extends ServerImp implements Runnable {

    /**
     * create a socket
     */
    private Socket socket;

    /**
     * start server socket
     * 
     * @throws IOException
     */
    public TCPServer() throws IOException {
        super();
    }

    /**
     * The TCP server will listen to all connection of clients,
     * once connection established, create a thread to handle the client
     */
    @Override
    public void run() {
        while (true) {
            try {
                // wait for connection
                socket = serverSocket.accept();
                // if connected, start a thread
                SocketThread socketThread = new SocketThread(socket);
            } catch (IOException e) {
                System.out.println("Error in server initialization");
            }
        }
        
    } 
}
