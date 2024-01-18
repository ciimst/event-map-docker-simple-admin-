package com.imst.event.map.admin.vo;

import java.util.Date;

import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.GeoLayer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoLayerItem {

	private Integer id;
	private String name;
	private String data;
	private Integer layerId;
	private String layerName;
	private Date createDate;
	private Boolean state;
	
	private String createDateStr;
	
	public GeoLayerItem() {
		
	}
	
	public GeoLayerItem(Integer id,String name,String data,Date createDate,Integer layerId,String layerName, Boolean state) {
		this.id = id;
		this.name = name;
		this.data = data;
		this.createDate = createDate;
		this.layerId = layerId;
		this.layerName = layerName;
		this.state = state;
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		
	}
	public GeoLayerItem(GeoLayer geoLayer) {
		this.id = geoLayer.getId();
		this.name = geoLayer.getName();
		this.data = geoLayer.getData();
		this.createDate = geoLayer.getCreateDate();
		this.layerId = geoLayer.getLayer().getId();
		this.layerName = geoLayer.getLayer().getName();
		this.state = geoLayer.getState();
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		
	}
	
	public static GeoLayerItem newInstanceForLog(GeoLayer geoLayer) {
		GeoLayerItem geoLayerItem = new GeoLayerItem(geoLayer);
		return geoLayerItem;
	}
	
}
