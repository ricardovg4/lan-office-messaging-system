package com.algomau.ca;

import java.rmi.*;
import java.util.ArrayList;
import java.util.List;

public class Chat implements ChatInterface {
    private List<String> clients;
    private List<String> connectedClients;
    private Database db;

    public Chat() {
        db = new Database();
        clients = db.getUsers();
        connectedClients = db.getConnectedUsers();
    }

    // sends a message
    public boolean sendMessage(Message message) throws RemoteException {
        boolean store_message = db.storeMessage(message);
        // insert rmi send to respective client after storing
        return store_message;
    }

    public List<Message> getMessages(User user) throws RemoteException {
        return db.getMessages(user);
    }

    public boolean registerUser(UserInterface user) throws RemoteException {
        try {
            db.insertUser(user);
            clients.add(user.getUsername());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> getConnectedClients() throws RemoteException {
        return connectedClients;
    }

    public List<String> getClients() throws RemoteException {
        return clients;
    }

    public boolean isUser(String user) throws RemoteException {
        return db.userExists(user);
    }

    public boolean authenticateUser(UserInterface user) throws RemoteException {
        try {
            return db.authenticateUser(user.getUsername(), user.getPassword());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserStatus(UserInterface user, String status) throws RemoteException {
        try {
            return db.updateStatus(user.getUsername(), status);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserOnline(UserInterface user, boolean online) throws RemoteException {
        try {
            // add to connected clients list if online
            if (online) {
                connectedClients.add(user.getUsername());
            // remove from connected clients list if offline
            } else if (!online){
                connectedClients.remove(user.getUsername());
            }

            // update db
            db.updateOnline(user.getUsername(), online);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
