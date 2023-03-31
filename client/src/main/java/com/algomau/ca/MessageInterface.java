package com.algomau.ca;

import java.time.LocalDateTime;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageInterface extends Remote{
    public String getSender() throws RemoteException;
    public String getReceiver() throws RemoteException;
    public String getMessage() throws RemoteException;
    public LocalDateTime getTimestamp() throws RemoteException;
    public boolean isRead() throws RemoteException;
    public void setSender(String sender) throws RemoteException;
	public void setReceiver(String receiver) throws RemoteException;
	public void setMessage(String message) throws RemoteException;
	public void setTimestamp(LocalDateTime timestamp) throws RemoteException;
	public void setRead(boolean read) throws RemoteException;
}

