package com.imst.event.map.admin.vo.api;

import java.util.List;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.hibernate.entity.Event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventItem {
	
	private Integer id;
	private Double latitude;
	private Double longitude;
	
	private String blackListTag;

	private boolean state = true;
	
	private Integer groupId;
	private Integer userId;
	
	private String title;
	private String spot;
	private String description;
	private Long eventDate;
	
	private String city;
	private String country;
	
	private ApiEventTypeItem eventTypeItem;
	private ApiEventGroupItem eventGroupItem;
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	
	private String reserved1;
	private String reserved2;
	private String reserved3;
	private String reserved4;
	private String reserved5;

	private List<ApiUserEventPermissionItem> userEventPermissions;
	private List<ApiEventMediaItem> eventMedias;
	private List<ApiEventTagItem> eventTags;
	
	
	public ApiEventItem() {
	
	}
	
	public ApiEventItem(Event event) {
		
		this.id = event.getId();
		this.latitude = event.getLatitude();
		this.longitude = event.getLongitude();
		this.blackListTag = event.getBlackListTag();
		this.state = StateE.getIntegerStateToBoolean(event.getState().getId());
		this.groupId = event.getGroupId();
		this.userId = event.getUserId();
		this.title = event.getTitle();
		this.spot = event.getSpot();
		this.description = event.getDescription();
		this.eventDate = event.getEventDate().getTime();
		this.city = event.getCity();
		this.country = event.getCountry();
		this.eventTypeItem = new ApiEventTypeItem(event.getEventType());
		this.eventGroupItem = new ApiEventGroupItem(event.getEventGroup());
		this.reservedKey = event.getReservedKey();
		this.reservedType = event.getReservedType();
		this.reservedId = event.getReservedId();
		this.reserved1 = event.getReserved1();
		this.reserved2 = event.getReserved2();
		this.reserved3 = event.getReserved3();
		this.reserved4 = event.getReserved4();
		this.reserved5 = event.getReserved5();
	}
	
	public static ApiEventItem newInstanceForLog(Event event) {
		
		return new ApiEventItem(event);
	}
	
	
}
