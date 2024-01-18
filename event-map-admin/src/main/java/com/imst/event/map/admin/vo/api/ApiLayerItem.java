package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.Layer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiLayerItem {
	
	private Integer id;
	private String name;
	private Boolean state = true;
	private Boolean isTemp = false;
	
	public ApiLayerItem() {
	
	}
	
	public ApiLayerItem(Layer layer) {
		
		this.id = layer.getId();
		this.name = layer.getName();
		this.state = layer.getState();
		this.isTemp = layer.getIsTemp();
	}
	
	public static ApiLayerItem newInstanceForLog(Layer layer) {
		
		ApiLayerItem apiLayerItem = new ApiLayerItem(layer);
		
		return apiLayerItem;
	}
}
