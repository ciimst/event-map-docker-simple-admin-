package com.imst.event.map.admin.vo.mobile;

import com.imst.event.map.hibernate.entity.EventGroup;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventGroupItemMobile {
	
	private Integer id;
	private String name;
	private String color;
	private Integer layerId;	
	private String dbName;
		
	public EventGroupItemMobile() {
	}
		
	public EventGroupItemMobile(Integer id, String name, String color, Integer layerId) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.layerId = layerId;
		
	}
	
	public EventGroupItemMobile(EventGroup eventGroup) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer().getId();
	
	}
		
}
