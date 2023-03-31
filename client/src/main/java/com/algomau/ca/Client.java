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

        String help = "\nCommands: help, clients, online clients, message:<username>:<message> exit";

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
            server.updateUserOnline(user, true);
            System.out.println(help);

            while (connected) {
                userInput = input.nextLine().trim();
                if (userInput.equalsIgnoreCase("help")) {
                    System.out.println(help);
                } else if (userInput.equalsIgnoreCase("clients")) {
                    System.out.println(server.getClients());
                } else if (userInput.equalsIgnoreCase("online clients")) {
                    System.out.println(server.getConnectedClients());

                } else if (userInput.startsWith("message")) {
                    String recipient = userInput.split(":")[1];

                    // String recipient = userInput.substring(1, userInput.indexOf(":")).trim();
                    // check if recipient exists
                    if (!server.isUser(recipient)) {
                        System.out.println(recipient + " doesn't exist");
                    } else {
                        // String text = userInput.substring(2, userInput.indexOf(":")).trim();
                        String text = userInput.split(":")[2];
                        MessageInterface message = new Message(user.getUsername(), recipient, text.trim(), false);
                        server.sendMessage(message);
                    }

                } else if (userInput.equalsIgnoreCase("exit")) {
                    // remove user from connected
                    server.updateUserOnline(user, false);
                    input.close();
                    connected = false;
                    System.out.println("Goodbye");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
