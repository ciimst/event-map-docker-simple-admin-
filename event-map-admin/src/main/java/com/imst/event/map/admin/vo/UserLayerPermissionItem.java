package com.imst.event.map.admin.vo;

import java.io.Serializable;

import com.imst.event.map.hibernate.entity.UserLayerPermission;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserLayerPermissionItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1545645645457454L;
	private Integer id;
	private Integer userId;
	private String userName;
	private Integer layerId;
	private String layerName;
	
	private Boolean hasFullPermission;

	
	public UserLayerPermissionItem() {
		
	}
	
	public UserLayerPermissionItem(Integer id,Integer layerId, String layerName,Integer userId,String userName) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.layerId = layerId;
		this.layerName = layerName;
		this.hasFullPermission = true;
	}
	
	public UserLayerPermissionItem(UserLayerPermission userLayerPermission) {
		this.id=userLayerPermission.getId();
		this.layerId=userLayerPermission.getLayer().getId();
		this.layerName=userLayerPermission.getLayer().getName();
		this.userId=userLayerPermission.getUser().getId();
		this.userName=userLayerPermission.getUser().getUsername();
		this.hasFullPermission = true;
	}
	
	public static UserLayerPermissionItem newInstanceForLog(UserLayerPermission userLayerPermission) {
		
		UserLayerPermissionItem userLayerPermissionItem = new UserLayerPermissionItem(userLayerPermission);
		
		return userLayerPermissionItem;
	}
	
}
