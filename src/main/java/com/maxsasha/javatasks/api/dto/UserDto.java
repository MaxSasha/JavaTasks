package com.maxsasha.javatasks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	@Getter
	private int id;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String email;

	public UserDto(String name, String email) {
		this.name = name;
		this.email = email;
	}

}