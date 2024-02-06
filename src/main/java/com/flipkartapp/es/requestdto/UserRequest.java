package com.flipkartapp.es.requestdto;

import com.flipkartapp.es.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserRequest {
String email;
String password;
UserRole userRole;
}
