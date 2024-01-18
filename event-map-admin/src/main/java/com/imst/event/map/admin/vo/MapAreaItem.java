package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.MapArea;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapAreaItem {
	
	private Integer id;
	private Integer mapAreaGroupId;
	private String mapAreaGroupName;
	private String mapAreaGroupColor;
	private String title;
	private String coordinateInfo;
	private Boolean state;
	
	
	
	public MapAreaItem() {
	
	}
	
	public MapAreaItem(Integer id, Integer mapAreaGroupId,
					   String mapAreaGroupName, String mapAreaGroupColor,
					   String title, String coordinateInfo, Boolean state) {
		
		this.id = id;
		this.mapAreaGroupId = mapAreaGroupId;
		this.mapAreaGroupName = mapAreaGroupName;
		this.mapAreaGroupColor = mapAreaGroupColor;
		this.title = title;
		this.coordinateInfo = coordinateInfo;
		this.state = state;
	}
	
	public MapAreaItem(MapArea mapArea) {
	
		this.id = mapArea.getId();
		this.mapAreaGroupId = mapArea.getMapAreaGroup().getId();
		this.title = mapArea.getTitle();
		this.coordinateInfo = mapArea.getCoordinateInfo();
		this.state = mapArea.getState();
	}
	
	
	public static MapAreaItem newInstanceForLog(MapArea mapArea) {
		
		MapAreaItem mapAreaGroupItem = new MapAreaItem(mapArea);
		
		return mapAreaGroupItem;
	}
}
