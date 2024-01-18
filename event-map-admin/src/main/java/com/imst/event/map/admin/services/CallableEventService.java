package com.imst.event.map.admin.services;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.imst.event.map.admin.constants.MultitenantDatabaseE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.multitenant.conf.TenantContext;
import com.imst.event.map.admin.db.repositories.LayerExportRepository;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.admin.vo.mobile.EventItemMobile;

public class CallableEventService  implements Callable<List<EventItemMobile>>{
	
	private MasterDao masterDao;
	@Autowired LayerExportRepository layerExportRepository;
	
	private String tenantName;
	private Date startDate;
	private Date finishDate;
	private Integer layerId;
	private EventService eventService;
	private MultitenantDatabaseE multitenantDatabaseE;	
	private Integer lastEventId;

	
	public CallableEventService(Date startDate, Date finishDate, Integer layerId, DataSourceInfo dataSourceInfo, Integer layerExportId, Integer lastEventId) {
		
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.layerId = layerId;
		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
		this.lastEventId = lastEventId;
	}
	
	@Override
	public List<EventItemMobile> call() throws Exception{
		
		TenantContext.setCurrentTenant(this.tenantName);
			
		List<EventItemMobile> eventList  = eventService.generateEventData(masterDao, layerId, startDate, finishDate, lastEventId);
		eventList.stream().sorted(Comparator.comparingInt(EventItemMobile::getId)).collect(Collectors.toList());
		
		for(EventItemMobile eventItemMobile : eventList) {
			eventItemMobile.setDbName(tenantName);
		}		
	
		return eventList;	
	}
	
}
