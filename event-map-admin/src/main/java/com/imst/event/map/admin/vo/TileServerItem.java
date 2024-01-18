package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.TileServer;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileServerItem {
	
	private Integer id;
	private String name;
	private String url;
	private Date createDate;
	private Date updateDate;
	private Integer sortOrder;
	private Boolean state;
	
	public TileServerItem() {
	
	}
	
	public TileServerItem(Integer id, String name, String url, Date createDate, Date updateDate, Integer sortOrder, Boolean state) {
		
		this.id = id;
		this.name = name;
		this.url = url;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.sortOrder = sortOrder;
		this.state = state;
	}
	
	public TileServerItem(TileServer tileServer) {
		this.id = tileServer.getId();
		this.name = tileServer.getName();
		this.url = tileServer.getUrl();
		this.createDate = tileServer.getCreateDate();
		this.updateDate = tileServer.getUpdateDate();
		this.sortOrder = tileServer.getSortOrder();
		this.state = tileServer.getState();
	}
	
	public static TileServerItem newInstanceForLog(TileServer tileServer) {
		
		TileServerItem tileServerItem = new TileServerItem(tileServer);
		
		return tileServerItem;
	}
}
