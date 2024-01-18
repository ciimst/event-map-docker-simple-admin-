package com.imst.event.map.admin.vo;

import com.imst.event.map.admin.datatables.EntitySortKey;
import com.imst.event.map.hibernate.entity.Alert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertItem {
	
	private Integer id;
	private String name;
	private Integer userId;
	private String query;
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String resrevedLink;
	
	private Integer eventTypeId;
	private Integer eventGroupId;
	private Integer layerId;
	
	@EntitySortKey("eventTypeId")	
	private String eventTypeName;
	
	
//	private String eventTypeImage;
//	private String eventTypeCode;
	
	
	
	@EntitySortKey("eventGroupId")
	private String eventGroupName;
//	private String eventGroupColor;
	
	@EntitySortKey("layerId")
	private String layerName;
	
	@EntitySortKey("eventTypeId")
	private String eventTypeCode; 
	
	@EntitySortKey("userId")
	private String userName;
	
	public AlertItem() {
	
	}



	public AlertItem(Integer id, String name, Integer userId, String query, String reservedKey, String reservedType,
			String reservedId, String resrevedLink, Integer eventTypeId, Integer eventGroupId, Integer layerId, String eventTypeName,
			String eventGroupName, String layerName, String eventTypeCode, String userName) {
		
		this.id = id;
		this.name = name;
		this.userId = userId;
		this.query = query;
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.resrevedLink = resrevedLink;
		this.eventTypeId = eventTypeId;
		this.eventGroupId = eventGroupId;
		this.layerId = layerId;
		this.eventTypeName = eventTypeName;
		this.eventGroupName = eventGroupName;
		this.layerName = layerName;
		this.eventTypeCode = eventTypeCode;
		this.userName = userName;
	}



	public AlertItem(Alert alert) {
		
		this.id = alert.getId();
		this.name = alert.getName();
		this.userId = alert.getUser().getId();
		this.query = alert.getQuery();
		this.reservedKey = alert.getReservedKey();
		this.reservedType = alert.getReservedType();
		this.reservedId = alert.getReservedId();
		this.resrevedLink = alert.getReservedLink();
		this.eventTypeId = alert.getEventType().getId();
		this.eventGroupId = alert.getEventGroup().getId();
		this.layerId = alert.getLayer().getId();
	}
	
	public static AlertItem newInstanceForLog(Alert alert) {
		return new AlertItem(alert);
	}
	
	
	
	
	
	
	
//	//bunu silme
//	public AlertItem(Integer id, String title, String spot, String description, 
//			Date eventDate, Integer eventTypeId, String eventTypeName, String eventTypeImage, String eventTypeCode,
//			String countryName,String cityName, Double latitude, Double longitude, String blackListTag,
//			Integer eventGroupId, String eventGroupName, Integer stateId,
//			String reservedKey, String reservedType, String reservedId, String reservedLink, Integer userId, Integer groupId, Integer layerId,
//			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5) {
//		
//		this.id = id;
//		this.city = cityName;
//		this.country = countryName;
//		this.eventTypeId = eventTypeId;
//		this.eventTypeName = eventTypeName;
//		this.eventTypeImage =  eventTypeImage;
//		this.eventTypeCode =  eventTypeCode;
//		this.title = title;
//		this.spot = spot;
//		this.description = description;
//		this.eventDate = eventDate;
//		this.latitude = latitude;
//		this.longitude = longitude;
//		this.blackListTag = blackListTag;
//		this.eventGroupId = eventGroupId;
//		this.eventGroupName = eventGroupName;
//		this.stateId = stateId;
//		this.reservedKey = reservedKey;
//		this.reservedType = reservedType;
//		this.reservedId = reservedId;
//		this.reservedLink = reservedLink;
//		this.eventDateStr = DateUtils.formatWithCurrentLocale(this.eventDate);
//		this.userId = userId;
//		this.groupId = groupId;
//		
//		this.state = StateE.getIntegerStateToBoolean(stateId); 
//		
//		this.layerId = layerId;
//		
//		this.reserved1 = reserved1;
//		this.reserved2 = reserved2;
//		this.reserved3 = reserved3;
//		this.reserved4 = reserved4;
//		this.reserved5 = reserved5;
//	}
//	
//	
//	public AlertItem(Event event) {
//		
//		this.id = event.getId();
//		this.city = event.getCity();
//		
//		this.country = event.getCountry();
//		this.eventTypeId = event.getEventType().getId();
//		this.eventTypeName = event.getEventType().getName();
//		//this.eventTypeImage = event.getEventType().getImage();
//		this.eventTypeImage = event.getEventType().getCode();
//		
//		this.eventGroupId = event.getEventGroup().getId();
//		this.eventGroupName = event.getEventGroup().getName();
//		this.eventGroupColor = event.getEventGroup().getColor();
//		
//		this.layerId = event.getEventGroup().getLayer().getId();
//		this.layerName = event.getEventGroup().getLayer().getName();
//		this.title = event.getTitle();
//		this.spot = event.getSpot();
//		this.description = event.getDescription();
//		this.eventDate = event.getEventDate();
//		this.latitude = event.getLatitude();
//		this.longitude = event.getLongitude();
//		this.blackListTag = event.getBlackListTag();
//		this.createUser = event.getCreateUser();
//		
//		this.reservedKey = event.getReservedKey();
//		this.reservedType = event.getReservedType();
//		this.reservedId = event.getReservedId();
//		this.reservedLink = event.getReservedLink();
//		this.eventDateStr = DateUtils.formatWithCurrentLocale(this.eventDate);
//		
//		this.stateId = event.getState().getId();
//		
//		this.state = StateE.getIntegerStateToBoolean(event.getState().getId());
//		
//		this.userId = event.getUserId();
//		this.groupId = event.getGroupId();
//		
//		this.layerId = event.getEventGroup().getLayer().getId();
//		
//		this.reserved1 = event.getReserved1();
//		this.reserved2 = event.getReserved2();
//		this.reserved3 = event.getReserved3();
//		this.reserved4 = event.getReserved4();
//		this.reserved5 = event.getReserved5();
//	}
//	
//	public AlertItem(Integer id, Integer stateId) {
//		this.id = id;
//		this.stateId = stateId;
//	}
//	
//	
//	public static AlertItem newInstanceForLog(Event event) {
//		
//		return new AlertItem(event);
//	}
}
