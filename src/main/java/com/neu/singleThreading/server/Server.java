package com.neu.singleThreading.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.neu.response.Message;
import com.neu.response.MessageType;

/**
 * The server will start in passive mode listening for a transmission from the
 * client. The client will then start and contact the server (on a given IP
 * address and port number). The client will pass the server a string (eg:
 * “network”) up to 80 characters in length.
 * On receiving a string from a client, the server should: 1) reverse all the
 * characters, and 2) reverse the capitalization of the strings (“network” would
 * now become “KROWTEN”).
 * The server should then send the string back to the client. The client will
 * display the received string and exit.
 */
public class Server {

    // sever socket and socket
    private static ServerSocket serverSocket;
    private static Socket socket;

    /**
     * Main method to run the program.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        try {
            // 1. create server socket
            serverSocket = new ServerSocket(32000);
            System.out.println("Sever is listening at port 32000 ...");
            socket = serverSocket.accept();
            // 2. create IO stream for data transfer
            // string reads in
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) objectInputStream.readObject();
            // 3. reverse string and capitalize
            String output = reverseAndCapitalizeString(message.getContent().toCharArray());
            // 4. send the string back to the client
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // write in
            Message response = new Message(-1, message.getSenderID(), message.getTime(), output, MessageType.NORMAL);
            objectOutputStream.writeObject(response);
            // close IO stream
            objectOutputStream.close();
            objectInputStream.close();
        } catch (IOException e) {
            System.out.println("Error in IO connection");
        } catch (ClassNotFoundException e) {
            System.out.println("Message class not found");
        } finally {
            // close socket
            try {
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("IO exception when close");
            }
            System.out.println("Sever exited ...");
        }
    }

    /**
     * Reverse a char array and return a capitalized string of the char array
     * 
     * @param charArr char array
     * @return string
     */
    public static String reverseAndCapitalizeString(char[] charArr) {
        // create a result array with the same length of the char array
        char[] result = new char[charArr.length];
        for (int i = charArr.length - 1; i >= 0; i--) {
            // assign the value of the char array to the result array reversely
            result[result.length - 1 - i] = charArr[i];
        }
        // return the capitalized string
        return new String(result).toUpperCase();
    }

}
