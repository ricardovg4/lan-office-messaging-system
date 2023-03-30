package com.algomau.ca;

import java.rmi.*;
import java.util.ArrayList;
import java.util.List;

public class Chat implements ChatInterface {
    private ArrayList<String> clients;
    private List<User> connectedClients;
    private Database db;

    public Chat(){
        db = new Database();
        clients = db.getUsers();
    }

    // sends a message
    public boolean sendMessage(Message message) throws RemoteException {
        boolean store_message = db.storeMessage(message);
        // insert rmi send to respective client after storing
        return store_message;
    }

    public List<Message> getMessages(User user) throws RemoteException{
        return db.getMessages(user);
    }

    public boolean registerUser(User user) throws RemoteException {
        return true;
    }


}