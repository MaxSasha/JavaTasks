package com.maxsasha.javatasks.api.transformer;

import java.util.ArrayList;
import java.util.List;

import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.entity.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTransformer {

	public static User transform(UserDto dto) {
		return User.builder()
				.id(dto.getId())
				.name(dto.getName())
				.email(dto.getEmail())
				.build();
	}
	
	public static UserDto transform(User user) {
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}
	public static List<UserDto> transfrom(List<User> users)
	{
		List<UserDto> transformList = new ArrayList<UserDto>();
		for(User item : users)
		{
			transformList.add((UserDto.builder()
					.id(item.getId())
					.name(item.getName())
					.email(item.getEmail())
					.build()));
		}
		return transformList;
	}
}