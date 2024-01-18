package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.FakeLayerId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiFakeLayerIdItem {
	
	private Integer id;
	private String roleId;
	private Integer layerId;
	
	public ApiFakeLayerIdItem() {
	
	}
	
	public ApiFakeLayerIdItem(FakeLayerId fakeLayerId) {
		
		this.id = fakeLayerId.getId();
		this.roleId = fakeLayerId.getRoleId();
		this.layerId = fakeLayerId.getLayer().getId();
	}
	
	public static ApiFakeLayerIdItem newInstanceForLog(FakeLayerId fakeLayerId) {
		
		return new ApiFakeLayerIdItem(fakeLayerId);
	}
}
