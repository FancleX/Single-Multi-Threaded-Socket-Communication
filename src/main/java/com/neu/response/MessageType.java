package com.neu.response;

/**
 * Message type of a message.
 * REGISTRY: a client tries to connect to the server and will be registered in the server.
 * NORMAL: normal message.
 * CLOSE: a client tries to exit.
 */
public enum MessageType {
    REGISTRY, NORMAL, CLOSE
}
