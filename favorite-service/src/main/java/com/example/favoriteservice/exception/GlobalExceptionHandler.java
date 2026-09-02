package com.example.favoriteservice.exception;

import com.example.favoriteservice.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		var body = new ErrorResponseDto(
				HttpStatus.BAD_REQUEST.value(),
				"Validasiya xetasi",
				errors,
				LocalDateTime.now()
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(FavoriAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDto> handleAlreadyExists(FavoriAlreadyExistsException ex) {
		var body = new ErrorResponseDto(
				HttpStatus.BAD_REQUEST.value(),
				ex.getMessage(),
				null,
				LocalDateTime.now()
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(FavoriNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleNotFound(FavoriNotFoundException ex) {
		var body = new ErrorResponseDto(
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage(),
				null,
				LocalDateTime.now()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(LoginRequiredException.class)
	public ResponseEntity<ErrorResponseDto> handleLoginRequired(LoginRequiredException ex) {
		var body = new ErrorResponseDto(
				HttpStatus.UNAUTHORIZED.value(),
				ex.getMessage(),
				null,
				LocalDateTime.now()
		);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

}
