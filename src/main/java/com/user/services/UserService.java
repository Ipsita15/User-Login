package com.user.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.model.UserModel;
import com.user.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public ResponseEntity<Map<String, Object>> createUser(UserModel user) {
		Map<String, Object> response = new HashMap<>();

		// Check mandatory fields
		if (user.getEmail() == null || user.getEmail().isEmpty() || user.getPasswordHash() == null) {
			response.put("status", "FAILED");
			response.put("message", "Missing Mandatory Parameters");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Validate email
		if (!ValidateUser.validateEmail(user.getEmail())) {
			response.put("status", "FAILED");
			response.put("message", "Invalid Email ID");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Validate password
		String passwordValidationMessage = ValidateUser.validatePassword(user.getPasswordHash());
		if (!passwordValidationMessage.equals("Password is strong")) {
			response.put("status", "FAILED");
			response.put("message", passwordValidationMessage);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Validate date of birth
		String dobValidationMessage = ValidateUser.validateDateOfBirth(user.getDateOfBirth());
		if (!dobValidationMessage.equals("Date of Birth is valid")) {
			response.put("status", "FAILED");
			response.put("message", dobValidationMessage);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Validate phone number
		if (!ValidateUser.validatePhoneNumber(user.getPhoneNumber())) {
			response.put("status", "FAILED");
			response.put("message", "Invalid Phone Number");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Validate postal code
		if (!ValidateUser.validatePostalCode(user.getPostalCode())) {
			response.put("status", "FAILED");
			response.put("message", "Invalid Pin Code");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Check if user already exists

		if (userRepository.existsByEmail(user.getEmail())) {
			response.put("status", "FAILED");
			response.put("message", "Email already exists");
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

		// Generate UID with 10 random digits
		String userId = ValidateUser.generateUserId();
		user.setUserId(userId);

		// Hash password (simple example, replace with real hash in production)

		// Hash password
		try {
			String hashedPassword = ValidateUser.hashPassword(user.getPasswordHash());
			user.setPasswordHash(hashedPassword);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			response.put("status", "FAILED");
			response.put("message", "Error hashing password");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Save user
		UserModel createdUser = userRepository.save(user);
		if (createdUser != null && createdUser.getUserId() != null) {
			response.put("userId", createdUser.getUserId());
			response.put("status", "SUCCESS");
			response.put("message", "User has been created successfully");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.put("status", "FAILED");
			response.put("message", "User creation failed");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
