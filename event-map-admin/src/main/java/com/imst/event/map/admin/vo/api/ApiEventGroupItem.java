package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.EventGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventGroupItem {
	
	private Integer id;
	private String name;
	private String color;
	private ApiLayerItem layerItem;
	
	public ApiEventGroupItem() {
	
	}
	
	public ApiEventGroupItem(EventGroup eventGroup) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerItem = new ApiLayerItem(eventGroup.getLayer());
	}
	
	public static ApiEventGroupItem newInstanceForLog(EventGroup eventGroup) {
		
		ApiEventGroupItem item = new ApiEventGroupItem(eventGroup);
		
		return item;
	}
}
