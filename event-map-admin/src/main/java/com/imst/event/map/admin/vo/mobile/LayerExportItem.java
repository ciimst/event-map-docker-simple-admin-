package com.imst.event.map.admin.vo.mobile;

import java.util.Date;

import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.LayerExport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayerExportItem {

	private Integer id;
	private Integer minZ;
	private Integer maxZ;
	private Date createDate;
	private Date startDate;
	private Date finishDate;
	private Integer layerId;
	private String layerName;

	private String createDateStr;
	private String startDateStr;
	private String finishDateStr;
	
	private Date eventCreateDate;
	private String eventCreateDateStr;
	
	private Date tileCreateDate;
	private String tileCreateDateStr;
	private String name;
	
	private Integer tileServerId;
	private String tileServerName;
	private String tileServerUrl;
	
	private Integer eventExportCount;
	
	public LayerExportItem() {
		
	}
	
	public LayerExportItem(Integer id, Integer minZ, Integer maxZ, Date createDate,  Date startDate, Date finishDate, Integer layerId, String layerName, Date eventCreateDate, Date tileCreateDate, String name,
			Integer tileServerId, String tileServerName, String tileServerUrl, Integer eventExportCount) {
		this.id = id;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.createDate = createDate;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.layerId = layerId;
		this.layerName = layerName;
		this.eventCreateDate = eventCreateDate;
		this.tileCreateDate = tileCreateDate;
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.startDateStr = DateUtils.formatWithCurrentLocale(this.startDate);
		this.finishDateStr = DateUtils.formatWithCurrentLocale(this.finishDate);
		this.eventCreateDateStr = DateUtils.formatWithCurrentLocale(this.eventCreateDate);
		this.tileCreateDateStr = DateUtils.formatWithCurrentLocale(this.tileCreateDate);
		this.name = name;
		
		this.tileServerId = tileServerId;
		this.tileServerName = tileServerName;
		this.tileServerUrl = tileServerUrl;
		this.eventExportCount = eventExportCount;
	}
	
	public LayerExportItem(LayerExport layerExport) {
		this.id = layerExport.getId();
		this.minZ = layerExport.getMinZ();
		this.maxZ = layerExport.getMaxZ();
		this.createDate = layerExport.getCreateDate();
		this.startDate = layerExport.getStartDate();
		this.finishDate = layerExport.getFinishDate();
		this.layerId = layerExport.getLayer().getId();
		this.layerName = layerExport.getLayer().getName();
		this.eventCreateDate = layerExport.getEventCreateDate();
		this.tileCreateDate = layerExport.getTileCreateDate();
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.startDateStr = DateUtils.formatWithCurrentLocale(this.startDate);
		this.finishDateStr = DateUtils.formatWithCurrentLocale(this.finishDate);
		this.eventCreateDateStr = DateUtils.formatWithCurrentLocale(this.eventCreateDate);
		this.tileCreateDateStr = DateUtils.formatWithCurrentLocale(this.tileCreateDate);
		this.name = layerExport.getName();
		
		this.tileServerId = layerExport.getTileServer().getId();
		this.tileServerName = layerExport.getTileServer().getName();
		this.tileServerUrl = layerExport.getTileServer().getUrl();
		
		this.eventExportCount = layerExport.getEventExportCount();
	}
}
