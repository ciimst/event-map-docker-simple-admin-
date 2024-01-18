package com.imst.event.map.admin.vo.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserItem {
	
	private Integer id;
	private String name;
	private String username;
	private Integer profileId;
	private String password;
	private Boolean state = true;
	private Boolean isDbUser = false;
	private Integer providerUserId;
	
	
	public ApiUserItem() {
	
	}
	
}
