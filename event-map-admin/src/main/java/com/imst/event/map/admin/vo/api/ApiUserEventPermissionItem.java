package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserEventPermissionItem {
	
	private Integer id;
	private Integer userId;
	private Integer eventId;
	
	public ApiUserEventPermissionItem() {
	
	}
	
	public ApiUserEventPermissionItem(User user) {
		
		this.userId = user.getId();
	}
}
