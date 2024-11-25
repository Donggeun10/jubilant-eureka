package com.example.chatter.exception;

/**
 * 채팅방 정보가 없을 경우 발생
 * */
public class NotFoundChatRoomException extends RuntimeException {

	public NotFoundChatRoomException(String message) {
		super(message);
	}

}
