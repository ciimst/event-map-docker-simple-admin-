package com.imst.event.map.admin.constants;


import java.util.HashMap;

public enum SettingsE {
	
	MEDIA_PATH("mediaPath"),
	PAGE_REFRESH_TIME_IN_SECOND("WebPageRefreshTimeInSec"), 
	PAGE_EVENT_COUNT_PER_LOAD("WebPageEventCountPerLoad"),
	STATIC_IMAGE_ROOT_PATH("StaticImageRootPath"),
	ADMIN_TITLE_FOR_LOGIN("AdminTitleForLogin"),
	ADMIN_TEXT_FOR_LOGIN("AdminTextForLogin"),
	ADMIN_LOGO_IMAGE("AdminLogoImage"),
	ADMIN_LOGIN_IMAGE("AdminLoginImage"),
	WEB_TITLE_FOR_LOGIN("WebTitleForLogin"),
	WEB_TEXT_FOR_LOGIN("WebTextForLogin"),
	WEB_LOGO_IMAGE("WebLogoImage"),
	WEB_LOGIN_IMAGE("WebLoginImage"),
	FAVICON_IMAGE("FaviconImage"),
	LAYER_TILE_ROOT_PATH("LayerTileRootPath"),
	LAYER_EXPORT_FILE_PATH("LayerExportFilePath"),
	TILE_EXPORT_FILE_PATH("TileExportFilePath"),
	LAYER_EXPORT_EVENT_LOAD_LIMIT("LayerExportEventLoadLimit"),
	EXCEL_EVENT_TITLE("ExcelEventTitle"),
	EXCEL_EVENT_SPOT("ExcelEventSpot"),
	EXCEL_EVENT_DESCRIPTION("ExcelEventDescription"),
	EXCEL_EVENT_DATE("ExcelEventDate"),
	EXCEL_EVENT_TYPE("ExcelEventType"),
	EXCEL_EVENT_LAYER_NAME("ExcelEventLayerName"),
	EXCEL_EVENT_GROUP_NAME("ExcelEventGroupName"),
	EXCEL_EVENT_COUNTRY("ExcelEventCountry"),
	EXCEL_EVENT_CITY("ExcelEventCity"),
	EXCEL_EVENT_RESERVED_LINK("ExcelEventReservedLink"),
	EXCEL_EVENT_RESERVED_TYPE("ExcelEventReservedType"),
	EXCEL_EVENT_RESERVED_KEY("ExcelEventReservedKey"),
	EXCEL_EXCEL_EVENT_RESEERVED_ID("ExcelEventReservedId"),
	EXCEL_EVENT_LATITUDE("ExcelEventLatitude"),
	EXCEL_EVENT_LONGITUDE("ExcelEventLongitude"),
	EXCEL_EVENT_BLACK_LIST_TAG("ExcelEventBlackListTag"),
	EXCEL_EVENT_CREATE_USER("ExcelEventCreateUser"),
	EXCEL_EVENT_CREATE_DATE("ExcelEventCreateDate"),
	EXCEL_EVENT_UPDATE_DATE("ExcelEventUpdateDate"),
	EXCEL_EVENT_GROUP_COLOR("ExcelEventGroupColor"),
	EXCEL_EVENT_USER_ID("ExcelEventUserId"),
	EXCEL_EVENT_GROUP_ID("ExcelEventGroupId"),
	EXCEL_EVENT_STATE("ExcelEventState"),
	EXCEL_EVENT_RESERVED_1("ExcelEventReserved1"),
	EXCEL_EVENT_RESERVED_2("ExcelEventReserved2"),
	EXCEL_EVENT_RESERVED_3("ExcelEventReserved3"),
	EXCEL_EVENT_RESERVED_4("ExcelEventReserved4"),
	EXCEL_EVENT_RESERVED_5("ExcelEventReserved5"),
	MAX_COUNT_EVENTS_EXCEL("MaxCountEventsExcel"),
	DATABASE_BACKUP_COUNT("DatabaseBackupCount"),
	DATABASE_BACKUP_INTERVAL("DatabaseBackupInterval")
	;
	
	private String name; 
	
	private SettingsE(String name) {
		this.name = name;
	}
	
	private static HashMap<String, SettingsE> map = new HashMap<>();
	
	public String getName() {
		return name;
	}
	
	public static SettingsE getSettings(String key) {
		return map.get(key) == null ? null : map.get(key);
	}
	
	static {
		
		for (SettingsE settingsE : SettingsE.values()) {
			map.put(settingsE.name(), settingsE);
		}
	}
	
}
