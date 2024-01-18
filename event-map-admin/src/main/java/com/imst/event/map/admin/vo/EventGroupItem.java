package com.imst.event.map.admin.vo;

import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.hibernate.entity.EventGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventGroupItem {
	
	private Integer id;
	private String name;
	private String color;
	private Integer layerId;
	private String layerName;
	private Integer parentId;
	private String parentName;
	private String description;
	
	public EventGroupItem() {
	}
	
	public EventGroupItem(Integer id, String name, String color, Integer layerId, String layerName, Integer parentId, String description) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.layerId = layerId;
		this.layerName = layerName;
		this.parentId = parentId;
		this.description = description;
	}
	
	// Veritabanı kullanıyor değiştirilmemeli
	public EventGroupItem(EventGroup eventGroup) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer().getId();
		this.parentId = eventGroup.getParentId();
		this.description = eventGroup.getDescription();
	}
	
	public EventGroupItem(EventGroup eventGroup, Boolean isNotDb) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer().getId();
		this.layerName = eventGroup.getLayer().getName();
		this.parentId = eventGroup.getParentId();
		this.description = eventGroup.getDescription();
	}
	
	public static EventGroupItem newInstanceForLog(EventGroup eventGroup) {
		
		EventGroupItem eventGroupItem = new EventGroupItem(eventGroup, false);
		
		return eventGroupItem;
	}
	
	public EventGroupItem(EventGroupProjection eventGroupProjection) {
		
		this.id = eventGroupProjection.getId();
		this.name = eventGroupProjection.getName();
		this.color = eventGroupProjection.getColor();
		this.layerId = eventGroupProjection.getLayerId();
		this.layerName = eventGroupProjection.getLayerName();
		this.parentId = eventGroupProjection.getParentId();
		this.description = eventGroupProjection.getDescription();
		
		
	}
	
	
}
