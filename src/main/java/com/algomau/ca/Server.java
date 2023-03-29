package com.algomau.ca;

import java.util.ArrayList;
import java.util.List;


public class Server {

    public static void main(String args[]) {
        Database db = new Database();
        User brad = new User("brad", "password");
        db.insertUser(brad, 0);
        System.out.println(
                db.authenticateUser("bradi", "password"));

        Message m = new Message("bradi", "bradi", "Hello there.", false);
        db.storeMessage(m);
        db.storeMessage(m);
        db.storeMessage(m);
        db.storeMessage(m);

        // List<Message> messages = db.getMessages(brad);
        // messages.forEach((n) -> System.out.println(n.getTimestamp() + "\t" + n.getMessage()));
    }
}
