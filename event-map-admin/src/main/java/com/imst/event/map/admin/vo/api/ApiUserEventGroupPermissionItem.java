package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserEventGroupPermissionItem {

	private Integer id;
	private Integer fk_userId;
	private Integer fk_eventGroupId;
	
	public ApiUserEventGroupPermissionItem() {
		
	}
	
	public ApiUserEventGroupPermissionItem(UserEventGroupPermission userEventGroupPermission) {
		
		this.id = userEventGroupPermission.getId();
		this.fk_userId = userEventGroupPermission.getUser().getId();
		this.fk_eventGroupId = userEventGroupPermission.getEventGroup().getId();
	}
	
public static ApiUserEventGroupPermissionItem newInstanceForLog(UserEventGroupPermission userEventGroupPermission) {
		
		return new ApiUserEventGroupPermissionItem(userEventGroupPermission);
	}
}
