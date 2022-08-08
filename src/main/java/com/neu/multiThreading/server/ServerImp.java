package com.neu.multiThreading.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Implementation of Server.
 */
public abstract class ServerImp implements Server {

    // create a server socket
    protected ServerSocket serverSocket;

    /**
     * Initialize the server socket
     * 
     * @throws IOException
     */
    public ServerImp() throws IOException {
        serverSocket = new ServerSocket(30000);
        System.out.println("MultipleThread server is listening at port 30000 ...");
    }

    // @Override
    // public void sendMessageTo(SocketThread destination, Message message) {
    //     
    // }
    
    
    // @Override
    // public void close() {
    //     try {
    //         serverSocket.close();
    //     } catch (IOException e) {
    //         System.out.println("Unable to close the server.");
    //     }
    // }

}
