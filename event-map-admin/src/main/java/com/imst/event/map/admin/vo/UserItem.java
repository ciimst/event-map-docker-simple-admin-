package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserItem {
	
	private Integer id;
	private String name;
	private String username;
	private Integer profileId;
	private String profileName;
	private String password;
	private Boolean isDbUser;
	private Integer providerUserId;
	
	private Boolean state;
	
	public UserItem() {
	
	}
	
	public UserItem(Integer id, String name, String username, Integer profileId, String profileName, Boolean state, Boolean isDbUser, Integer providerUserId) {
		
		this.id = id;
		this.name = name;
		this.username = username;
		this.profileId = profileId;
		this.profileName = profileName;
		
		this.state = state;
		this.isDbUser = isDbUser;
		this.providerUserId = providerUserId;
	}
	
	public UserItem(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.username = user.getUsername();
		this.profileId = user.getProfile().getId();
		
		this.state = user.getState();
		this.isDbUser = user.getIsDbUser();
		this.providerUserId = user.getProviderUserId();
	}
	
	public static UserItem newInstanceForLog(User user) {
		
		UserItem userItem = new UserItem(user);
		
		return userItem;
	}
}
