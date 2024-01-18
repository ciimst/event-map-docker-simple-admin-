package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.UserUserId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserUserIdItem {
	private Integer id;
	private Integer userId;
	private Integer fk_userId;
	
	public ApiUserUserIdItem() {
		
	}
	public ApiUserUserIdItem(UserUserId userUserId) {
		this.id = userUserId.getId();
		this.userId = userUserId.getUserId();
		this.fk_userId = userUserId.getUser().getId();
		
	}
	
public static ApiUserUserIdItem newInstanceForLog(UserUserId userUserId) {
		
		return new ApiUserUserIdItem(userUserId);
	}
}
