package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiTagItem {
	
	private Integer id;
	private String name;
	
	public ApiTagItem() {
	
	}
	
	public ApiTagItem(Tag tag) {
		
		this.id = tag.getId();
		this.name = tag.getName();
	}
	
	public static ApiTagItem newInstanceForLog(Tag tag) {
		
		ApiTagItem apiLayerItem = new ApiTagItem(tag);
		
		return apiLayerItem;
	}
}
