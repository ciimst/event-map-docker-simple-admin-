package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.UserUserId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUserIdItem {

	private Integer id;
	private Integer userId;
	private Integer fk_userId;
	private String fk_username;
	
	public UserUserIdItem() {
		
	}
	
	public UserUserIdItem(Integer id,Integer userId,Integer fk_userId, String fk_username) {
		this.id = id;
		this.userId = userId;
		this.fk_userId = fk_userId;
		this.fk_username = fk_username;
	}
	
	public UserUserIdItem(UserUserId userUserId) {
		this.id = userUserId.getId();
		this.userId = userUserId.getUserId();
		this.fk_userId = userUserId.getUser().getId();
		this.fk_username = userUserId.getUser().getUsername();
	}
	
public static UserUserIdItem newInstanceForLog(UserUserId userUserId) {
		
	UserUserIdItem userUserIdItem = new UserUserIdItem(userUserId);
		
		return userUserIdItem;
	}
}
