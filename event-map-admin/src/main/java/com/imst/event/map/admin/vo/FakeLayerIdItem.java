package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.FakeLayerId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeLayerIdItem {
	
	private Integer id;
	private String roleId;
	private Integer layerId;
	private String layerName;


	public FakeLayerIdItem() {
	
	}
	
	public FakeLayerIdItem(Integer id, String roleId, Integer layerId, String layerName) {
		
		this.id = id;
		this.roleId = roleId;
		this.layerId = layerId;
		this.layerName = layerName;
	
	}
	
	public FakeLayerIdItem(FakeLayerId fakeLayerId) {
		this.id = fakeLayerId.getId();
		this.roleId = fakeLayerId.getRoleId();
		this.layerId = fakeLayerId.getLayer().getId();

	}
	
	public static FakeLayerIdItem newInstanceForLog(FakeLayerId fakeLayerId) {
		
		FakeLayerIdItem layerItem = new FakeLayerIdItem(fakeLayerId);
		
		return layerItem;
	}
}
