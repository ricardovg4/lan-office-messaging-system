package com.algomau.ca;

import java.time.LocalDateTime;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Message extends UnicastRemoteObject implements MessageInterface {
    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Message(String sender, String receiver, String message, boolean read, LocalDateTime timestamp)
            throws RemoteException {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public Message(String sender, String receiver, String message, boolean read) throws RemoteException {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.read = read;
    }

    public String getSender() throws RemoteException {
        return sender;
    }

    public String getReceiver() throws RemoteException {
        return receiver;
    }

    public String getMessage() throws RemoteException {
        return message;
    }

    public LocalDateTime getTimestamp() throws RemoteException {
        return timestamp;
    }

    public boolean isRead() throws RemoteException {
        return read;
    }

    public void setSender(String sender) throws RemoteException {
        this.sender = sender;
    }

    public void setReceiver(String receiver) throws RemoteException {
        this.receiver = receiver;
    }

    public void setMessage(String message) throws RemoteException {
        this.message = message;
    }

    public void setTimestamp(LocalDateTime timestamp) throws RemoteException {
        this.timestamp = timestamp;
    }

    public void setRead(boolean read) throws RemoteException {
        this.read = read;
    }

}
