package com.imst.event.map.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private BaseAuthenticationProvider baseAuthenticationProvider;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/api/**")
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.realmName("afad-map")
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		;
		
		http.csrf().disable();
		
	}
	
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		
		builder.authenticationProvider(baseAuthenticationProvider);
	}
	
}