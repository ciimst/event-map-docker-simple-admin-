package com.imst.event.map.admin.utils;

import java.util.HashMap;

import org.springframework.util.Base64Utils;

import com.imst.event.map.admin.constants.SettingsE;


public class SettingsUtil {
	
	public static final HashMap<String, String> settings = new HashMap<>();
	
	public static String getString(SettingsE settingsE) {
		
		String settingsResultStr = settings.get(settingsE.getName());
		return settingsResultStr == null ? "" : settingsResultStr;
	}
	
	public static int getInteger(SettingsE settingsE) {
		
		return Integer.parseInt(settings.get(settingsE.getName()));
	}
	
    public static Boolean getBoolean(SettingsE settingsE) {
    	
		return Boolean.parseBoolean(settings.get(settingsE.getName()));
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getFaviconImage() {
		
		return Base64Utils.encodeToString(getString(SettingsE.FAVICON_IMAGE).getBytes());
	}
	
//	Web Modülü -------------------------------------------------------------------------------------------------------------------------------------------
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir
	public static String getWebTitleForLogin() {
		
		return getString(SettingsE.WEB_TITLE_FOR_LOGIN);
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getWebTextForLogin() {
		
		return getString(SettingsE.WEB_TEXT_FOR_LOGIN);
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getWebLogoImage() {
		
		return Base64Utils.encodeToString(getString(SettingsE.WEB_LOGO_IMAGE).getBytes());
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getWebLoginImage() {
		
		return Base64Utils.encodeToString(getString(SettingsE.WEB_LOGIN_IMAGE).getBytes());
	}
	
//	Web Modülü -------------------------------------------------------------------------------------------------------------------------------------------
	
	
//	Yönetim Paneli Modülü -------------------------------------------------------------------------------------------------------------------------------------------
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir
	public static String getAdminTitleForLogin() {
		
		return getString(SettingsE.ADMIN_TITLE_FOR_LOGIN);
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getAdminTextForLogin() {
		
		return getString(SettingsE.ADMIN_TEXT_FOR_LOGIN);
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getAdminLogoImage() {
		
		return Base64Utils.encodeToString(getString(SettingsE.ADMIN_LOGO_IMAGE).getBytes());
	}
	
	// Metod ismi doğrudan html sayfa içerisinde kullanılmıştır. Değiştirilmesi durumunda runtime hatası verir	
	public static String getAdminLoginImage() {
		
		return Base64Utils.encodeToString(getString(SettingsE.ADMIN_LOGIN_IMAGE).getBytes());
	}
//	Yönetim Paneli Modülü -------------------------------------------------------------------------------------------------------------------------------------------
	

	

}
