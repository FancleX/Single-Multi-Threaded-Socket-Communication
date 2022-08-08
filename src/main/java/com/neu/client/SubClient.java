package com.neu.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.format.DateTimeFormatter;

import com.neu.response.Message;

/**
 * SubClient class is a subthread of client to keep reading message from server.
 */
public class SubClient extends Thread {
    
    // take inpute stream
    private ObjectInputStream in;

    // determine if client wants to exit
    private boolean isExit = false;

    /**
     * Take input stream and start this thread
     * 
     * @param in object input stream
     */
    public SubClient(ObjectInputStream in) {
        this.in = in;
        this.start();
    }

    /**
     * Reading from server and print out the response.
     */
    @Override
    public void run() {
        while (!isExit) {
            try {
                // read from server
                readFromServer();
            } catch (IOException e) {
                System.out.println("Error in input connection");
            } catch (ClassNotFoundException e) {
                System.out.println("Message class not found");
            }
        }
        // if client intends to close 
        Client.subClientClose();
    }

    /**
     * Read message from server.
     * 
     * @throws IOException connection problem in IO
     * @throws ClassNotFoundException Message class not found
     */
    public void readFromServer() throws IOException, ClassNotFoundException {
        // reading
        Message message = (Message) in.readObject();
        // check the type of the message to display in different way
        switch (message.getMessageType()) {
            case NORMAL:
                // print message
                messageView(message);
                break;
                // client intends to close
            case CLOSE:
                isExit = true;
                break;
            default:
                break;
        }
    }

    /**
     * Print message.
     * 
     * @param message message to be printed
     */
    public void messageView(Message message) {
        switch (message.getSenderID()) {
            // message from server
            case -1:
                System.out.println("Sever: " + message.getContent() + " at time " + message.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                break;
            // message from other clients
            default:
                System.out.println("Client " + message.getSenderID() + ": " + message.getContent() + " at time " + message.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                break;
        }
    }
}
