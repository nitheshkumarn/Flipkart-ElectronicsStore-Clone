package com.flipkartapp.es.responsedto;

import com.flipkartapp.es.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserResponse {
int userId;
String userName;
String email;
UserRole userRole;
boolean isEmailVerified;
boolean isDeleted;
}
