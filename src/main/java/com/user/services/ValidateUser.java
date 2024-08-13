package com.user.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

@Service
public class ValidateUser {

	public static final SecureRandom RANDOM = new SecureRandom();
	public static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1950, 1, 1);
	public static final int HASH_LENGTH = 100; // Length of the hash
	public static final int SALT_LENGTH = 16; // Length of the salt
	public static final int ITERATIONS = 10000; // Number of iterations for PBKDF2

	public static  boolean validateEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		return email != null && pat.matcher(email).matches();
	}

	public static  String validatePassword(String password) {
		if (password.length() < 6 || password.length() > 15) {
			return "Password must be between 6 and 15 characters long";
		}
		if (!password.matches(".*\\d.*")) {
			return "Password must contain at least one numeric character";
		}
		if (!password.matches(".*[a-z].*")) {
			return "Password must contain at least one lowercase letter";
		}
		if (!password.matches(".*[A-Z].*")) {
			return "Password must contain at least one uppercase letter";
		}
		if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			return "Password must contain at least one special character";
		}
		return "Password is strong";
	}

	public static String validateDateOfBirth(LocalDate dateOfBirth) {
		LocalDate currentDate = LocalDate.now();
		if (dateOfBirth.isBefore(MIN_DATE_OF_BIRTH) || dateOfBirth.isAfter(currentDate)) {
			return "Invalid DOB";
		}
		return "Date of Birth is valid";
	}

	public static boolean validatePhoneNumber(String phoneNumber) {
		return phoneNumber.matches("\\d{10}");
	}

	public static boolean validatePostalCode(String postalCode) {
		return postalCode.matches("\\d{6}");
	}

	// user ID creation
	public static  String generateUserId() {
		StringBuilder sb = new StringBuilder("UID");
		for (int i = 0; i < 10; i++) {
			sb.append(RANDOM.nextInt(10));
		}
		return sb.toString();
	}

	public static  String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
	    // Use a fixed salt value for consistency
	    String fixedSalt = "16"; // Use a constant salt value
	    byte[] salt = Base64.getDecoder().decode(fixedSalt);

	    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	    byte[] hash = skf.generateSecret(spec).getEncoded();

	    String encodedHash = Base64.getEncoder().encodeToString(hash);

	    // Combine salt and hash. Fixed salt means hash length is constant
	    return fixedSalt + encodedHash.substring(0, HASH_LENGTH - fixedSalt.length());
	}

}
