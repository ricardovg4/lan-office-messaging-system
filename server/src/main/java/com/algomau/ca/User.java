package com.algomau.ca;

import java.security.Key;

public class User {
	
	private String username;
	private String password;
	// private Key publicKey;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		// this.publicKey = publicKey;
	}
	
	public String getUsername() { //getting user name
		return username;
	}
	
	public String getPassword() { //getting user password
		return password;
	}
	
	// public Key getPublicKey() { //getting Public Key
	// 	return publicKey// }
}

