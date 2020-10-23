package com.maxsasha.javatasks.api.transformer;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsasha.javatasks.api.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestMapper {
	private final ObjectMapper mapper = new ObjectMapper();

	public UserDto convertToDto(String userInfo) throws RuntimeException {
		try {
			return mapper.readValue(userInfo, UserDto.class);
		} catch (IOException ex) {
			log.error("Error with convert json to dto", ex.getMessage());
			throw new RuntimeException(String.format("Error with convert json to dto", ex.getMessage()));
		}
	}
}