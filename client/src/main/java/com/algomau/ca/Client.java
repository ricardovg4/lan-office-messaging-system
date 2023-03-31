package com.algomau.ca;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Client extends Application{
	
	public Scene scene;
	public int screenX;
	public int screenY;
	public Label info;
	
	public String ip = "localhost";
	public boolean connected = false;
	
	public ChatInterface server;
	public String username;
	public Scanner input = new Scanner(System.in);
	public  String userInput;

	public String help = "\nCommands: help, clients, online clients, message:<username>:<message> exit";

    UserInterface user = null;
    
    boolean isLogin;
    
	public void start(Stage primaryStage) {
		this.screenX = 1280;
		this.screenY = 720;
		
		//Create pane
		BorderPane pane = new BorderPane();
		HBox infoText = new HBox();
		this.info = new Label();
		infoText.getChildren().add(this.info);
	    pane.setCenter(mainScene());
	    pane.setTop(infoText);
	    
		//Create the scene and set in stage
		this.scene = new Scene(pane, this.screenX, this.screenY);
		primaryStage.setTitle("LOMS");
		primaryStage.setScene(scene);// Place scene in the stage
		primaryStage.show(); //Display the stage
		ConnectToServer();
	}
	
	public VBox mainScene() {
		VBox pane = new VBox();
		pane.setPadding(new Insets(10,10,10,10));
		pane.setStyle("-fx-background-color: #336699;");
		
		
		TextField username = new TextField();
		username.setMaxSize(120, 20);
		username.setTranslateX(this.screenX /2 -50);
		username.setTranslateY(screenY/ 3);
		TextField password = new TextField();
		password.setTranslateX(this.screenX /2 -50);
		password.setMaxSize(120, 20);
		password.setTranslateY(screenY/ 3 + 50);
		
		Button login = new Button();
		login.setText("Login");
		login.setPrefSize(120, 30);
		login.setTranslateX(this.screenX /2 -50);
		login.setTranslateY(this.screenY/ 2);
		Button register = new Button();
		register.setText("Register");
		register.setPrefSize(120, 30);
		register.setTranslateX(this.screenX /2 -50);
		register.setTranslateY(this.screenY/ 2);
		
		Button confirm = new Button();
		confirm.setText("confirm");
		confirm.setPrefSize(120, 30);
		confirm.setTranslateX(this.screenX /2 - 50);
		confirm.setTranslateY(this.screenY/ 2);
		
		confirm.setOnAction(e -> {
			if(this.isLogin) {
				 // LOGIN EXISTENT USER
                this.info.setText("Enter Username and Password");
                try {
                // check if user doesn't exist, if so keep prompting for correct username
                if(!this.server.isUser(username.getText())) {
                	this.info.setText(username.getText() + " doesn't exist.");
                }
                // check if password is correct
                UserInterface tmpUser = new User(username.getText(), password.getText());
              
					if(this.server.authenticateUser(tmpUser)) {
						this.user = new User(username.getText(), password.getText());
		                this.connected = true;
		                this.info.setText("Welcome: " + username.getText());
		                pane.getChildren().clear();
					    
					}else if(!this.server.authenticateUser(tmpUser)){
						this.info.setText("Incorrect password");
					    tmpUser = new User(username.getText(), password.getText());
					}
						
					
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {
				 // REGISTER NEW USER
	            try {
					if(this.server.isUser(username.getText())) {
						this.info.setText("User " + username.getText() + " already exists, please try with a different name");
					}// if user already exists, prompt new username
					else if(username.getText().equals("") || password.getText().equals("")){
						this.info.setText("Please enter both a username and password");
					}
					else {
						this.info.setText("User " + username.getText() + " created");
						this.user = new User(username.getText(), password.getText());
			            this.server.registerUser(user);
			            this.connected = true;
			            pane.getChildren().clear();
					}
	            }
			 catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		login.setOnAction(e -> {
			this.isLogin = true;
			pane.getChildren().clear();
			pane.getChildren().addAll(username, password, confirm);
			 
            
		});
		
		register.setOnAction(e -> {
			this.isLogin = false;
			pane.getChildren().clear();
			pane.getChildren().addAll(username, password, confirm);
			
		});
		
		pane.getChildren().addAll(login, register);
		
		return pane;
		
		
	}
	
    public void ConnectToServer() {
    	  // if on lan, insert IP of server
        try {
            this.server = (ChatInterface) Naming.lookup("rmi://" + ip + ":1099/IDS");
            this.info.setText("Connected to the server");
        } catch (Exception e) {
        e.printStackTrace();
        }
	/*
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
                    // check if recipient exists
                    if (!server.isUser(recipient)) {
                        System.out.println(recipient + " doesn't exist");
                    } else {
                        String text = userInput.split(":")[2];
                        MessageInterface message = new Message(user.getUsername(), recipient, text.trim(), false);
                        server.sendMessage(message);
                    }
                } else if (userInput.startsWith("history")) {
                    String recipient = userInput.split(":")[1];
                    // check if recipient exists
                    if (!server.isUser(recipient)) {
                        System.out.println(recipient + " doesn't exist");
                    } else {
                        List<MessageInterface> messages;

                        messages = server.getMessages(user, recipient);
                        System.out.println(messages.size());
                        // MessageInterface m = messages.get(0);
                        // System.out.println(m.getMessage());

                        messages.forEach(m -> {
                            try {
                                System.out.println("______________________________");
                                System.out.println("sender:"+  m.getSender() );
                                System.out.println("recipient:"+  m.getReceiver() );
                                System.out.println("message:"+  m.getMessage() );
                                System.out.println("timestamp:"+  m.getTimestamp() );
                                System.out.println("______________________________");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

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
*/
    }
    }
