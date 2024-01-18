package com.imst.event.map.admin.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			HttpServletResponse response, Authentication authentication) throws IOException {


		HttpSession session = request.getSession(false);
		if (session != null) {

			UserItemDetails userItemDetails = (UserItemDetails) authentication.getPrincipal();		

			Map<String, Object> logMap = new HashMap<>();
			logMap.put("giriş", "admin paneli");
			logMap.put("username", userItemDetails.getUsername());
			logMap.put("id", userItemDetails.getUserId());

			DBLogger dbLogger = ApplicationContextUtils.getBean(DBLogger.class);
			dbLogger.log(logMap, LogTypeE.LOGIN);
		}


		redirectStrategy.sendRedirect(request, response, "/admin");
	}
}



