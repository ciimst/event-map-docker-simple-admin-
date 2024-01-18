package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.UserGroupId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupIdItem {

	private Integer id;
	private Integer groupId;
	private Integer userId;
	private String username;
	
	public UserGroupIdItem() {
		
	}
	
	public UserGroupIdItem(Integer id,Integer groupId,Integer userId, String username) {
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.username = username;
	}
	
	public UserGroupIdItem(UserGroupId userGroupId) {
		this.id = userGroupId.getId();
		this.groupId = userGroupId.getGroupId();
		this.userId = userGroupId.getUser().getId();
		this.username = userGroupId.getUser().getUsername();
	}
	
public static UserGroupIdItem newInstanceForLog(UserGroupId userGroupId) {
		
	UserGroupIdItem userGroupIdItem = new UserGroupIdItem(userGroupId);
		
		return userGroupIdItem;
	}
}
