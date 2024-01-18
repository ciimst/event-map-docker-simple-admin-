package com.imst.event.map.admin.services;

import java.sql.Timestamp;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.repositories.LogRepository;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.Log;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DBLogger {
	
	@Autowired
	private HeaderExtractorService headerExtractorService;
	@Autowired
	private LogRepository logRepository;
	
	private ExtendedLogger fileLogger;
	
	public DBLogger() {
		
		fileLogger  = LogManager.getContext(false).getLogger("com.imst.event.map.admin");
	}
	
	public ExtendedLogger getLogger() {
		
		return fileLogger;
	}
	
	public void logWithUser(UserItemDetails user, String description, LogTypeE logTypeE) {
		
		try {
			
			String username = "unknown";
			Integer userId = null;

			if (user != null && !StringUtils.isEmpty(user.getUsername())) {
				username = user.getUsername();
				userId = user.getUserId();
			}
			
			String searchable = "";
			if (!StringUtils.isBlank(description)) {
				
				searchable = description
						.replaceAll("\\{", "")
						.replaceAll("}", "")
						.replaceAll("]", "")
						.replaceAll("\\[", "")
						.replaceAll("\\(", "")
						.replaceAll("\\)", "")
						.toLowerCase(Locale.ENGLISH)
						.toUpperCase(Locale.ENGLISH)
				;
			}
			
			Timestamp nowT = DateUtils.nowT();
			Log log = new Log();
			log.setUsername(username);
			log.setUserId(userId);
			log.setDescription(description);
			log.setSearchableDescription(searchable);
			log.setFkLogTypeId(logTypeE.getId());
			log.setIp(headerExtractorService.getClientIpAddress());
			log.setCreateDate(nowT);
			
			log(log);
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void logWithUser(UserItemDetails user, Object description, LogTypeE logTypeE) {
		
		String jsonDescription = new Gson().toJson(description);
		logWithUser(user, jsonDescription, logTypeE);
	}
	
	public void log(Object description, LogTypeE logTypeE) {
		
		String jsonDescription = new Gson().toJson(description);
		log(jsonDescription, logTypeE);
	}
	
	public void log(String description, LogTypeE logTypeE) {
		
		UserItemDetails user = ApplicationContextUtils.getUser();
		logWithUser(user, description, logTypeE);
	}
	
	public void log(Log log) {
		
		if (log == null) {
			return;
		}
		
		getLogger().log(Level.getLevel("USER"), new Gson().toJson(log));
		logRepository.save(log);
	}
	
	
	
}
