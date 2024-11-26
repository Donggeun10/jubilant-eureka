package com.example.chatter.exception;

/**
 * 채팅방 내 사용자 정보가 없을 경우 발생
 * */
public class NotFoundChatRoomMemberException extends RuntimeException {

	public NotFoundChatRoomMemberException(String message) {
		super(message);
	}

}
