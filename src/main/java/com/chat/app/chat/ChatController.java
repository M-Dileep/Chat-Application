package com.chat.app.chat;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chat.app.chat.entites.ChatMessage;
import com.chat.app.chat.entites.NotificationMessage;

@Controller
public class ChatController {
	private final SimpMessagingTemplate messagingTemplate;

	// Inject SimpMessagingTemplate in the constructor
	public ChatController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

//	private SimpMessageSendingOperations messagingTemplate;
//
//	@MessageMapping("chat.sendMessage")
//	@SendTo("/topic/public")
//	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//		chatMessage.setTyping(false);
//		chatMessage.setTimestamp(LocalDateTime.now());
//		return chatMessage;
//	}
	@MessageMapping("/app/chat.sendMessage")
	public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		chatMessage.setTyping(false);
		chatMessage.setTimestamp(LocalDateTime.now());

		// Check if the message is private
		if (chatMessage.isPrivateMessage()) {
			String recipient = chatMessage.getRecipient();

			// Send the private message to the recipient
			messagingTemplate.convertAndSendToUser(recipient, "/queue/private", chatMessage);

			// You may want to handle offline users and store messages for delivery upon
			// login
			// For now, we assume the recipient is always online when sending private
			// messages
		} else {
			// Broadcast the regular message to all users in the public topic
			messagingTemplate.convertAndSend("/topic/public", chatMessage);
		}
	}

	@MessageMapping("/chat.typing")
	@SendTo("/topic/public")
	public ChatMessage typingIndicator(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}

	@MessageMapping("/chat.notify")
	public void sendNotification(@Payload NotificationMessage notification, SimpMessageHeaderAccessor headerAccessor) {
		messagingTemplate.convertAndSendToUser(notification.getRecipient(), "/queue/notifications", notification);
	}

	@MessageMapping("chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		chatMessage.setTimestamp(LocalDateTime.now());
		return chatMessage;
	}

	@MessageMapping("/chat.removeUser")
	@SendTo("/topic/public")
	public ChatMessage removeUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username != null) {
			ChatMessage leaveMessage = new ChatMessage();
			leaveMessage.setType(MessageType.LEAVE);
			leaveMessage.setSender(username);
			headerAccessor.getSessionAttributes().remove("username");
			return leaveMessage;
		}
		return null;
	}

}
