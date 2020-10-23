package com.maxsasha.javatasks.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
	@Setter(AccessLevel.NONE)
	private Integer id;
	private String name;
	private String email;
}