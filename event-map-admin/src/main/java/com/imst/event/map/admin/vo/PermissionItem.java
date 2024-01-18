package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.Permission;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PermissionItem {
	
	
	private Integer id;
	private String name;
	private String description;
	private String groupName;
	private Integer displayOrder;
	private Boolean state;
	private Boolean selected;
	
	public PermissionItem() {
	
	}
	
	public PermissionItem(Permission permission) {
		
		this.id = permission.getId();
		this.name = permission.getName();
		this.description = permission.getDescription();
		this.groupName = permission.getGroupName();
		this.displayOrder = permission.getDisplayOrder();
		this.state = permission.getState();
		this.selected = false;
	}
	
}
