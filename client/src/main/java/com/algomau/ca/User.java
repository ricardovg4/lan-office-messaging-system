package com.algomau.ca;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;

public class User extends UnicastRemoteObject implements UserInterface {

    private String username;
    private String password;
    // private Key publicKey;

    public User(String username, String password) throws RemoteException{
        this.username = username;
        this.password = password;
        // this.publicKey = publicKey;
    }

    public String getUsername() throws RemoteException { // getting user name
        return username;
    }

    public String getPassword() throws RemoteException { // getting user password
        return password;
    }

    // public Key getPublicKey() { //getting Public Key
    // return publicKey// }
}
