package com.example.chatter.exception;

/**
 * 채팅방 최대 정원 초과시 발생
 * */
public class ChatRoomFullException extends RuntimeException {

	public ChatRoomFullException(String message) {
		super(message);
	}

}
