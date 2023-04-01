package com.algomau.ca;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Client extends Application {

	public Scene scene;
	public int screenX;
	public int screenY;
	public Label info;

	public String ip = "localhost";
	public boolean connected = false;

	public ChatInterface server;
	public String username;
	public Scanner input = new Scanner(System.in);
	public String userInput;

	public String help = "Send a message to a User on this list";

	UserInterface user = null;

	public boolean isLogin;

	public BorderPane mainPane;

	public int userIndex = -1;

	public void start(Stage primaryStage) {

		this.screenX = 1280;
		this.screenY = 720;

		// Create pane
		this.mainPane = new BorderPane();
		HBox infoText = new HBox();
		this.info = new Label();
		infoText.getChildren().add(this.info);
		this.mainPane.setCenter(mainScene());
		this.mainPane.setTop(infoText);

		// Create the scene and set in stage
		this.scene = new Scene(this.mainPane, this.screenX, this.screenY);
		primaryStage.setTitle("LOMS");
		primaryStage.setScene(scene);// Place scene in the stage
		primaryStage.show(); // Display the stage

		ConnectToServer();
		Update();
	}

	public VBox mainScene() {
		this.statusOptions = new ComboBox<String>();;
		this.statusOptions.getItems().add("Online");
		this.statusOptions.getItems().add("Away");
		this.statusOptions.getItems().add("Busy");
		this.statusOptions.getSelectionModel().selectFirst();
		VBox pane = new VBox();
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setStyle("-fx-background-color: #336699;");

		TextField username = new TextField();
		username.setMaxSize(120, 20);
		username.setTranslateX(this.screenX / 2 - 50);
		username.setTranslateY(screenY / 3);
		TextField password = new TextField();
		password.setTranslateX(this.screenX / 2 - 50);
		password.setMaxSize(120, 20);
		password.setTranslateY(screenY / 3 + 50);

		Button login = new Button();
		login.setText("Login");
		login.setPrefSize(120, 30);
		login.setTranslateX(this.screenX / 2 - 50);
		login.setTranslateY(this.screenY / 2);
		Button register = new Button();
		register.setText("Register");
		register.setPrefSize(120, 30);
		register.setTranslateX(this.screenX / 2 - 50);
		register.setTranslateY(this.screenY / 2);

		Button confirm = new Button();
		confirm.setText("confirm");
		confirm.setPrefSize(120, 30);
		confirm.setTranslateX(this.screenX / 2 - 50);
		confirm.setTranslateY(this.screenY / 2);

		confirm.setOnAction(e -> {
			if (this.isLogin) {
				// LOGIN EXISTENT USER
				this.info.setText("Enter Username and Password");
				try {
					// check if user doesn't exist, if so keep prompting for correct username
					if (!this.server.isUser(username.getText())) {
						this.info.setText(username.getText() + " doesn't exist.");
					}
					// check if password is correct
					UserInterface tmpUser = new User(username.getText(), password.getText());

					if (this.server.authenticateUser(tmpUser)) {
						this.user = new User(username.getText(), password.getText());
						this.connected = true;
						this.info.setText("Welcome: " + username.getText());
						pane.getChildren().clear();

						this.server.updateUserOnline(this.user, true);
						this.server.updateUserStatus(this.user, this.statusOptions.getValue());
						info.setText(this.help);
						this.mainPane.setLeft(UserList());

					} else if (!this.server.authenticateUser(tmpUser)) {
						this.info.setText("Incorrect password");
						tmpUser = new User(username.getText(), password.getText());
					}

				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				// REGISTER NEW USER
				try {
					if (this.server.isUser(username.getText())) {
						this.info.setText(
								"User " + username.getText() + " already exists, please try with a different name");
					} // if user already exists, prompt new username
					else if (username.getText().equals("") || password.getText().equals("")) {
						this.info.setText("Please enter both a username and password");
					} else {
						this.info.setText("User " + username.getText() + " created");
						this.user = new User(username.getText(), password.getText());
						this.server.registerUser(user);
						this.connected = true;
						pane.getChildren().clear();

						this.server.updateUserOnline(this.user, true);
						this.server.updateUserStatus(this.user, this.statusOptions.getValue());
						info.setText(this.help);
						this.mainPane.setLeft(UserList());
					}
				} catch (RemoteException e1) {
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

	public ScrollPane userPaneBackGround;
	public VBox userPane;
	public Button[] users;
	public Circle[] status;
	public VBox statusPane;
	public ScrollPane UserList() throws RemoteException {
		this.userPane = new VBox();
		this.userPane.setPadding(new Insets(25, 25, 25, 25));
		this.userPane.setStyle("-fx-background-color: #336693;");
		this.userPaneBackGround = new ScrollPane();
		
		TextField searchBar = new TextField();
		Label searchLabel = new Label();
		searchLabel.setText("Search for User");
		this.userPane.getChildren().addAll(searchLabel, searchBar);
		
		searchBar.setOnAction(e -> {
			
			try {
				if(this.server.isUser(searchBar.getText()) && searchBar.getText() != this.user.getUsername()){
					this.mainPane.setCenter(ChatLog());
					this.mainPane.setBottom(SendMessage());
					this.userIndex = this.server.getClients().indexOf(searchBar.getText());
					appendToScrollPane(this.mainScrollPane);
					this.info.setText("Chatting with: " + searchBar.getText());
				}
				else {
					this.info.setText("User Not found");
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		this.users = new Button[this.server.getClients().size()];
		this.status = new Circle[this.server.getClients().size()];
		
		for (int i = 0; i < this.server.getClients().size(); i++) {
			if (!this.server.getClients().get(i).equals(this.user.getUsername())) {
				this.users[i] = new Button(this.server.getClients().get(i));
				this.status[i] = new Circle();
				this.status[i].setTranslateX(users[i].getTranslateX() + 80);
				this.status[i].setTranslateY(users[i].getTranslateY() - 20);
				this.status[i].setRadius(7);;
				this.status[i].setFill(Color.RED);
				this.userPane.getChildren().addAll(users[i], status[i]);
			}
			
		}
		
		for (int i = 0; i < this.users.length; i++) {
			if (!this.server.getClients().get(i).equals(this.user.getUsername())) {
				String otherUser = new String(this.server.getClients().get(i));
				this.users[i].setOnAction(e -> {
					try {
						
						this.userIndex = this.server.getClients().indexOf(otherUser);
						//this.users[this.userIndex].setStyle("-fx-font-weight: normal");
						this.mainPane.setCenter(ChatLog());
						this.info.setText("Chatting with: " + otherUser);
						
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.mainPane.setBottom(SendMessage());

				});
			}
			
		}
		
		setStatus();
		this.userPaneBackGround.setContent(userPane);
		IsOnline(); 
		return this.userPaneBackGround;
	}
	public ComboBox<String> statusOptions;
	public void setStatus() throws RemoteException {
		this.statusPane = new VBox();
		this.statusOptions = new ComboBox<String>();
		this.statusOptions.getItems().add("Online");
		this.statusOptions.getItems().add("Away");
		this.statusOptions.getItems().add("Busy");
		this.statusOptions.getSelectionModel().selectFirst();
		this.statusPane.getChildren().add(this.statusOptions);
		this.userPane.getChildren().add(this.statusPane);
		
		statusOptions.setOnAction(e -> {
			try {
				this.server.updateUserStatus(this.user, statusOptions.getValue());
				System.out.println(statusOptions.getValue());
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		IsOnline(); 
	}

	public void IsOnline() throws RemoteException {
			for (int i = 0; i < this.server.getClients().size(); i++) {
				if(this.status.length == this.server.getClients().size() && this.status[i] != null) {
				switch(this.server.getUserStatus(this.server.getClients().get(i))) {
				  case "Online":
				    this.status[i].setFill(Color.GREEN);
				    break;
				  case "Away":
				    this.status[i].setFill(Color.YELLOW);
				    break;
				   case "Busy":
					    this.status[i].setFill(Color.BLUE);
					    break;
				  default:
				    this.status[i].setFill(Color.RED);
				}
				}
			}
		
	}

	public int totalUsers;

	public void CheckUserList() throws RemoteException {
		int lastUsers = this.server.getClients().size();
		if (this.totalUsers != lastUsers) {
			totalUsers = lastUsers;
			this.mainPane.setLeft(UserList());
		}

	}

	public Text lastMessage = null;
	public List<MessageInterface> messages;
	public VBox messagePane;
	public BorderPane backgroundPane;
	public ScrollPane mainScrollPane;
	public MessageInterface lastMsgInter;
	
	
	public ScrollPane ChatLog() throws RemoteException {
		this.mainScrollPane = new ScrollPane();
		this.mainScrollPane.setPadding(new Insets(25, 25, 25, 25));
		this.mainScrollPane.setStyle("-fx-background-color: #999999;");
		this.mainScrollPane.setPannable(true);
		this.mainScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		AnchorPane.setBottomAnchor(mainScrollPane, 0.0);
		AnchorPane.setTopAnchor(mainScrollPane, 0.0);
		AnchorPane.setLeftAnchor(mainScrollPane, 0.0);
		AnchorPane.setRightAnchor(mainScrollPane, 0.0);

		this.messagePane = new VBox();
		this.messagePane.setStyle("-fx-background-color: #0000FF;");
		this.backgroundPane = new BorderPane();

		getMessages(this.messages, this.messagePane, this.backgroundPane);

		this.mainScrollPane.setContent(this.backgroundPane);
		appendToScrollPane(this.mainScrollPane);

		return mainScrollPane;
	}
	
	
	public void getMessages(List<MessageInterface> messages, VBox messagePane, BorderPane backgroundPane)
			throws RemoteException {
		
		
		messagePane = new VBox();
		VBox pane = messagePane;
		if (this.userIndex > -1) {
			messages = this.server.getMessages(this.user, this.server.getClients().get(userIndex));
			
		} else {
			messages = null;
		}
		
		
		if (messages != null) {
			messages.forEach(m -> {
				try {
					Text currentMessage = new Text();
					
					currentMessage = new Text(m.getSender() + "\n" + m.getMessage() + "\n" + m.getTimestamp());
					if(m.getSender().equals(this.user.getUsername()) ) {
						if (this.lastMsgInter != null && this.lastMsgInter.getSender().equals(this.user.getUsername()) && this.lastMessage != null) {
							currentMessage.setTranslateX(this.lastMessage.getTranslateX());
							currentMessage.setTranslateY(this.lastMessage.getTranslateY());
						} else {
							currentMessage.setTranslateX(700);
							currentMessage.setTranslateY(0);
						}
						this.lastMessage = currentMessage;
						this.lastMsgInter = m;
					}
					else if(m.getSender().equals(this.server.getClients().get(this.userIndex)) ) {
						if (this.lastMsgInter != null && this.lastMsgInter.getSender().equals(this.server.getClients().get(this.userIndex)) && this.lastMessage != null) {
							currentMessage.setTranslateX(this.lastMessage.getTranslateX());
							currentMessage.setTranslateY(this.lastMessage.getTranslateY());
						} else {
							currentMessage.setTranslateX(15);
							currentMessage.setTranslateY(0);
						}
						this.lastMessage = currentMessage;
						this.lastMsgInter = m;
					}
				
					pane.getChildren().add(currentMessage);

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			this.lastMessage = null;
			if (pane.getChildren().size() > 0) {
				pane.setTranslateX(backgroundPane.getTranslateX());
				pane.setPrefWidth(this.screenX - 100);
				pane.setPrefHeight((pane.getChildren().get(0).getTranslateY()
						+ pane.getChildren().get(pane.getChildren().size() - 1).getTranslateY()) * 3 - 25);
			}
			backgroundPane.setCenter(pane);

		}
	}

	public Pane SendMessage() {
		Pane pane = new Pane();
		pane.setPadding(new Insets(25, 25, 25, 25));
		pane.setStyle("-fx-background-color: #FFFFFF;");

		TextField chatBox = new TextField();
		chatBox.setAlignment(Pos.TOP_LEFT);
		chatBox.setPrefSize(1100, 150);
		chatBox.setTranslateX(screenX - screenX * 0.9);

		chatBox.setOnAction(e -> {
			MessageInterface message;
			try {
				if (userIndex > -1) {
					message = new Message(user.getUsername(), this.server.getClients().get(userIndex),
							chatBox.getText(), false);
					server.sendMessage(message);
					//this.users[this.userIndex].setStyle("-fx-font-weight: bold");
					this.mainPane.setCenter(ChatLog());

				}
				chatBox.clear();

			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});

		pane.getChildren().add(chatBox);
		return pane;
	}

		/*public void CheckNewMessages() {
			for (int i = 0; i < this.server.getClients().size(); i++) {
			
			}
		}*/
			
	public void appendToScrollPane(ScrollPane scrollPane) {
		// ... actions which add content to the scroll pane ...

		// generate a layout pass on the scroll pane.
		scrollPane.applyCss();
		scrollPane.layout();

		// scroll to the bottom of the scroll pane.
		scrollPane.setVvalue(1.0);
	}

	public void Update() { // INCOMPLETE
		KeyFrame refreshChat = new KeyFrame(Duration.seconds(1), event -> {
			if (userIndex > -1) {
				try {
					getMessages(this.messages, this.messagePane, this.backgroundPane);
					CheckUserList();
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				if(this.user != null)
					IsOnline();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		Timeline timeline = new Timeline(refreshChat);
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

	}

	@Override
	public void stop() {
		try {
			this.server.updateUserStatus(this.user, "Offline");
			System.out.print("Quit");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void ConnectToServer() {
		// if on lan, insert IP of server
		try {
			this.server = (ChatInterface) Naming.lookup("rmi://" + ip + ":1099/IDS");
			this.info.setText("Connected to the server");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
		/*
		 * try { ChatInterface server = (ChatInterface) Naming.lookup("rmi://" + ip +
		 * ":1099/IDS"); System.out.println("Connected to the server");
		 * System.out.println("Type login or register...");
		 * 
		 * while (!connected) { userInput = input.nextLine().trim();
		 * 
		 * // REGISTER NEW USER if (userInput.equalsIgnoreCase("register")) { // } else
		 * if (userInput == "login") { // get username and check if already exists in db
		 * System.out.println("Enter username:"); username = input.nextLine().trim();
		 * 
		 * // if user already exists, prompt new username while
		 * (server.isUser(username)) { System.out.println("User " + username +
		 * "already exists, please try with a different name"); username =
		 * input.nextLine().trim(); }
		 * 
		 * // ask password System.out.println("Enter password:"); String password =
		 * input.nextLine().trim(); user = new User(username, password);
		 * server.registerUser(user); connected = true; }
		 * 
		 * // LOGIN EXISTENT USER else if (userInput.equalsIgnoreCase("login")) {
		 * System.out.println("Enter username:"); username = input.nextLine().trim();
		 * 
		 * // check if user doesn't exist, if so keep prompting for correct username
		 * while (!server.isUser(username)) { System.out.println(username +
		 * " doesn't exist."); username = input.nextLine().trim(); }
		 * 
		 * // ask password System.out.println("Enter password:"); String password =
		 * input.nextLine().trim();
		 * 
		 * // check if password is correct UserInterface tmpUser = new User(username,
		 * password); while (!server.authenticateUser(tmpUser)) {
		 * System.out.println("Incorrect password"); username = input.nextLine().trim();
		 * tmpUser = new User(username, password); }
		 * 
		 * user = new User(username, password); connected = true; }
		 * 
		 * } // LOGGED IN System.out.println("Logged in!");
		 * System.out.println("Welcome " + user.getUsername());
		 * server.updateUserOnline(user, true); System.out.println(help);
		 * 
		 * while (connected) { userInput = input.nextLine().trim(); if
		 * (userInput.equalsIgnoreCase("help")) { System.out.println(help); } else if
		 * (userInput.equalsIgnoreCase("clients")) {
		 * System.out.println(server.getClients()); } else if
		 * (userInput.equalsIgnoreCase("online clients")) {
		 * System.out.println(server.getConnectedClients());
		 * 
		 * } else if (userInput.startsWith("message")) { String recipient =
		 * userInput.split(":")[1]; // check if recipient exists if
		 * (!server.isUser(recipient)) { System.out.println(recipient +
		 * " doesn't exist"); } else { String text = userInput.split(":")[2];
		 * MessageInterface message = new Message(user.getUsername(), recipient,
		 * text.trim(), false); server.sendMessage(message); } } else if
		 * (userInput.startsWith("history")) { String recipient =
		 * userInput.split(":")[1]; // check if recipient exists if
		 * (!server.isUser(recipient)) { System.out.println(recipient +
		 * " doesn't exist"); } else { List<MessageInterface> messages;
		 * 
		 * messages = server.getMessages(user, recipient);
		 * System.out.println(messages.size()); // MessageInterface m = messages.get(0);
		 * // System.out.println(m.getMessage());
		 * 
		 * messages.forEach(m -> { try {
		 * System.out.println("______________________________");
		 * System.out.println("sender:"+ m.getSender() );
		 * System.out.println("recipient:"+ m.getReceiver() );
		 * System.out.println("message:"+ m.getMessage() );
		 * System.out.println("timestamp:"+ m.getTimestamp() );
		 * System.out.println("______________________________"); } catch (Exception e) {
		 * e.printStackTrace(); } });
		 * 
		 * }
		 * 
		 * } else if (userInput.equalsIgnoreCase("exit")) { // remove user from
		 * connected server.updateUserOnline(user, false); input.close(); connected =
		 * false; System.out.println("Goodbye"); System.exit(0); } } } catch (Exception
		 * e) { e.printStackTrace(); }
		 */

