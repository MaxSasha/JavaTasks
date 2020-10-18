package com.maxsasha.javatasks.api.transformer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsasha.javatasks.api.dto.UserDto;

public class RequestMapper {

    public UserDto convertToDto(String userInfo) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(userInfo, UserDto.class);
	}
}