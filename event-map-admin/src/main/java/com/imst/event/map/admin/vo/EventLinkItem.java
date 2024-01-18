package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.EventLink;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventLinkItem {
	
	private Integer id;
	private String link;
	private Integer eventColumnId;
	private String eventColumnName;
	private String displayName;
	private String color;
	
	public EventLinkItem() {
	}
	
	public EventLinkItem(Integer id, String link, Integer eventColumnId, String eventColumnName, String displayName, String color) {
		this.id = id;
		this.link = link;
		this.eventColumnId = eventColumnId;
		this.eventColumnName = eventColumnName;	
		this.displayName = displayName;
		this.color = color;
	}
	
	// Veritabanı kullanıyor değiştirilmemeli
	public EventLinkItem(EventLink eventLink) {
		
		this.id = eventLink.getId();
		this.link = eventLink.getLink();
		this.eventColumnId = eventLink.getEventColumn().getId();
		this.eventColumnName = eventLink.getEventColumn().getName();
		this.displayName = eventLink.getDisplayName();
		this.color = eventLink.getColor();
	
	}
	
	public static EventLinkItem newInstanceForLog(EventLink eventLink) {
		
		EventLinkItem eventLinkItem = new EventLinkItem(eventLink);
		
		return eventLinkItem;
	}

	
}
