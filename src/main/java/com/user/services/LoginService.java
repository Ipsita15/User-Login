package com.user.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.model.UserModel;
import com.user.repositories.UserRepository;

@Service
public class LoginService {

	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<Map<String, Object>> loginUser(String email, String password) {

		Map<String, Object> response = new HashMap<>();

		UserModel userData = userRepository.getPasswordHashByEmail(email);
		if (userData == null) {
			response.put("status", "FAILED");
			response.put("statusCode", "000");
			response.put("message", "User does not exist.");
			response.put("TOKEN", null);
			response.put("userID", null);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}


		String userHash = null;
		
		try {
			userHash = ValidateUser.hashPassword(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (userData.getPasswordHash().equals(userHash)) {
			response.put("status", "SUCCESS");
			response.put("statusCode", "200");
			response.put("message", "User Logged in successfully.");
			response.put("TOKEN", "access_token");
			response.put("userID", userData.getUserId());

			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		
		else {
			response.put("status", "FAILED");
			response.put("statusCode", "101");
			response.put("message", "Incorrect Password");
			response.put("TOKEN", "access_denied");
			response.put("userID", null);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

	}

}
