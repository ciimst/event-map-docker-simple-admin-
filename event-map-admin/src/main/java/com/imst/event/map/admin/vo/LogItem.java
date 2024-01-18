package com.imst.event.map.admin.vo;

import java.util.Date;

import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.EntitySortKey;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogItem {
	
	private Integer id;
	private String username;
	private Integer userId;
	private String ip;
	private String description;
	private Date createDate;
	private Integer fkLogTypeId;
	@EntitySortKey(sortable = false)
	private String logTypeName;
	private Integer uniqueId;
	private String searchableDescription;
	
	private String createDateStr;
	
	public LogItem() {
	
	}
	
	public LogItem(Integer id, String username, Integer userId, String ip, String description, Date createDate, Integer fkLogTypeId, Integer uniqueId) {
		
		this.id = id;
		this.username = username;
		this.userId = userId;
		this.ip = ip;
		this.description = description;
		this.createDate = createDate;
		this.fkLogTypeId = fkLogTypeId;
		this.logTypeName = LogTypeE.getSetting(fkLogTypeId).getName();
		this.uniqueId = uniqueId;
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
	}
	
	public LogItem(Log log) {
		
		this.id = log.getId();
		this.username = log.getUsername();
		this.userId = log.getUserId();
		this.ip = log.getIp();
		this.description = log.getDescription();
		this.createDate = log.getCreateDate();
		this.fkLogTypeId = log.getFkLogTypeId();
		this.logTypeName = LogTypeE.getSetting(fkLogTypeId).getName();
		this.uniqueId = log.getUniqueId();
		this.searchableDescription = log.getSearchableDescription();
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
	}
}
