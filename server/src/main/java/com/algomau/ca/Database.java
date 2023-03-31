package com.algomau.ca;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:";
    private static final String DB_FOLDER = "database/";
    private static final String DB_NAME = "messaging.db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private Connection connection;

    public Database() {
        // create db relative folder
        createDbFolder(DB_FOLDER);
        // connect to database
        connect();
        // create users table if non-existent
        createUsersTable();
        // create messsages table if non-existent
        createMessagesTable();
    }

    // check if db folder exists, if not create it
    private void createDbFolder(String folder) {
        Path currentPath = Paths.get("");
        Path newFolderPath = currentPath.resolve(folder);

        // check if folder already exists
        if (Files.exists(Paths.get(newFolderPath.toString()))) {
            // System.out.println("Folder already exists.");
        } else {
            try {
                Files.createDirectory(newFolderPath);
                // System.out.println("Folder created successfully.");
            } catch (IOException e) {
                System.err.println("Failed to create folder: " + e.getMessage());
            }
        }
    }

    // initiate db connection
    private void connect() {
        try {
            Class.forName(DB_DRIVER);
            // connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            connection = DriverManager.getConnection(DB_URL + DB_FOLDER + DB_NAME);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // will have two tables: users and messages

    /*
     * USERS
     */
    private void createUsersTable() {
        // SQL statement for creating a users table
        String createUsers = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	username text UNIQUE NOT NULL,\n"
                + "	password text NOT NULL,\n"
                + "	online integer NOT NULL,\n"
                + "	status text NOT NULL\n"
                + ");";

        try {
            Statement statement = connection.createStatement();
            statement.execute(createUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<String>();

        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT username FROM users";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("username");

                users.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public ArrayList<String> getConnectedUsers() {
        ArrayList<String> users = new ArrayList<String>();

        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT username FROM users WHERE online = 1";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("username");

                users.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean authenticateUser(String username, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("count") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertUser(UserInterface user) throws RemoteException {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (username, password, online, status) VALUES (?, ?, ?, ?)");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, "0");
            statement.setString(4, "");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateOnline(String username, boolean online) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE users SET online = ? WHERE username = ?");
            statement.setString(1, online ? "1" : "0");
            statement.setString(2, username);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean updateStatus(String username, String status) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE users SET status = (status) WHERE username = (username) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, status);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM users WHERE username = (username) VALUES (?)");
            statement.setString(1, username);
            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean userExists(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("count") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * MESSAGES
     */
    private void createMessagesTable() {
        // SQL statement for creating a messages table
        String createMessages = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	sender text NOT NULL,\n"
                + "	receiver text NOT NULL,\n"
                + "	message text NOT NULL,\n"
                + "	timestamp text NOT NULL,\n"
                + "	read integer NOT NULL\n"
                + ");";

        try {
            Statement statement = connection.createStatement();
            statement.execute(createMessages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean storeMessage(MessageInterface message) throws RemoteException {
        try {
            PreparedStatement statement = connection
                    .prepareStatement(
                            "INSERT INTO messages (sender, receiver, message, timestamp, read) VALUES (?,?,?,?,?)");

            statement.setObject(1, message.getSender());
            statement.setObject(2, message.getReceiver());
            statement.setObject(3, message.getMessage());
            statement.setObject(4, message.getTimestamp().toString());
            statement.setObject(5, message.isRead() == true ? 1 : 0);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MessageInterface> getMessages(UserInterface user, String contact) throws RemoteException {
        List<MessageInterface> messages = new ArrayList<MessageInterface>();

        try {
            String query = "SELECT * FROM messages WHERE (receiver = ? OR receiver = ?) AND (sender = ? OR sender = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, contact);
            statement.setString(3, user.getUsername());
            statement.setString(4, contact);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                String sender = resultSet.getString("sender");
                String receiver = resultSet.getString("receiver");
                String text = resultSet.getString("message");
                LocalDateTime timestamp = LocalDateTime.parse(resultSet.getString("timestamp"));
                Boolean read = resultSet.getBoolean("read");
                Message message = new Message(sender, receiver, text, read, timestamp);

                messages.add(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getAllMessages(UserInterface user) throws RemoteException {
        List<Message> messages = new ArrayList<Message>();

        try {
            PreparedStatement statement = connection
                    .prepareStatement(
                            "SELECT * FROM messages WHERE receiver = ?");

            statement.setObject(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();
            // statement.executeUpdate();

            while (resultSet.next()) {

                String sender = resultSet.getString("sender");
                String receiver = resultSet.getString("receiver");
                String text = resultSet.getString("message");
                LocalDateTime timestamp = LocalDateTime.parse(resultSet.getString("timestamp"));
                Boolean read = resultSet.getBoolean("read");
                Message message = new Message(sender, receiver, text, read, timestamp);

                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
