package com.imst.event.map.admin.vo;

import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.hibernate.entity.EventType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventTypeItem {
	
	private Integer id;
	private String name;
	private String image;
	private String code;
	private String pathData;
	
	public EventTypeItem() {
	
	}
	
	public EventTypeItem(Integer id, String name, String image, String code, String pathData) {
		
		this.id = id;
		this.name = name;
		this.image = image;
		this.code = code;
		this.pathData =  pathData;
	}
	
	public EventTypeItem(EventType eventType) {
		
		this.id = eventType.getId();
		this.name = eventType.getName();
		this.image = eventType.getImage();
		this.code = eventType.getCode();
		this.pathData = eventType.getPathData();
	}
	
	public EventTypeItem(EventTypeProjection eventTypeProjection) {
		
		this.id = eventTypeProjection.getId();
		this.name = eventTypeProjection.getName();
		this.code = eventTypeProjection.getCode();
		this.image = eventTypeProjection.getImage();
	}
	
	public static EventTypeItem newInstanceForLog(EventType eventType) {
		
		EventTypeItem eventTypeItem = new EventTypeItem(eventType);
		
		return eventTypeItem;
	}
}
