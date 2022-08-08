package com.neu.multiThreading.server;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class ThreadManagement will manage the multiple threads in the server.
 */
public class ThreadManagement {

    // use a hashmap to control threads
    private static Map<Integer, SocketThread> threadPool = new HashMap<>();

    /**
     * Add a client thread.
     * 
     * @param id     the ID of the client
     * @param thread the thread of the client
     */
    public static void add(Integer id, SocketThread thread) {
        threadPool.put(id, thread);
    }

    /**
     * Get a thread by client ID.
     * 
     * @param id the client ID
     * @return the client thread
     */
    public static SocketThread getThreadByClientID(Integer id) {
        return threadPool.get(id);
    }

    /**
     * Determine if the client is currently connecting with the server.
     * 
     * @param id the client ID
     * @return true if the client is in connecting, otherwise false
     */
    public static boolean isClientIDAvailable(Integer id) {
        if (threadPool.containsKey(id)) {
            return true;
        }
        return false;
    }

    /**
     * Get all the current threads except the specific target. Use for message
     * broadcasting,
     * send message to other clients except the sender.
     * 
     * @param exceptID the ID of the exception
     * @return A map of client IDs and threads
     */
    public static Map<Integer, SocketThread> getAllThreads(Integer exceptID) {
        return threadPool.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(exceptID))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the client ID by its thread.
     * 
     * @param thread the thread to be found
     * @return the ID of the client
     */
    public static Integer getCliendIDByThread(SocketThread thread) {
        return threadPool.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(thread))
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }

    /**
     * Remove a thread by client ID.
     * 
     * @param id the client ID to be removed
     */
    public static void removeThreadByClientID(Integer id) {
        threadPool.remove(id);
    }

    /**
     * Check if someone is connected.
     * 
     * @return false if nobody connected, else true
     */
    public static boolean isEmpty() {
        return threadPool.isEmpty();
    }

}
