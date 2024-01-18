package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventTagItem {
	
	private Integer tagId;
	private String tagName;
	
	public ApiEventTagItem() {
	
	}
	
	public ApiEventTagItem(Tag tag) {
		
		this.tagId = tag.getId();
		this.tagName = tag.getName();
	}
}
