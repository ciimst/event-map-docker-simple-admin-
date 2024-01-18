package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagItem {
	
	private Integer id;
	private String name;
	
	public TagItem() {
	}
	
	public TagItem(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public TagItem(Tag tag) {
		
		this.id = tag.getId();
		this.name = tag.getName();
	}
	
	public static TagItem newInstanceForLog(Tag tag) {
		
		TagItem tagItem = new TagItem(tag);
		
		return tagItem;
	}
}
