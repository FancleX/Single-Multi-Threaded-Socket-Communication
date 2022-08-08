package com.neu.response;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The message class defines the message in communication between client and server.
 */
public class Message implements Serializable {

    private static final Long serialVersionUID = 1L;

    // sender of the message
    // the id of server is -1 by default
    private int senderID;

    // receiver of the message, if -1, server give an echo to this client
    // if 0, server broadcast to all connected clients
    // else send to the receiver
    private int receiverID;

    // time of the message send out
    private LocalDateTime time;

    // the content of message
    private String content;

    // message type
    private MessageType messageType;

    public Message() {}

    public Message(int senderID, int receiverID, LocalDateTime time, String content, MessageType messageType) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.time = time;
        this.content = content;
        this.messageType = messageType;
    }

    public int getSenderID() {
        return this.senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getReceiverID() {
        return this.receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "{" +
            " senderID='" + getSenderID() + "'" +
            ", receiverID='" + getReceiverID() + "'" +
            ", time='" + getTime() + "'" +
            ", content='" + getContent() + "'" +
            ", messageType='" + getMessageType() + "'" +
            "}";
    }

}
