package com.imst.event.map.admin.vo.mobile;

import java.util.Date;

import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.TileExport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileExportItem {

	private Integer id;
	private String name;
	private Integer minZ;
	private Integer maxZ;
	private Date createDate;
	private String createDateStr;
	private Double maxLat;
	private Double minLat;
	private Double maxLong;
	private Double minLong;
	private Integer tileServerId;
	private String tileServerName;
	private String tileServerUrl;
	

	public TileExportItem() {
		
	}
	
	public TileExportItem(Integer id, String name, Integer minZ, Integer maxZ, Date createDate, double maxLat, double minLat, double maxLong, double minLong,
			Integer tileServerId, String tileServerName, String tileServerUrl) {
		this.id = id;
		this.name = name;
		this.minZ = minZ;
		this.maxZ = maxZ;	
		this.createDate = createDate;
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.maxLat = maxLat;
		this.minLat = minLat;
		this.maxLong = maxLong;
		this.minLong = minLong;
		this.tileServerId = tileServerId;
		this.tileServerName = tileServerName;
		this.tileServerUrl = tileServerUrl;
		
	}
	
	public TileExportItem(TileExport tileExport) {
		this.id = tileExport.getId();
		this.name = tileExport.getName();
		this.minZ = tileExport.getMinZ();
		this.maxZ = tileExport.getMaxZ();
		this.createDate = tileExport.getCreateDate();
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		
		this.maxLat = tileExport.getMaxLat();
		this.minLat = tileExport.getMinLat();
		this.maxLong = tileExport.getMaxLong();
		this.minLong = tileExport.getMinLong();
		
		this.tileServerId = tileExport.getTileServer().getId();
		this.tileServerName = tileExport.getTileServer().getName();
		this.tileServerUrl = tileExport.getTileServer().getUrl();
			
	}
	
}
