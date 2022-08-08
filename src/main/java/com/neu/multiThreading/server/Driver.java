package com.neu.multiThreading.server;

import java.io.IOException;
import java.util.Queue;

/**
 * Driver to power on the multiple thread server
 */
public class Driver {
    
    public static void main(String[] args) throws IOException {
        new TCPServer().run();
    }
}
