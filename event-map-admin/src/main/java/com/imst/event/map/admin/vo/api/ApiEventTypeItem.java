package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.EventType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventTypeItem {
	
	private Integer id;
	private String name;
	private String image;
	private String code;
	private String pathData;
	
	public ApiEventTypeItem() {
	
	}
	
	public ApiEventTypeItem(EventType eventType) {
		
		this.id = eventType.getId();
		this.name = eventType.getName();
		this.image = eventType.getImage();
		this.code = eventType.getCode();
		this.pathData = eventType.getPathData();
	}
	
	public static ApiEventTypeItem newInstanceForLog(EventType eventType) {
		
		ApiEventTypeItem apiEventTypeItem = new ApiEventTypeItem(eventType);
		
		return apiEventTypeItem;
	}
	
}
