package com.imst.event.map.admin.vo.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserSearchItem {
	
	private Integer id;
	private String name;
	private String username;
	private Integer profileId;
	private String profileName;
	private Integer providerUserId;
	
	public ApiUserSearchItem() {
	
	}
	
	public ApiUserSearchItem(Integer id, String name, String username, Integer profileId, String profileName, Integer providerUserId) {
		
		this.id = id;
		this.name = name;
		this.username = username;
		this.profileId = profileId;
		this.profileName = profileName;
		this.providerUserId = providerUserId;
	}
}
