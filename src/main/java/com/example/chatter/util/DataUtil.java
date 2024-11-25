package com.example.chatter.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataUtil {

	private DataUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String makeErrorLogMessage(Exception e) {
		StackTraceElement[] stackTrace =  e.getStackTrace();
		return String.format("%s:%s", e.getMessage(), stackTrace[0]);
	}

	public static String getCurrentDatetimeStringValue() {
		// Get the current date and time
		LocalDateTime now = LocalDateTime.now();

		// Define the format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		// Format the current date and time
		return now.format(formatter);
	}
}