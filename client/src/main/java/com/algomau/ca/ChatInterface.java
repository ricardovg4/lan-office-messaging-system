package com.algomau.ca;

import java.rmi.*;
import java.util.ArrayList;
import java.util.List;

public interface ChatInterface extends Remote{
    public boolean sendMessage(MessageInterface  message) throws RemoteException;
    public List<MessageInterface> getMessages(UserInterface user, String contact) throws RemoteException;
    public boolean registerUser(UserInterface user) throws RemoteException;
    public List<String> getClients() throws RemoteException;
    public List<String> getConnectedClients() throws RemoteException;
    public boolean isUser(String user) throws RemoteException;
    public boolean authenticateUser(UserInterface user) throws RemoteException;
    public boolean updateUserStatus(UserInterface user, String status) throws RemoteException;
    public boolean updateUserOnline(UserInterface user, boolean online) throws RemoteException;
    public String getUserStatus(String user) throws RemoteException;
}
