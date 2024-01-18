package com.imst.event.map.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import com.imst.event.map.admin.security.ldap.LdapUserSearchConfig;
import com.imst.event.map.admin.vo.LdapItem;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(3)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private BaseAuthenticationProvider baseAuthenticationProvider;
	
	
	@Autowired
	LdapUserSearchConfig ldapUserSearchConfig;
	
	
//	@Bean
//	public SpringSecurityDialect securityDialect() {
//		return new SpringSecurityDialect();
//	}
	
	
	@Bean
	@ConfigurationProperties(prefix="ldap.context")
	public LdapItem getLdapItem() {
		LdapItem ldapItem = new LdapItem();
		return ldapItem;
	}
	
	@Bean
	@ConfigurationProperties(prefix="ldap.context-source")
	public LdapContextSource contextSource() {
		LdapItem ldapItem = getLdapItem();
		LdapContextSource contextSource = new LdapContextSource();
		
		contextSource.setPassword(ldapItem.getPassword());
		contextSource.setUserDn(ldapItem.getUserDn());
		
		return contextSource;
	}


	@Bean
	public AuthenticationProvider ldapAuthenticationProvider() throws Exception {

		LdapContextSource contextSource = contextSource();
		return ldapUserSearchConfig.getProvider(contextSource);
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.requestMatchers().anyRequest();
	
		http
			.authorizeRequests()
				.antMatchers("fragments/**").permitAll()
				.antMatchers("/live").permitAll()
				.antMatchers("/ready").permitAll()
				.antMatchers("layouts/**").permitAll()
				.antMatchers("page/**").permitAll()
//				.antMatchers("/login").permitAll()
//				.antMatchers("/sso/login").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/image/static/**").permitAll()
				.antMatchers("/i18n/all").permitAll()
				.anyRequest().authenticated();
		
		http.formLogin()				
				.loginPage("/login")
				.successHandler(new MyAuthenticationSuccessHandler())
//				.defaultSuccessUrl("/admin", true)
				.failureUrl("/login?error")
				.permitAll();
		http.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout**"))
				.deleteCookies("JSESSIONID", "SESSION")
				.invalidateHttpSession(true)//invalid urlye gitmesin diye false yapılcak
				.logoutSuccessHandler(new MyLogoutSuccessHandler())
				.permitAll();
		http.exceptionHandling()
				.accessDeniedPage("/denied");
		
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false)
				.expiredUrl("/expired?expired=true")
//				.sessionRegistry(sessionRegistry);
		;
		
	}
	
	/*@Bean
	public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider(){
		ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = new
				ActiveDirectoryLdapAuthenticationProvider("imst.local", "ldaps://177.177.1.25:636");
		return activeDirectoryLdapAuthenticationProvider;
	}*/
	
	 //Güvenilir olmayan sertifikayı proje bazlı tanıtmak için kullanılır
//    @Bean
//    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider()
//            throws IOException {
//        
//        File jks = File.createTempFile("cacerts", "jks");
//        jks.deleteOnExit();
//        
//        try (InputStream fromJks = WebSecurityConfig.class.getResource("/cacerts.jks").openStream()) {
//            FileCopyUtils.copy(FileCopyUtils.copyToByteArray(fromJks), jks);
//        }
//
//        System.setProperty("javax.net.ssl.trustStore", jks.getPath());
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//
//        ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = 
//                new ActiveDirectoryLdapAuthenticationProvider("imst.local", "ldaps://177.177.1.25:636");
//        
//        return activeDirectoryLdapAuthenticationProvider;
//    }

	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(baseAuthenticationProvider);
//		auth.authenticationProvider(ldapAuthenticationProvider());
	}
	
}