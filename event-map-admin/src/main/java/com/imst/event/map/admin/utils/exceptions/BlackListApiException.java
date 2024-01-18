package com.imst.event.map.admin.utils.exceptions;


import org.springframework.http.HttpStatus;

public class BlackListApiException extends RuntimeException {
	
	private static final long serialVersionUID = -8845909516665108332L;

	private HttpStatus status;
	
	public BlackListApiException(String message) {
		
		this(message, HttpStatus.NOT_ACCEPTABLE);
	}
	
	public BlackListApiException(String message, HttpStatus status) {
		
		super(message);
		this.status = status;
	}
	
	
	public HttpStatus getStatus() {
		
		return status;
	}
	
}
