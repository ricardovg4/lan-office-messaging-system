package com.algomau.ca;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        // if on lan, insert IP of server
        String ip = "localhost";
        boolean online = false;
        String name;
        Scanner input = new Scanner(System.in);
        String userInput;
        String menu = "This is the help menu...";

        try {
            ChatInterface server = (ChatInterface) Naming.lookup("rmi://" + ip + ":1099/IDS");
            System.out.println("Connected to the server.");

            while (true) {
                userInput = input.nextLine().trim();
                if (userInput.equalsIgnoreCase("help")) {
                    System.out.println(menu);
                } else if (userInput.equalsIgnoreCase("register")) {
                    System.out.println("Enter username to register");
                    String username = input.nextLine().trim();
                    System.out.println("Enter password to register");
                    String password = input.nextLine().trim();
                    UserInterface user = new User(username, password);
                    // UserInterface user = (UserInterface) new User(username, password);
                    server.registerUser(user);
                } else if (userInput.equalsIgnoreCase("show")) {
                    System.out.println(server.getClients());
                } else if (userInput.equalsIgnoreCase("exit")) {
                    // add remove user from connected
                    // TODO
                    UnicastRemoteObject.unexportObject(server, false);
                    input.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }
}
