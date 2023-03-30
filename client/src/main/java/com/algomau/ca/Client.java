package com.algomau.ca;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        // if on lan, insert IP of server
        String ip = "localhost";
        boolean connected = false;
        String username;
        Scanner input = new Scanner(System.in);
        String userInput;

        String menu = "This is the help menu...";

        UserInterface user = null;

        try {
            ChatInterface server = (ChatInterface) Naming.lookup("rmi://" + ip + ":1099/IDS");
            System.out.println("Connected to the server");
            System.out.println("Type login or register...");

            while (!connected) {
                userInput = input.nextLine().trim();

                // REGISTER NEW USER
                if (userInput.equalsIgnoreCase("register")) {
                // } else if (userInput == "login") {
                    // get username and check if already exists in db
                    System.out.println("Enter username:");
                    username = input.nextLine().trim();

                    // if user already exists, prompt new username
                    while (server.isUser(username)) {
                        System.out.println("User " + username + "already exists, please try with a different name");
                        username = input.nextLine().trim();
                    }

                    // ask password
                    System.out.println("Enter password:");
                    String password = input.nextLine().trim();
                    user = new User(username, password);
                    server.registerUser(user);
                    connected = true;
                }

                // LOGIN EXISTENT USER
                else if (userInput.equalsIgnoreCase("login")) {
                    System.out.println("Enter username:");
                    username = input.nextLine().trim();

                    // check if user doesn't exist, if so keep prompting for correct username
                    while (!server.isUser(username)) {
                        System.out.println(username + " doesn't exist.");
                        username = input.nextLine().trim();
                    }

                    // ask password
                    System.out.println("Enter password:");
                    String password = input.nextLine().trim();

                    // check if password is correct
                    UserInterface tmpUser = new User(username, password);
                    while (!server.authenticateUser(tmpUser)) {
                        System.out.println("Incorrect password");
                        username = input.nextLine().trim();
                        tmpUser = new User(username, password);
                    }

                    user = new User(username, password);
                    connected = true;
                }

            }
            // LOGGED IN
            System.out.println("Logged in!");
            System.out.println("Welcome " + user.getUsername());
            System.out.println("Registered users are: ");
            server.updateUserOnline(user, true);

            while (connected) {
                userInput = input.nextLine().trim();
                if (userInput.equalsIgnoreCase("help")) {
                    System.out.println(menu);
                } else if (userInput.equalsIgnoreCase("clients")) {
                    System.out.println(server.getClients());
                } else if (userInput.equalsIgnoreCase("online clients")) {
                    System.out.println(server.getConnectedClients());
                } else if (userInput.equalsIgnoreCase("exit")) {
                    // remove user from connected
                    server.updateUserOnline(user, false);
                    input.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
