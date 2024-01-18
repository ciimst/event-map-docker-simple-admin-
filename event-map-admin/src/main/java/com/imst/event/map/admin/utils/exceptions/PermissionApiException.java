package com.imst.event.map.admin.utils.exceptions;

import org.springframework.http.HttpStatus;

public class PermissionApiException extends RuntimeException {
	
	private static final long serialVersionUID = -8640341137694101263L;
	private HttpStatus status;
	
	public PermissionApiException(String message) {
		
		this(message, HttpStatus.NOT_ACCEPTABLE);
	}
	
	public PermissionApiException(String message, HttpStatus status) {
		
		super(message);
		this.status = status;
	}
	
	
	public HttpStatus getStatus() {
		
		return status;
	}
}
