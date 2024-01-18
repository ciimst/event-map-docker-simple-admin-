package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.EventLink;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventLinkItem {
	
	private Integer id;
	private String displayName;
	private String link;
	private Integer eventColumnId;
	private String color = "#000000";

	
	public ApiEventLinkItem() {
		
	}


	public ApiEventLinkItem(Integer id, String displayName, String link, Integer eventColumnId, String color) {
		this.id = id;
		this.displayName = displayName;
		this.link = link;
		this.eventColumnId = eventColumnId;
		this.color = color;
	}
	
	public ApiEventLinkItem(EventLink eventLink) {
		this.id = eventLink.getId();
		this.displayName = eventLink.getDisplayName();
		this.link = eventLink.getLink();
		this.eventColumnId = eventLink.getEventColumn().getId();
		this.color = eventLink.getColor();
	}
	
	

	
}
