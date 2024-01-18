package com.imst.event.map.admin.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.imst.event.map.admin.constants.MultitenantDatabaseE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.multitenant.conf.TenantContext;
import com.imst.event.map.admin.db.repositories.LayerExportRepository;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.admin.vo.mobile.EventItemMobile;

public class CallableTileExportEventService implements Callable<List<EventItemMobile>> {

	private MasterDao masterDao;
	@Autowired LayerExportRepository layerExportRepository;
	
	private String tenantName;
	private Date startDate;
	private Date finishDate;	
	private EventService eventService;
	private MultitenantDatabaseE multitenantDatabaseE;	
	private Integer layerId;

public CallableTileExportEventService(Date startDate, Date finishDate, DataSourceInfo dataSourceInfo, Integer layerId) {
		
		this.startDate = startDate;
		this.finishDate = finishDate;
		
		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
		
		this.layerId = layerId;
				
	}
	
	@Override
	public List<EventItemMobile> call() throws Exception{
		
		TenantContext.setCurrentTenant(this.tenantName);
		
		List<EventItemMobile> totalEventList = new ArrayList<>();
		List<EventItemMobile> maxLat  = eventService.getEventLatLong(masterDao, startDate, finishDate, Sort.by(Direction.DESC, "latitude") ,layerId);
		List<EventItemMobile> minLat  = eventService.getEventLatLong(masterDao, startDate, finishDate, Sort.by(Direction.ASC, "latitude") ,layerId);
		List<EventItemMobile> maxLong  = eventService.getEventLatLong(masterDao, startDate, finishDate, Sort.by(Direction.DESC, "longitude") ,layerId);
		List<EventItemMobile> minLong  = eventService.getEventLatLong(masterDao, startDate, finishDate, Sort.by(Direction.ASC, "longitude") ,layerId);
		
		totalEventList.addAll(maxLat);
		totalEventList.addAll(minLat);
		totalEventList.addAll(maxLong);
		totalEventList.addAll(minLong);
		
		return totalEventList;
	}

}
