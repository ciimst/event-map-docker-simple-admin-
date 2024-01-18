package com.imst.event.map.admin.utils.exceptions;


import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	
	private static final long serialVersionUID = 6186070415494463927L;

	private HttpStatus status;
	
	public ApiException(String message) {
		
		this(message, HttpStatus.NOT_FOUND);
	}
	
	public ApiException(String message, HttpStatus status) {
		
		super(message);
		this.status = status;
	}
	
	
	public HttpStatus getStatus() {
		
		return status;
	}
	
}
