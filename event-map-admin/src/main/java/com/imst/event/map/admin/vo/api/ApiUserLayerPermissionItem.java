package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.UserLayerPermission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserLayerPermissionItem {

	private Integer id;
	private Integer fk_userId;
	private Integer fk_layerId;
	
	public ApiUserLayerPermissionItem() {
		
	}
	
	public ApiUserLayerPermissionItem(UserLayerPermission userLayerPermission) {
		
		this.id = userLayerPermission.getId();
		this.fk_userId = userLayerPermission.getUser().getId();
		this.fk_layerId = userLayerPermission.getLayer().getId();
	}
	
public static ApiUserLayerPermissionItem newInstanceForLog(UserLayerPermission userLayerPermission) {
		
		return new ApiUserLayerPermissionItem(userLayerPermission);
	}
}
