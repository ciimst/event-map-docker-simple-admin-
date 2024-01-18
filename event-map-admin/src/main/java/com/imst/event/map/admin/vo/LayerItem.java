package com.imst.event.map.admin.vo;

import java.util.Date;
import java.util.List;

import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.Layer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayerItem {
	
	private Integer id;
	private String name;
	private Date createDate;
	private Date updateDate;
	private Boolean state;
	private Boolean isTemp;
	private String guid;
	
	List<EventGroupItem> eventGroups;
	List<MapAreaGroupItem> mapAreaGroups;
	
	private String createDateStr;
	private String updateDateStr;
	
	public LayerItem() {
	
	}
	
	public LayerItem(Integer id, String name, Date createDate, Date updateDate, Boolean state, Boolean isTemp, String guid) {
		
		this.id = id;
		this.name = name;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.state = state;
		this.isTemp = isTemp;
		this.guid = guid;
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
	}
	
	public LayerItem(Layer layer) {
		this.id = layer.getId();
		this.name = layer.getName();
		this.createDate = layer.getCreateDate();
		this.updateDate = layer.getUpdateDate();
		this.state = layer.getState();
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		this.isTemp = layer.getIsTemp();
		this.guid = layer.getGuid();
	}
	
	public static LayerItem newInstanceForLog(Layer layer) {
		
		LayerItem layerItem = new LayerItem(layer);
		
		return layerItem;
	}
}
