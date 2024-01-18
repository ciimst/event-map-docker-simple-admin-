package com.imst.event.map.admin.constants;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;

import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.hibernate.entity.LogType;
import com.imst.event.map.hibernate.entity.Permission;

public class Statics {

	public static HashMap<Integer, LogType> logTypeMap = new HashMap<>();
	public static List<Permission> permissionList = new ArrayList<>();
	public static final HashMap<String, String> settings = new HashMap<>();
		
	public static final Integer adminUserLoginPermissionId = 39;
	
	public static Map<String, DataSourceInfo> tenantDataSourceInfoMap = new HashMap<>();
	
	
	public static Integer eventMediaListSize = 1000;
	
	public static String exporttEncryptPrivateKey = "qwerty";
	public static String databaseEncryptPrivateKey = "qwerty";
	
	public static String applicationPropertiesLocation = "classpath:application.properties";
	
	public static final String DEFAULT_DB_NAME = "default";
	
	public static Collator sortedCollator() {
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(Collator.PRIMARY);
		
		return collator;
	}
	
	
}
