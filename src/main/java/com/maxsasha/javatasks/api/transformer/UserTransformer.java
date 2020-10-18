package com.maxsasha.javatasks.api.transformer;

import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.entity.User;

public class UserTransformer {

	public User toEntity(UserDto dto) {
		return User.builder()
				.id(dto.getId())
				.name(dto.getName())
				.email(dto.getEmail())
				.build();
	}
	
	public UserDto toDto(User user) {
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}
	
}
