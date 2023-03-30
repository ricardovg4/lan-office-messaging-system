package com.algomau.ca;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class Server {
    private static String ip = "localhost";

    public static void main(String args[]) {
        try {
            Chat server = new Chat();
            ChatInterface serv_stub = (ChatInterface)

            UnicastRemoteObject.exportObject(server, 0);
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Naming.bind("rmi://" + ip + ":1099/IDS", serv_stub);
            System.out.println("Server running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
