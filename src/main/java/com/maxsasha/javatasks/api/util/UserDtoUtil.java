package com.maxsasha.javatasks.api.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsasha.javatasks.entity.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserDtoUtil {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static User fromJson(String json) throws RuntimeException{
		try {
			return mapper.readValue(json, User.class);
		} catch (IOException ex) {
			log.error("Error with convert json to dto", ex.getMessage());
			throw new RuntimeException(String.format("Error with convert json to object {0}", ex.getMessage()));
		}
	}
}