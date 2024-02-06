package com.flipkartapp.es.entity;

import org.springframework.data.annotation.Id;

import com.flipkartapp.es.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int userId;
	String userName;
	String email;
	String password;
	@Enumerated(EnumType.STRING)
	UserRole userRole;
	boolean isDeleted;
	boolean isEmailVerified;

}
