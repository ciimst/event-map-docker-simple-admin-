package com.imst.event.map.admin.vo.mobile;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventItemMobile {
	
	
	private Integer id;
	
	private Integer eventGroupId;
	private Integer eventTypeId;
	private String title;
	private String spot;
	private String description;
	private Long eventDate; // long olmalı
	private String country;
	private String city;
	private Double latitude;
	private Double longitude;
	private String createUser;
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	private String dbName;
	
	private List<EventMediaItemMobile> eventMediaList;
	
	public EventItemMobile() {
	
	}

	public EventItemMobile(Integer id, Integer eventGroupId, Integer eventTypeId, String title, String spot,
			String description, Date eventDate, String country, String city, Double latitude, Double longitude,
			String createUser, String reservedKey, String reservedType, String reservedId, String reservedLink) {
		super();
		this.id = id;
		this.eventGroupId = eventGroupId;
		this.eventTypeId = eventTypeId;
		this.title = title;
		this.spot = spot;
		this.description = description;
		this.eventDate = eventDate == null ? null : eventDate.getTime();
		this.country = country;
		this.city = city;
		this.latitude = latitude;
		this.longitude = longitude;
		this.createUser = createUser;
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedLink = reservedLink;
	}
	
	
	

	
}
