package com.imst.event.map.admin.vo;

import java.util.Date;

import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.DatabaseDump;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseDumpItem {
	
	private Integer id;
	private String name;
	private Date createDate;
	private String createDateStr;
	private String dumpSize;
	
	public DatabaseDumpItem() {
	}	
	
	public DatabaseDumpItem(Integer id, String name, Date createDate, String dumpSize) {
		this.id = id;
		this.name = name;
		this.createDate = createDate;
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.dumpSize = dumpSize;
	}
	
	public DatabaseDumpItem(DatabaseDump databaseDump) {
		
		this.id = databaseDump.getId();
		this.name = databaseDump.getName();
		this.createDate = databaseDump.getCreateDate();
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.dumpSize = databaseDump.getDumpSize();
	}
	
	public static DatabaseDumpItem newInstanceForLog(DatabaseDump databaseDump) {
		
		DatabaseDumpItem databaseDumpItem = new DatabaseDumpItem(databaseDump);
		
		return databaseDumpItem;
	}
}
