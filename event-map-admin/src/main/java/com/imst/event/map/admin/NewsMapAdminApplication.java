package com.imst.event.map.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.db.repositories.PermissionRepository;
import com.imst.event.map.admin.utils.ApplicationContextUtils;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@EnableScheduling
@SpringBootApplication
@EnableRedisHttpSession(redisNamespace = "${spring.session.redis.namespace}",maxInactiveIntervalInSeconds = 1800)
public class NewsMapAdminApplication {
	
	@Value("${master.datasource.username}")
	private String username;
	@Value("${master.datasource.password}")
	private String password;
	@Value("${master.datasource.jdbcUrl}")
	private String jdbcUrl;

	public static void main(String[] args) {
		SpringApplication.run(NewsMapAdminApplication.class, args);
	}
	
	@Bean
	LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	

	
	
	@Autowired
	public void context(ApplicationContext context) {
		
		ApplicationContextUtils.setApplicationContext(context);
		Statics.permissionList = permissionRepository.findAllByState(true);
	}
	
//	@Autowired
//	@PostConstruct
//	public void dbCreate() {
//		
//		String username = this.username;
//		String pass = this.password;	
//		String[] dbArray = jdbcUrl.split("/");
//		
//		String[] hostNPort = dbArray[2].split(":");
//		String dbName = dbArray[3].trim();
//		String hostName = hostNPort[0];
//		String port = hostNPort[1];
//		
//        Connection connection = null;
//        Statement statement = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:postgresql://" + hostName + ":" + port + "/", username, pass);
//            statement = connection.createStatement();
//            statement.executeQuery("SELECT count(*) FROM pg_database WHERE datname =" + "'" + dbName + "'");
//            ResultSet resultSet = statement.getResultSet();
//            resultSet.next();
//            int count = resultSet.getInt(1);
//
//            if (count <= 0) {
//                statement.executeUpdate("CREATE DATABASE" + dbName);
//            } else {
//            }
//        } catch (SQLException e) {
//        	e.printStackTrace();
//        } finally {
//            try {
//                if (statement != null) {
//                    statement.close();                   
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//            	e.printStackTrace();
//            }
//        }
//	}
	
	
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		ApplicationContextUtils.setMessageSource(messageSource);
	}
	
	
	@Value("${datatable.page.length}")
	private void setTableLength(Integer length) {
		
		ApplicationContextUtils.setTableLength(length);
	}
}
