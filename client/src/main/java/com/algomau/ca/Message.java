package com.algomau.ca;

import java.time.LocalDateTime;

public class Message {
	private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Message() {
    }

    public Message(String sender, String receiver, String message, boolean read) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.read = read;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setSender(String sender) {
		this.sender = sender;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

}
