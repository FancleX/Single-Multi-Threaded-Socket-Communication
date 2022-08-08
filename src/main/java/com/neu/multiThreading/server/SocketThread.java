package com.neu.multiThreading.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import com.neu.response.Message;
import com.neu.response.MessageType;

/**
 * A thread holds the socket to handle client reactions.
 */
public class SocketThread extends Thread {

    // socket that to be handled
    private Socket socket;

    // create IO stream 
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    /**
     * Receiver a socket and start the thread
     * 
     * @param socket socket
     */
    public SocketThread(Socket socket) {
        this.socket = socket;
        this.start();
    }

    /**
     * The thread will listen to messages from the socket,
     * and classify and handle the client's requests like echo, broadcast, send to another client.
     * It will be terminated, once the client exited.
     */
    @Override
    public void run() {
        boolean isClose = false;
        try {
            // create IO stream
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while (!isClose) {
                Message message = (Message) objectInputStream.readObject();
                // check message type
                MessageType type = message.getMessageType();
                switch (type) {
                    // register the client in the server
                    case REGISTRY:
                        // server assigns an ID to the client
                        Random random = new Random();
                        int ID = random.nextInt(10000) + 1;
                        ThreadManagement.add(ID, this);
                        // send back ID to the client
                        Message response = new Message(-1, message.getSenderID(), LocalDateTime.now(), Integer.toString(ID),
                                MessageType.REGISTRY);
                        sendMessage(-1, response, objectOutputStream);
                        System.out.println("Client " + ID + " connects to the server ...");
                        break;
                    // normal communication
                    case NORMAL:
                        // check receiver
                        // if -1, send back to the client
                        if (message.getReceiverID() == -1) {
                            Message response1 = new Message(-1, message.getSenderID(), LocalDateTime.now(),
                                    message.getContent(), MessageType.NORMAL);
                            sendMessage(-1, response1, objectOutputStream);
                        }
                        // if 0, broadcast to all available clients
                        else if (message.getReceiverID() == 0) {
                            sendMessage(0, message, objectOutputStream);
                        }
                        // if a specific id, send to the client if the client is available, 
                        // otherwise, send a message to the client:
                        // the receiver is not available now
                        else if (ThreadManagement.isClientIDAvailable(message.getReceiverID())) {
                            sendMessage(1, message, objectOutputStream);
                        } else {
                            Message response2 = new Message(-1, message.getSenderID(), message.getTime(),
                                    "Sorry, the receiver is not available now.", MessageType.NORMAL);
                            sendMessage(-1, response2, objectOutputStream);
                        }
                        break;
                    // client exits, shutdown the loop and send a message to the client that the client can exit safely
                    case CLOSE:
                        // loop close
                        isClose = true;
                        // message to client to close client thread
                        Message onClose = new Message(-1, message.getSenderID(), LocalDateTime.now(), null, MessageType.CLOSE);
                        sendMessage(-1, onClose, objectOutputStream);
                        System.out.println("Client " + ThreadManagement.getCliendIDByThread(this) + " exited ...");
                        break;
                }
            }
            // close IO stream
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            // catch error if the cient suddently losses the connection
            System.out.println("Client " + ThreadManagement.getCliendIDByThread(this) + " exited abnormally ...");
        } catch (ClassNotFoundException e) {
            System.out.println("Message class not found");
        }
        // remove the thread from thread management if the client exited
        ThreadManagement.removeThreadByClientID(ThreadManagement.getCliendIDByThread(this));
        // close socket
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error in close socket");
        }
    }

    /**
     * Send a message to clients.
     * 
     * @param type -1: is an echo case, server send the echo to the client; 
     * 0: broadcast the client message to all avaiable clients;
     * 1: send the client message to a specific client
     * @param message message to be sent
     * @param objectOutputStream the output stream of the receiver
     * @throws IOException exception when connection is lost
     */
    public void sendMessage(int type, Message message, ObjectOutputStream objectOutputStream) throws IOException {
        switch (type) {
            // echo case
            case -1:
                // write
                objectOutputStream.writeObject(message);
                break;
            // broadcast case
            case 0:
                Map<Integer, SocketThread> threads = ThreadManagement.getAllThreads(message.getSenderID());
                threads.values().stream().forEach(a -> {
                    try {
                        sendMessage(-1, message, a.getObjectOutputStream());
                    } catch (IOException e) {
                        System.out.println("Error in IO");
                    }
                });
                break;
            // send to someone
            case 1:
                SocketThread thread = ThreadManagement.getThreadByClientID(message.getReceiverID());
                thread.sendMessage(-1, message, thread.getObjectOutputStream());
        }
    }

    /**
     * Get the socket of the thread
     * 
     * @return socket of the thread
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Get the object output stream of the thread
     * 
     * @return the object output stream of the thread
     */
    public ObjectOutputStream getObjectOutputStream() {
        return this.objectOutputStream;
    }

    /**
     * Get the object input stream of the thread
     * 
     * @return
     */
    public ObjectInputStream getObjectInputStream() {
        return this.objectInputStream;
    }
 
}
