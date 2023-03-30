package com.algomau.ca;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserInterface extends Remote{

    public String getUsername() throws RemoteException;

    public String getPassword() throws RemoteException;
}
