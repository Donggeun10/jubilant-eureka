package com.example.chatter.advise;

import com.example.chatter.exception.ChatRoomFullException;
import com.example.chatter.exception.NotFoundChatRoomException;
import com.example.chatter.domain.ErrorResponse;
import com.example.chatter.exception.NotFoundChatRoomMemberException;
import com.example.chatter.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ChatRoomControllerAdvice {

	@ExceptionHandler(NotFoundChatRoomException.class)
	public ResponseEntity<ErrorResponse> notFoundChatRoom(NotFoundChatRoomException e) {

		ErrorResponse errorRes = new ErrorResponse(e.getMessage());
		log.error(DataUtil.makeErrorLogMessage(e));

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
	}

	@ExceptionHandler(ChatRoomFullException.class)
	public ResponseEntity<ErrorResponse> chatRoomIsFull(ChatRoomFullException e) {

		ErrorResponse errorRes = new ErrorResponse(e.getMessage());
		log.error(DataUtil.makeErrorLogMessage(e));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
	}

	@ExceptionHandler(NotFoundChatRoomMemberException.class)
	public ResponseEntity<ErrorResponse> notFoundChatRoomMember(NotFoundChatRoomMemberException e) {

		ErrorResponse errorRes = new ErrorResponse(e.getMessage());
		log.error(DataUtil.makeErrorLogMessage(e));

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
	}
}