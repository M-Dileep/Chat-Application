package com.chat.app.chat.entites;

import java.time.LocalDateTime;

import com.chat.app.chat.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

	private String content;
	private String sender;
	private MessageType type;
	private boolean typing;
	private boolean privateMessage;
	private String recipient;

	public boolean isPrivateMessage() {
		return privateMessage;
	}

	public void setPrivateMessage(boolean privateMessage) {
		this.privateMessage = privateMessage;
	}

	private LocalDateTime timestamp;

	public String getContent() {
		return content;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public boolean isTyping() {
		return typing;
	}

	public void setTyping(boolean typing) {
		this.typing = typing;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
