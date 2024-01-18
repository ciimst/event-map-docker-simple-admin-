package com.imst.event.map.admin.services;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.imst.event.map.admin.constants.MultitenantDatabaseE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.multitenant.conf.TenantContext;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.admin.vo.mobile.EventGroupItemMobile;

public class CallableEventGroupService implements Callable<List<EventGroupItemMobile>>{

	private Integer layerId;
	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	@Autowired EventGroupRepository eventGroupRepository;
	private EventService eventService;
	private MasterDao masterDao;
	
	public CallableEventGroupService(Integer layerId, DataSourceInfo dataSourceInfo) {
		
		this.layerId = layerId;
		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		this.eventGroupRepository = ApplicationContextUtils.getBean(EventGroupRepository.class);	
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();		
	}
	
	@Override
	public List<EventGroupItemMobile> call() throws Exception{
		
		TenantContext.setCurrentTenant(this.tenantName);
		
		List<EventGroupItemMobile> eventGroupList = eventService.generateEventGroupData(masterDao, Sort.by("id"), layerId);

		for(EventGroupItemMobile eventGroupItemMobile : eventGroupList) {
			eventGroupItemMobile.setDbName(tenantName);
		}
		
		return eventGroupList;
		
	}
}
