package com.imst.event.map.admin.vo.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiGeoLayerItem {
	
	private Integer id;
	private String name;
	private String data;
	private Integer layerId;
	private Boolean state = true;
	
	public ApiGeoLayerItem() {
	
	}
	
}
