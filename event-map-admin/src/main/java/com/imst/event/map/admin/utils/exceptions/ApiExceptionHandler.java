package com.imst.event.map.admin.utils.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public void apiException(ApiException ex, HttpServletResponse response) throws IOException {

		response.sendError(ex.getStatus().value(), ex.getMessage());
	}
}
