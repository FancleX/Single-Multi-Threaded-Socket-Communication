package com.neu.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.neu.response.Message;
import com.neu.response.MessageType;

/**
 * Client to connect with single thread server or multiple threads server.
 */
public class Client {
    
    // create socket
    private static Socket socket;

    // ID of the client that assigned by the server
    private static int ID;

    // determine if the client wants to exit, 
    // it uses for close reading thread of the client that communicates with multiple threads server
    private static boolean closeSubThread = false;

    /**
     * Main method to run the client.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        try {
            // 1. create a socket and IO stream
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream objectOutputStream; 
            ObjectInputStream objectInputStream;
            // let user to choose which server to connect
            int server = chooseServer(scanner);
            int port;
            // single thread server will do an echo, then both of the client and server will exit
            if (server == 1) {
                port = 32000;
                socket = new Socket(InetAddress.getLocalHost(), port);
                System.out.println("Client started");
                // Client send message to server
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                String input = checkValidation(scanner);
                Message message = new Message(0, -1, LocalDateTime.now(), input, MessageType.NORMAL);
                objectOutputStream.writeObject(message);
                // start listening here
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message response = (Message) objectInputStream.readObject();
                System.out.println("Server: " + response.getContent() + " at time " + response.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                // multiple threads server will allow the client to do more operations
                port = 30000;
                socket = new Socket(InetAddress.getLocalHost(), port);
                System.out.println("Client started");
                // start output stream
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                // 2. register the client at server
                Message registry = new Message(0, 0, LocalDateTime.now(), null, MessageType.REGISTRY);
                objectOutputStream.writeObject(registry);
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                getRegistryInfo(objectInputStream);
                // 3. start the reading thread
                SubClient subClient = new SubClient(objectInputStream);
                // 4. let user choose options to communicate
                boolean isExit = false;
                while (!isExit) {
                    // write to server
                    int result = getUserInputAndDoOperations(objectOutputStream, scanner);
                    if (result == 0) {
                        isExit = true;
                        // if client wants to exit close the subthread first
                        while (true) {
                            if (closeSubThread) {
                                break;
                            }
                        }
                    }
                }
            }
            // 5. close IO stream and scanner
            scanner.close();
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error in IO connection");
        } catch (ClassNotFoundException e) {
            System.out.println("Didn't find Message.class");
        } finally {
            try {
                // close socket
                socket.close();
            } catch (IOException e) {
                System.out.println("IO exception when close");
            }
            System.out.println("Client exited ...");
        }
    }

    /**
     * Get user input and perform actions.
     * 
     * @return an int of status of the current action
     */
    public static int getUserInputAndDoOperations(ObjectOutputStream out, Scanner scanner) {
        String str;
        Message message;
        int option;
        System.out.println("Please choose an integer to use the function: \n1. get an echo with the server \n2. broadcast a message to all clients that connect to the server \n3. send a message to a client \n4. exit");
        // take user input
        try {
            str = scanner.nextLine();
            option = Integer.parseInt(str);
        } catch (Exception e) {
            // if an invalid input, give a default option number to go back to loop
            option = -1;
        }
        // perform function
        switch (option) {
            // get an echo with the server
            case 1:
                str = checkValidation(scanner);
                // send the message to server
                message = new Message(ID, -1, LocalDateTime.now(), str, MessageType.NORMAL);
                sendMessage(message, out);
                return 1;
            // broadcast a message to all clients that connect to the server
            case 2:
                str = checkValidation(scanner);
                message = new Message(ID, 0, LocalDateTime.now(), str, MessageType.NORMAL);
                sendMessage(message, out);
                return 1;      
            // send a message to a client       
            case 3:
                int receiverID;
                while (true) {
                    System.out.println("Please input the ID of the client: ");
                    try {
                        receiverID = Integer.parseInt(scanner.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println("Note: ID are integers!");
                    }                    
                }
                str = checkValidation(scanner);
                message = new Message(ID, receiverID, LocalDateTime.now(), str, MessageType.NORMAL);
                sendMessage(message, out);
                return 1;
            // client exits
            case 4:
                message = new Message(ID, -1, LocalDateTime.now(), null, MessageType.CLOSE);
                sendMessage(message, out);
                return 0;
            // default for invalid option 
            default:
                System.out.println("Please choose an legal integer to use the function!");
                return -1;
        }
    }

    /**
     * Send a message to server.
     * 
     * @param message message to be sent
     * @param objectOutputStream the object output stream of the client
     */
    public static void sendMessage(Message message, ObjectOutputStream objectOutputStream) {
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            System.out.println("Error in IO connection");
        }
    }

    /**
     * Check user input.
     * 
     * @param scanner scanner
     * @return a valid string
     */
    public static String checkValidation(Scanner scanner) {
        while (true) {
            System.out.println("Please input a message to sever (up to 80 characters): ");
                String str = scanner.nextLine();
                // check the length
                if ((str.length() > 80) || (str.length() == 0)) {
                    System.out.println("string up to 80 characters and cannot be empty!");
                } else {
                    return str;
                }
            }
    }

    /**
     * Client registers at server.
     * 
     * @param in the object input stream of the client
     * @throws IOException IOException when loss connection with server
     * @throws ClassNotFoundException Message class not found in this case
     */
    public static void getRegistryInfo(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Message registryMessage = (Message) in.readObject();
        ID = Integer.parseInt(registryMessage.getContent());
        System.out.println("Your ID is " + ID);
    }

    /**
     * Close the reading thread of the client.
     */
    public static void subClientClose() {
        closeSubThread = true;
    }

    /**
     * Let user to choose a server to connect.
     * 
     * @param scanner scanner
     * @return the choice
     */
    public static int chooseServer(Scanner scanner) {
        // choose server to connect
        while (true) {
            System.out.println("Please choose a server to connect: 1. single thread server 2. multiple threads server");
                String str = scanner.nextLine();
                try {
                    int res = Integer.parseInt(str);
                    if (res == 1 || res == 2) {
                        return res;
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input, please choose 1 or 2.");
                }
        }
    }
}
