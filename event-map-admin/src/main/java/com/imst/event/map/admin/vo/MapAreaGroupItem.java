package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.MapAreaGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapAreaGroupItem {
	
	private Integer id;
	private String name;
	private String color;
	private Integer layerId;
	private String layerName;
	
	public MapAreaGroupItem() {
	}
	
	public MapAreaGroupItem(Integer id, String name, String color, Integer layerId, String layerName) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.layerId = layerId;
		this.layerName = layerName;
	}
	
	public MapAreaGroupItem(MapAreaGroup mapAreaGroup) {
		
		this.id = mapAreaGroup.getId();
		this.name = mapAreaGroup.getName();
		this.color = mapAreaGroup.getColor();
		this.layerId = mapAreaGroup.getLayer().getId();
		this.layerName = mapAreaGroup.getLayer().getName();
	}
	
	public static MapAreaGroupItem newInstanceForLog(MapAreaGroup mapAreaGroup) {
		
		MapAreaGroupItem mapAreaGroupItem = new MapAreaGroupItem(mapAreaGroup);
		
		return mapAreaGroupItem;
	}
}
