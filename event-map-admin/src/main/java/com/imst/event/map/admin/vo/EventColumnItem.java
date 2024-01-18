package com.imst.event.map.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventColumnItem {
	
	private Integer id;
	private String name;

	
	public EventColumnItem() {
	}
	
	public EventColumnItem(Integer id, String name) {
		this.id = id;
		this.name = name;
	
	}

	
}
