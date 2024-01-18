package com.imst.event.map.admin.vo;

import java.io.Serializable;

import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserEventGroupPermissionItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 17874545454548545L;
	private Integer id;
	private Integer userId;
	private String userName;
	private Integer layerId;
	private String layerName;
	private Integer eventGroupId;
	private String eventGroupName;

	
	public UserEventGroupPermissionItem() {
		
	}
	
	public UserEventGroupPermissionItem(Integer id, Integer layerId, String layerName, Integer eventGroupId, String eventGroupName, Integer userId, String userName) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.layerId = layerId;
		this.layerName = layerName;
		this.eventGroupId = eventGroupId;
		this.eventGroupName = eventGroupName;
	}
	
	public UserEventGroupPermissionItem(UserEventGroupPermission userEventGroupPermission) {
		this.id=userEventGroupPermission.getId();	
		this.eventGroupId=userEventGroupPermission.getEventGroup().getId();
		this.eventGroupName=userEventGroupPermission.getEventGroup().getName();
		this.userId=userEventGroupPermission.getUser().getId();
		this.userName=userEventGroupPermission.getUser().getUsername();
		this.layerId = userEventGroupPermission.getEventGroup().getLayer().getId();
		this.layerName = userEventGroupPermission.getEventGroup().getLayer().getName();
	}
	
	public static UserEventGroupPermissionItem newInstanceForLog(UserEventGroupPermission userEventGroupPermission) {
		
		UserEventGroupPermissionItem userEventGroupPermissionItem = new UserEventGroupPermissionItem(userEventGroupPermission);
		
		return userEventGroupPermissionItem;
	}
	
}
