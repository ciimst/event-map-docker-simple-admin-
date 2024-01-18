package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.UserGroupId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserGroupIdItem {
	private Integer id;
	private Integer groupId;
	private Integer fk_userId;
	
	
	public ApiUserGroupIdItem() {
		
	}
	public ApiUserGroupIdItem(UserGroupId userGroupId) {
		this.id = userGroupId.getId();
		this.groupId = userGroupId.getGroupId();
		this.fk_userId =  userGroupId.getUser().getId();
	
	}
	
	public static ApiUserGroupIdItem newInstanceForLog(UserGroupId userGroupId) {
		
		return new ApiUserGroupIdItem(userGroupId);
	}
}
